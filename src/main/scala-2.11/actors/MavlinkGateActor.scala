package actors

import java.net.InetSocketAddress

import actors.Messages._
import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Put, Publish}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import org.mavlink.MAVLinkReader
import org.mavlink.messages.MAVLinkMessage
import utils.{MessageBatcher, ConfigProvider}
import scala.concurrent.duration._

/**
 * Created by max on 10/28/15.
 */

class MavlinkGateActor extends Actor with Stash with ActorLogging with ConfigProvider {

  import Tcp._
  import context.system
  import context.dispatcher
  import scala.collection.convert.wrapAll._

  val mavlinkPort = config.getInt("mavlink.port")
  val mavlinkHost = config.getString("mavlink.host")
  val mavlinkTypeFilter = {
    if (config.hasPath("mavlink.type-filter"))
      config.getIntList("mavlink.type-filter").toSet
    else
      Set.empty[Int]
  }

  val isRawTelemetryAllowed = {
    if (config.hasPath("mavlink.raw-telemetry"))
      config.getBoolean("mavlink.raw-telemetry")
    else
      false
  }

  val mediator = DistributedPubSub(context.system).mediator

  mediator ! Put(self)

  val memberId = config.getString("member-id")

  val remote = new InetSocketAddress(mavlinkHost, mavlinkPort)
  val reader = new MavLinkStreamReader()

  val rawBatcher = new MessageBatcher[TelemetryRaw](config.getConfig("buffer.raw"))(log)(batch => listeners.foreach(_ ! batch))

  val mlBatcher = new MessageBatcher[MavLinkTelemetry](config.getConfig("buffer.ml"))(log)({ batch =>
    mediator ! Publish(memberId, batch)
    listeners.foreach(_ ! batch)
  })

  @volatile var listeners = Set.empty[ActorRef]

  println(s"Telemetry gate for '$memberId'")

  //telemetryDispatcher ! DevDiscover

  IO(Tcp) ! Connect(remote)

  def receive = {
    case CommandFailed(_: Connect) =>
      log.error("Connect to mavlink failed " + remote + ". Trying to reconnect in one second")
      system.scheduler.scheduleOnce(1 seconds, IO(Tcp), Connect(remote))

    case c @ Connected(remote, local) =>

      val connection = sender()
      connection ! Register(self)
      unstashAll()
      context become {
        case BinnaryCmd(data) =>
          println(s"trying to send data: $data")
          connection ! Write(data)

        case data: ByteString =>
          println("trying to send data via embedded")
          connection ! Write(data)

        case CommandFailed(cmd: Command) =>
          log.error("Command failed " + cmd)

        case Received(data) =>
          dataStream(data).foreach { chunk =>
            processMavlinkData(reader, chunk)
          }

        case CloseMavlinkConnection =>
          log.warning("Connection closed by client")
          connection ! Close

        case _: ConnectionClosed =>
          log.warning("Connection closed " + remote + ". Killing self")
          self ! Kill

        case Subscribe(listener) =>
          listener ! DevDiscover
          println("listener added " + listener)
          listeners += listener

        case Unsubscribe(listener) =>
          listeners -= listener
      }

    case other => stash()
  }

  private def dataStream(data: ByteString): Stream[ByteString] = {
    val head = data.take(5000)
    if (head.isEmpty) Stream.empty
    else head #:: dataStream(data.drop(5000))
  }

  def processMavlinkData(reader: MavLinkStreamReader, data:ByteString) = {
    if (isRawTelemetryAllowed) {
      listeners.foreach(_ ! TelemetryRaw(data, memberId))
    }

    for {
      msg <- reader.getStream(data.toArray) if mavlinkTypeFilter.isEmpty || mavlinkTypeFilter.contains(msg.messageType)
    } {
      //log.debug(s"dispatching message2: $m")
      //dispatcher ! MavLinkTelemetry(m)
      mlBatcher.process(MavLinkTelemetry(msg, memberId))
    }
  }

}

class MavLinkStreamReader {
  private[this] val reader = new MAVLinkReader()
  private def readNext(buf: Option[Array[Byte]]) = {
    val arr = buf.getOrElse(null)
    val len = if (buf.isDefined) buf.get.length else 0
    Option(reader.getNextMessage(arr, len))
  }
  def getStream(buf: Array[Byte]) = {
    def loop(buf: Option[Array[Byte]]): Stream[MAVLinkMessage] = {
      readNext(buf) match {
        case Some(msg) => msg #:: loop(None)
        case None => Stream.empty
      }
    }
    loop(Some(buf))
  }

}

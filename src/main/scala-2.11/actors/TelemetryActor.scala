package actors

import java.io.FileOutputStream

import actors.Messages._
import akka.actor._
import akka.util.ByteString
import utils.{ConfigProvider, CircularBuffer}

/**
 * Created by yishchuk on 29.10.2015.
 */
class TelemetryActor extends Actor with ActorLogging with ConfigProvider with Stash {
  val historySize = config.getInt("history.size")
  lazy val buf = new CircularBuffer[TelemetryRaw](historySize)
  lazy val mlBuf = new CircularBuffer[MavLinkTelemetry](historySize)

  var listeners: List[ActorRef] = Nil

  def receive = {

    case DevDiscover =>
      println(s"discovered device ${sender()}")
      context become paired(sender())
      unstashAll()

    case x => println(s"TA: $x"); stash()
  }

  def paired(device: ActorRef): Receive = {
    case TelemetryRaw(data, _) =>
      appendFile(data.toArray)

    case trs: Array[TelemetryRaw] =>
      trs foreach { tel =>
        buf.push(tel)
      }

    case mlts: Array[MavLinkTelemetry] =>
      mlts foreach {mlt =>
        mlBuf.push(mlt)
      }
      listeners foreach {_ ! mlts}

    case TelemetryHistory(_) =>
      sender() ! buf.getAll

    case MavLinkTelemetryHistory(_) =>
      sender() ! mlBuf.getAll

    case (MavLinkTelemetryHistory(_), replyTo: ActorRef) =>
      replyTo ! mlBuf.getAll

    case RegisterListener(listener, _) =>
      listeners = listener :: listeners
      log.info(s"registered $listener")

    case UnregisterListener(listener, _) =>
      listeners = listeners.filterNot(_ == listener)
      log.info(s"unregistered $listener")

    case BatchCmd(_, batch) =>
      val buff = batch.foldLeft(ByteString()) { (acc, item) => acc ++ ByteString(item.encode()) }
      device ! BinnaryCmd(buff)

    case x => println(s"TA-p: $x")
  }

  override def postStop() = {
    listeners foreach { _ ! TelemetryStopped }
  }

  def appendFile(buff:Array[Byte]) = {
    val os = new FileOutputStream(s"mavlinkdata.bin", true)
    os.write(buff)
    os.close()
  }

}

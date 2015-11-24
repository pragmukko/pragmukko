package actors

import java.net.InetSocketAddress

import actors.Messages.{DiscoverAndJoinSwarmCluster, DiscoverSwarmCluster}
import akka.actor._
import akka.cluster.Cluster
import akka.io.IO
import akka.util.ByteString
import utils.{NetUtils, ConfigProvider}
import scala.concurrent.duration._

/**
 * Created by yishchuk on 12.11.2015.
 */

class UdpDiscoveryResponder extends Actor with ConfigProvider with ActorLogging {
  import context.system
  import akka.io.Udp

  IO(Udp) ! Udp.Bind(self, new InetSocketAddress(config.getInt("discovery.udp-port")))

  def receive = {
    case Udp.Bound(local) =>
      println(s"discovery responder bound to $local");
      context.become(ready(sender()))

    case x => println(s"N: $x")
  }

  def ready(socket: ActorRef): Receive = {
    case Udp.Received(data, remote) =>

      val systemName = data.utf8String
      log.debug(s"received: [${systemName}] from $remote, socket: $socket")
      if (data.utf8String == config.getString("akka-sys-name")) {
        val port = config.getInt("akka.remote.netty.tcp.port")
        socket ! Udp.Send(ByteString(s"$port"), remote)
      } else {
        log.info(s"Received discovery request from unknown akka system '${systemName}'")
      }
    case Udp.Unbind  => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
    case x => println(s"S: $x")
  }
}

class UdpDiscoverer extends Actor with ConfigProvider with ActorLogging {
  import context.system
  import akka.io.Udp
  import scala.concurrent.ExecutionContext.Implicits.global

  def receive = {
    case DiscoverSwarmCluster =>
      IO(Udp) ! Udp.Bind(self, new InetSocketAddress(0), List(Udp.SO.Broadcast(true)))
      context become receiveConnection(sender(), false)
    case DiscoverAndJoinSwarmCluster =>
      IO(Udp) ! Udp.Bind(self, new InetSocketAddress(0), List(Udp.SO.Broadcast(true)))
      context become receiveConnection(sender(), true)
  }
  def receiveConnection(replyTo: ActorRef, autoJoin: Boolean): Receive = {
    case Udp.Bound(local) =>
      val broadcast = NetUtils.broadcastAddress
      log.debug(s"broadcast: $broadcast")

      val localSender = sender()
      val isa = new InetSocketAddress(broadcast, config.getInt("discovery.udp-port"))
      val scheduled = context.system.scheduler.schedule(500 millis, 1 second) {
        log.debug("")
        localSender ! Udp.Send(ByteString(config.getString("akka-sys-name")), isa)
      }

      context.become(ready(replyTo, sender(), autoJoin, scheduled))
  }

  def ready(replyTo: ActorRef, socket: ActorRef, autoJoin: Boolean, scheduled: Cancellable): Receive = {
    case Udp.Received(data, remote) =>
      scheduled.cancel()
      val remotePort = data.utf8String.toInt
      log.debug(s"received remote port: ${remotePort} from $remote")
      val address = Address("akka.tcp", config.getString("akka-sys-name"), remote.getAddress.getHostAddress, remotePort)
      if (autoJoin) {
        Cluster(context.system).joinSeedNodes(List(address))
      } else {
        replyTo ! Array(address)
      }
      self ! Udp.Unbind
    case Udp.Unbind  => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
    case x => println(s"C: $x")
  }
}

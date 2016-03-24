/*
* Copyright 2015-2016 Pragmukko Project [http://github.org/pragmukko]
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
*    [http://www.apache.org/licenses/LICENSE-2.0]
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/
package actors

import java.net.InetSocketAddress

import actors.Messages.{DiscoveredSeedAddresses, DiscoverAndJoinSwarmCluster, DiscoverSwarmCluster}
import akka.actor._
import akka.cluster.Cluster
import akka.io.IO
import akka.util.ByteString
import com.typesafe.config.Config
import utils.{NetUtils, ConfigProvider}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

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
      val broadcast = NetUtils.broadcastAddress(config)
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
        replyTo ! DiscoveredSeedAddresses(Array(address))
      }
      self ! Udp.Unbind
    case Udp.Unbind  => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
    case x => println(s"C: $x")
  }
}

trait SwarmDiscovery { self: Actor with ActorLogging with ConfigProvider =>
  private def discoverInternal(discoveryMessage: Any) = {

    if (!config.hasPath("discovery.discoverer"))
      log.warning(s"Discoverer class is not defined. Using native akka cluster joining system.")
    else
      Try(Class.forName(config.getString("discovery.discoverer"))) match {
        case Success(clazz) =>
          context.actorOf(Props(clazz)) ! discoveryMessage
        case Failure(error) =>
          log.error(s"Discoverer class can't be loaded - can't join cluster: $error")
          context.system.terminate()
      }
  }

  def startDiscovery() = {
    discoverInternal(DiscoverSwarmCluster)
  }

  def discoverAndJoin() = {
    discoverInternal(DiscoverAndJoinSwarmCluster)
  }
}

object SwarmDiscovery {

  def startResponder(system: ActorSystem, config: Config) = {
    Try(Class.forName(config.getString("discovery.responder"))) match {
      case Success(clazz) =>
        system.actorOf(Props(clazz), "discovery-responder")
      case Failure(error) =>
        println(s"Discoverer class can't be loaded - can't join cluster: $error")
        system.terminate()
    }
  }

  def discoverAndJoin()(implicit system: ActorSystem, config: Config) = {
    Try(Class.forName(config.getString("discovery.discoverer"))) match {
      case Success(clazz) =>
        system.actorOf(Props(clazz)) ! DiscoverAndJoinSwarmCluster
      case Failure(error) =>
        println(s"Discoverer class can't be loaded - can't join cluster: $error")
        system.terminate()
    }
  }
}

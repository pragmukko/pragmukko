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

import java.util.concurrent.atomic.AtomicReference

import actors.Messages._
import akka.actor.SupervisorStrategy.{Restart, Escalate}
import akka.actor._
import akka.cluster.Cluster
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Put
import akka.event.LoggingAdapter
import akka.util.ByteString
import com.typesafe.config.Config
import utils.{MessageBatcher, ConfigProvider}

import scala.collection.mutable
import scala.reflect.ClassTag

/**
 * Created by max on 10/27/15.
 */

case class ExtReceiver(rcvr: BaseEmbeddedActor => PartialFunction[Any, Unit])

class BaseEmbeddedActor(hardwareGateClass:Option[Class[_ <: Actor]]) extends Actor with ActorLogging with ConfigProvider with SwarmDiscovery {

  private val hardwareGate:Option[ActorRef] = initHardwareGateActor

//  private val clusterDiscoverer = context.actorOf(Props[UdpDiscoverer], "clusterDiscoverer")
//
//
//  clusterDiscoverer ! DiscoverSwarmCluster

  val mediator = DistributedPubSub(context.system).mediator
  mediator ! Put(self)

  startDiscovery()
  context become baseReceive

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _: ActorKilledException => Restart
      case t =>
        super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }

  private def baseReceive: Receive = {
    val baseFnc:Receive = {

    case DiscoveredSeedAddresses(seedAddresses: Array[Address]) =>
      Cluster(context.system).joinSeedNodes(seedAddresses.toList)

      case GCDiscover(telemetryHistory) =>
        println("Ground control found")
        hardwareGate match {
          case Some(act) =>
            act ! Subscribe(telemetryHistory)
            println("Connected to ground control, processing started.")

          case _ =>
            log.warning("Attempt to register GroundControl on hardware events, but hw gate isn't created ")
            sender() ! Unsupported
        }

      case ExtReceiver(rcvr) =>
        //TODO: Check if sender is local
        context.become(rcvr(this) orElse baseReceive, true)

      case BinnaryCmd(buff) =>
        hardwareGate foreach ( _ ! buff )

      case x => println(s"UNKNOWN: $x")
    }
    receive orElse baseFnc
  }

  def subscribeHardwareEvents(actr:ActorRef):Unit = {
    hardwareGate match {
      case Some(x) => x ! Subscribe(actr)
      case None => new Exception("Subscribing on hardware events is not allowed.")
    }
  }

  def subscribeHardwareEvents():Unit = subscribeHardwareEvents(self)

  override def receive: Receive = PartialFunction.empty[Any, Unit]

  protected def initHardwareGateActor = {
    hardwareGateClass map { clzz => context.actorOf(Props(clzz), "mavlinkGate")}
  }

}


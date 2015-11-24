package actors

import java.util.concurrent.atomic.AtomicReference

import actors.Messages._
import akka.actor.SupervisorStrategy.{Restart, Escalate}
import akka.actor._
import akka.cluster.Cluster
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

class BaseEmbeddedActor(hardwareGateClass:Option[Class[_ <: Actor]]) extends Actor with ActorLogging with ConfigProvider {

  private val hardwareGate:Option[ActorRef] = initHardwareGateActor

  private val clusterDiscoverer = context.actorOf(Props[UdpDiscoverer], "clusterDiscoverer")

  clusterDiscoverer ! DiscoverSwarmCluster

  context become baseReceive

  override val supervisorStrategy =
    OneForOneStrategy() {
      case _: ActorKilledException => Restart
      case t =>
        super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }

  private def baseReceive: Receive = {
    val baseFnc:Receive = {

      case seedAddresses: Array[Address] =>
        Cluster(context.system).joinSeedNodes(seedAddresses.toList)

      case GCDiscover(telemetryHistory) =>
        println("Ground control found")
        hardwareGate match {
          case Some(act) =>
            act ! Subscribe(telemetryHistory)
            println("Connected to ground control, processing started.")

          case _ =>
            log.warning("Attempt to register GroundControl on hardware events, but hw gate doesn't created ")
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


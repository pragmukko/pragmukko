package builders

import actors.Messages.Start
import actors.{SwarmDiscovery, BaseEmbeddedActor, ExtReceiver, MavlinkGateActor}
import akka.actor.{Props, ActorSystem, Actor}
import utils.ConfigProvider

/**
 * Created by max on 11/6/15.
 */
trait EmbeddedNode extends ConfigProvider {

  type ReceiverType = BaseEmbeddedActor => PartialFunction[Any, Unit]

  case class EmbeddedNodeBuilder[AC <: BaseEmbeddedActor, HW <: Actor](receiver: Option[ReceiverType],
    embeddedActor:Class[AC],
    hardwareGateActor:Option[Class[HW]]) {

    def start() = {
    val system = ActorSystem(config.getString("akka-sys-name"), config)
    val ref = system.actorOf(Props(embeddedActor, hardwareGateActor), s"embedded.${config.getString("member-id")}")
    receiver foreach {
      rcvr =>
        ref ! ExtReceiver(rcvr)
        ref ! Start
    }

    if (config.getBoolean("discovery.start-responder")) {
      SwarmDiscovery.startResponder(system, config)
      //system.actorOf(Props[UdpDiscoveryResponder], "discovery-responder")
    }
  }

    def withEmbedded[T <: BaseEmbeddedActor](implicit tag : reflect.ClassTag[T]) = {
      EmbeddedNodeBuilder(this.receiver, tag.runtimeClass.asInstanceOf[Class[T]], this.hardwareGateActor)
    }

    def withHWGate[T <: Actor](implicit tag : reflect.ClassTag[T]) = {
      EmbeddedNodeBuilder(this.receiver, this.embeddedActor, Some(tag.runtimeClass.asInstanceOf[Class[T]]))
    }

    def withReceiver(rcvr:ReceiverType) = {
      EmbeddedNodeBuilder(Some(rcvr), this.embeddedActor, hardwareGateActor)
    }
  }

  def builder() : EmbeddedNodeBuilder[_,_] = {
    EmbeddedNodeBuilder(None, classOf[BaseEmbeddedActor], Some(classOf[MavlinkGateActor]))
  }


  def apply(receiver: ReceiverType) = {
    EmbeddedNodeBuilder(Some(receiver), classOf[BaseEmbeddedActor], Some(classOf[MavlinkGateActor])).start();
  }

  def apply() = {
    EmbeddedNode.builder().start()
  }

}

object EmbeddedNode extends EmbeddedNode
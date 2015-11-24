package builders

import actors.{GCExtentions, UdpDiscoveryResponder, ManageActor}
import akka.actor.{Actor, ActorSystem, Props}
import http.SwarmHttpService
import utils.ConfigProvider

/**
 * Created by max on 11/5/15.
 */
object GRoundControlNode extends ConfigProvider {

  case class GCNodeBuilder[T <: GCExtentions](clusterName:String = config.getString("akka-sys-name"),
                           restEndpoint:Boolean = true,
                           discovery: Boolean = true,
                           extentions: List[Class[T]] = List.empty[Class[T]]
                            ) {
    def start(): Unit = {
      val system = ActorSystem(clusterName, config)
      val mgr = system.actorOf(Props[ManageActor], "manager")
      if (restEndpoint) {
        new SwarmHttpService(mgr)(system, config).start()
      }

      extentions.foreach(c => system.actorOf(Props(c)))

      if (discovery) {
        system.actorOf(Props[UdpDiscoveryResponder], "discovery-responder")
      }
    }

    def withREST(rest:Boolean) = GCNodeBuilder(this.clusterName, rest, this.discovery, extentions)

    def withDiscovery(disc:Boolean) = GCNodeBuilder(this.clusterName, this.restEndpoint, this.discovery, extentions)

    def addExtention[P <: GCExtentions](implicit tag : reflect.ClassTag[P]) = {
      val clzz = tag.runtimeClass.asInstanceOf[Class[T]]
      GCNodeBuilder[T](this.clusterName, this.restEndpoint, this.discovery, clzz :: extentions)
    }

  }

  def build() : GCNodeBuilder[_] = GCNodeBuilder()

  def apply(clusterName:String = config.getString("akka-sys-name"), restEndpoint:Boolean = true, discovery: Boolean = true) = {
    val system = ActorSystem(clusterName, config)
    val mgr = system.actorOf(Props[ManageActor], "manager")
    if (restEndpoint) {
      new SwarmHttpService(mgr)(system, config).start()
    }
    if (discovery) {
      system.actorOf(Props[UdpDiscoveryResponder], "discovery-responder")
    }
  }

}

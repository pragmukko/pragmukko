package builders

import actors.{GCExtentions, SwarmDiscovery, ManageActor}
import akka.actor.{Actor, ActorSystem, Props}
import http.SwarmHttpService
import utils.ConfigProvider

/**
 * Created by max on 11/5/15.
 */
trait GRoundControlNode extends ConfigProvider {

  case class GCNodeBuilder[T <: GCExtentions](clusterName:String = config.getString("akka-sys-name"),
                           restEndpoint:Boolean = true,
                           discovery: Boolean = false,
                           extentions: List[Class[T]] = List.empty[Class[T]]
                            ) {
    def start(): Unit = {
      val system = ActorSystem(clusterName, config)
      val mgr = system.actorOf(Props[ManageActor], "manager")
      if (restEndpoint) {
        new SwarmHttpService(Some(mgr))(system, config).start()
      }

      extentions.foreach(c => system.actorOf(Props(c), c.getSimpleName))

//      if (discovery) {
//        SwarmDiscovery.startResponder(system, config)
//      }
    }

    def withREST(rest:Boolean) = GCNodeBuilder(this.clusterName, rest, this.discovery, extentions)

    //def withDiscovery(disc:Boolean) = GCNodeBuilder(this.clusterName, this.restEndpoint, disc, extentions)

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
      new SwarmHttpService(Some(mgr))(system, config).start()
    }
    if (config.getBoolean("discovery.start-responder")) {
      SwarmDiscovery.startResponder(system, config)
    }
  }

}

object GRoundControlNode extends GRoundControlNode


import actors.ClientActor
import akka.actor.{ActorSystem, Props}
import builders.{EmbeddedNode, GRoundControlNode}
import utils.ConfigProvider


/**
 * Created by max on 10/26/15.
 */
object Main extends App with ConfigProvider {

  config.getString("type").toLowerCase match {
    case "embedded" => initEmbeddedRole()
    case "manager" => initManagerRole()
    case "client" => initClientRole()
  }

  def initEmbeddedRole() = {
    println("Init embedded actor")
    EmbeddedNode()
  }

  def initManagerRole() = {
    println("Init manager actor")
    GRoundControlNode(restEndpoint = true)
  }

  def initClientRole() = {
    println("Init Client actor")
    val system = ActorSystem(config.getString("akka-sys-name"), config)
    system.actorOf(Props[ClientActor], "client")
  }
}


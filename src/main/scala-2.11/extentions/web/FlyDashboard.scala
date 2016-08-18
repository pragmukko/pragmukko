package extentions.web

import actors.GCExtentions
import actors.Messages.Start
import akka.actor.ActorRef
import utils.ConfigProvider

/**
 * Created by max on 8/18/16.
 */
class FlyDashboard extends GCExtentions with ConfigProvider {

  override def process(manager: ActorRef): Receive = {

    case Start =>
      new SwarmHttpService(Some(manager))(system, config).start()

    case _ =>
  }


}

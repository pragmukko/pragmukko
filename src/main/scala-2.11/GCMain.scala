import actors.{NewMemberWithId, GCExtentions}
import actors.Messages.Start
import akka.actor.ActorRef
import api.Channel
import builders.GRoundControlNode
import scala.concurrent.duration._

/**
 * Created by max on 11/18/15.
 */
object GCMain extends App {

  GRoundControlNode.build().addExtention[StdOutExt].start()

}

class StdOutExt extends GCExtentions {

  override def process(manager: ActorRef): Receive = {

    case Start =>
      listMembersIds foreach(handleNewMemeber(_, manager))

    case NewMemberWithId(id) => handleNewMemeber(id, manager)
  }

  def handleNewMemeber(id:String, manager:ActorRef) = {
    Channel.create(manager, id).source.groupedWithin(500, 5 seconds).runForeach {
      x => println(x.head)
    }
  }
}

import actors.{NewMemberWithId, GCExtentions}
import actors.Messages.{MavLinkTelemetry, Start}
import akka.actor.ActorRef
import akka.cluster.ClusterEvent.MemberUp
import api.Channel
import builders.GRoundControlNode
import mavlink.pixhawk.{DronePositionLocal, TelemetryBatch, DroneCommands}
import scala.concurrent.duration._

/**
 * Created by max on 11/18/15.
 */
object GCMain extends App {

  GRoundControlNode
    .build()
    .addExtention[DroneControlExt]
    .start()

}

class StdOutExt extends GCExtentions {

  override def process(manager: ActorRef): Receive = {

    case Start =>
      listMembersIds foreach(handleNewMemeber(_, manager))

    case NewMemberWithId(id) => handleNewMemeber(id, manager)
  }

  def handleNewMemeber(id:String, manager:ActorRef) = {
    Channel.create(id).source.groupedWithin(500, 5 seconds).runForeach {
      x => println(x.head)
    }
  }
}

class DroneControlExt extends GCExtentions with DroneCommands {

  override def process(manager: ActorRef): Receive = {

    case MemberUp(member) =>
      println("!!" + member)
      subscribeTelemetry(member)

    case TelemetryBatch(b) =>
      b.collect{case DronePositionLocal(p) => p }.lastOption match {
        case Some(p) =>
          val newVY = if (p.y > 10) -1 else p.vy
          val newVX = if (p.x > 10) -1 else p.vx
          sender() ! direction(newVX, newVY)

        case _ =>
      }

    case other => println(other)
  }

}


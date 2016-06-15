import actors.BaseEmbeddedActor
import actors.Messages.{DevDiscover, GCDiscover}
import akka.actor.{Actor, ActorRef}
import builders.Pragma
import mavlink.pixhawk.{PositionLocal, DroneCommands}
import utils.{ClusterUtils, ClusterNode}
import scala.concurrent.duration._

/**
 * Created by max on 4/19/16.
 */

object Follower extends App {

  Pragma.builder().withEmbedded[Follower].start()

}


class Follower(act:Option[Class[_ <: Actor]]) extends FakeDroneActor(act) with ClusterUtils  {

  context.system.scheduler.schedule(1 second, 1 second, self, "Tick")

  override def receive:Receive = handle orElse super.receive

  def handle:Receive = {

    case "Tick" =>
      // Look for the Leader among other cluster members
      val theLeader = members \\ "Leader"

      // Send the message to the Leader, and process the response
      theLeader ? "where are you man?" foreach {

        case pl:PositionLocal =>
          // A bit of vector magic, to find direction to the Leader
          val dx = pl.x - xyz(0)
          val dy = pl.y - xyz(1)

          val abs = 5.0f / Math.sqrt( dx * dx + dy * dy ).toFloat

          vxyz = List( dx * abs, dy * abs, 0.0f )


        case _ => println("I don't know what you talking about")
      }


  }

}

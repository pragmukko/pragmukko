import actors.BaseEmbeddedActor
import actors.Messages.{DevDiscover, MavLinkTelemetry, GCDiscover}
import akka.actor.{ActorRef, Actor}
import builders.EmbeddedNode
import mavlink.pixhawk.{SetPositionLocal, PositionLocal, DroneCommands}
import scala.concurrent.duration._

/**
 * Created by max on 11/12/15.
 */
object FakeDrone extends App {

  EmbeddedNode.builder().withEmbedded[FakeDroneActor].start()

}

case object Tick

class FakeDroneActor(act:Option[Class[_ <: Actor]]) extends BaseEmbeddedActor(None) with DroneCommands {

  implicit val escContext = context.system.dispatcher

  val initialVx = if (config.hasPath("vx")) config.getDouble("vx").toFloat else .0f
  val initialVy = if (config.hasPath("vy")) config.getDouble("vy").toFloat else .0f

  var xyz = List(.0f, .0f, .0f)
  var vxyz = List(initialVx, initialVy, .0f)

  var startTime = 0l

  var gcList = List.empty[ActorRef]

  startTime = System.currentTimeMillis()
  context.system.scheduler.schedule(100 milliseconds, 100 milliseconds, self, Tick)

  override def receive:Receive = {

    case GCDiscover(gc) =>
      println("GC discover")
      gc ! DevDiscover
      gcList = gc :: gcList


    case Tick =>
      val currentTime = System.currentTimeMillis()
      val delta = (currentTime - startTime) / 1000f // ms -> s
      startTime = currentTime

      xyz = vxyz.map(_ * delta).zip(xyz).map(p => p._1 + p._2)
      val pos = PositionLocal(xyz(0), xyz(1), xyz(2), vxyz(0), vxyz(1), vxyz(2))

      gcList foreach (_ ! Array(MavLinkTelemetry(pos.dueProtocol, "")))

    case SetPositionLocal(pos) =>
      vxyz = List(pos.vx, pos.vy, pos.vz)


    case "where are you man?" =>
      sender() ! PositionLocal(xyz(0), xyz(1), xyz(2), vxyz(0), vxyz(1), vxyz(2))

  }

}


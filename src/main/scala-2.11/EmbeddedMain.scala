import actors.BaseEmbeddedActor
import actors.Messages._
import akka.actor.{Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{CurrentTopics, GetTopics}
import builders.EmbeddedNode
import mavlink.pixhawk.Ping
import mavlink.pixhawk._

/**
 * Created by max on 11/6/15.
 */
object EmbeddedMain extends App {

  @volatile var lastKnownLocalPosition:Option[PositionLocal] = None
  @volatile var lastKnownGlobalPosition:Option[PositionGlobal] = None

  EmbeddedNode {
    ctx => {

      case Start =>
        println("Start !!")
        ctx.subscribeHardwareEvents()

      case "ping" => ctx.sender() ! "pong"

      case "where are you man?" =>
        lastKnownLocalPosition match {
          case Some(pos) => ctx.sender() ! pos
          case None => ctx.sender() ! "Have no idea"
        }

      case "where are you man globally?" =>
        lastKnownGlobalPosition match {
          case Some(pos) => ctx.sender() ! pos
          case None => ctx.sender() ! "Have no global idea"
        }

      case TelemetryBatch(batch) =>
        lastKnownLocalPosition = batch.collect { case DronePositionLocal(p) => p }.lastOption
        lastKnownGlobalPosition = batch.collect { case DronePositionGlobal(p) => p }.lastOption


      case MoveBy(dx, dy, dz) => lastKnownLocalPosition match {
        case Some(pos) =>
          println(s"moving by ($dx, $dy, $dz) from (${pos.x}, ${pos.y}, ${pos.z})")
          ctx.self ! DroneCommands.moveTo(pos.x + dx, pos.y + dy, pos.z + dz)
        case None => println(s"Can't move because current position is unknown")
      }

      case MoveByGlobal(dlat, dlon, dalt) => lastKnownGlobalPosition match {
        case Some(pos) =>
          println(s"moving globally by ($dlat, $dlon, $dalt) from (${pos.lat}, ${pos.lon}, ${pos.alt})")
          ctx.self ! DroneCommands.moveToGlobal(pos.lat + dlat, pos.lon + dlon, pos.alt + dalt)
        case None => println(s"Can't move because current global position is unknown")
      }

      case Ping(ping) => println(ping)
    }

  }
}

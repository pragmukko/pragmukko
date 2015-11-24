
import actors.Messages.Start
import builders.EmbeddedNode
import mavlink.pixhawk._

/**
 * Created by max on 11/6/15.
 */
object EmbeddedMain extends App {

  @volatile var lastKnownPosition:Option[Position] = None

  EmbeddedNode {
    ctx => {

      case Start =>
        println("Start !!")
        ctx.subscribeHardwareEvents()

      case "ping" => ctx.sender() ! "pong"

      case "where are you man?" =>
        lastKnownPosition match {
          case Some(pos) => ctx.sender() ! pos
          case None => ctx.sender() ! "Have no idea"
        }

      case TelemetryBatch(batch) =>
        lastKnownPosition = batch.collect { case DronePosition(p) => p }.lastOption


      case Ping(ping) => println(ping)
    }
  }
}

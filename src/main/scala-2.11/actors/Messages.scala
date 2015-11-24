package actors

import akka.actor.{Address, ActorRef}
import akka.util.ByteString
import org.mavlink.messages.MAVLinkMessage

/**
 * Created by max on 10/27/15.
 */
object Messages {

  sealed trait Command

  case object Ping
  case object ListMembers extends Command
  case object CloseMavlinkConnection
  case class GCDiscover(telemetryActor:ActorRef)
  case object DevDiscover
  case class TelemetryDiscovery(memberId: String)
  case class TelemetryHistory(memberId: String) extends Command
  case class MavLinkTelemetryHistory(memberId: String) extends Command

  case class TelemetryRaw(bs:ByteString, memberId: String) extends HasSize {
    override def size = bs.length
  }

  case class MavLinkTelemetry(message: MAVLinkMessage, memberId: String) extends HasSize {
    override def size = message.length
  }

  case object TelemetryStopped extends Command

  case class BatchCmd(him: String, elements:List[MAVLinkMessage])
  case class BinnaryCmd(bin:ByteString)

  case class RegisterListener(listener: ActorRef, memberId: String)
  case class UnregisterListener(listener: ActorRef, memberId: String)

  case class Subscribe(listener:ActorRef)
  case class Unsubscribe(listener:ActorRef)

  case object DiscoverSwarmCluster
  case object DiscoverAndJoinSwarmCluster

  case object Unsupported
  
  case object Start

}

trait HasSize {
  def size: Int
}
/*
* Copyright 2015-2016 Pragmukko Project [http://github.org/pragmukko]
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
*    [http://www.apache.org/licenses/LICENSE-2.0]
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/
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
  case class DiscoveredSeedAddresses(nodes: Array[Address])

  case object Unsupported
  
  case object Start

  case class MoveBy(dx: Float, dy: Float, dz: Float)

  case class MoveByGlobal(dlat: Long, dlon: Long, dalt: Float)

}

trait HasSize {
  def size: Int
}

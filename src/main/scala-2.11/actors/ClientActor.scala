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

import actors.Messages.{BinnaryCmd, DiscoverAndJoinSwarmCluster, BatchCmd, RegisterListener}
import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.stream.{OverflowStrategy, ActorMaterializer}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import api.Channel
import utils.ConfigProvider
import scala.concurrent.duration._
import org.mavlink.messages.pixhawk.{msg_set_position_target_local_ned, msg_altitude}

import scala.util.{Failure, Try, Success}

/**
 * Created by yishchuk on 06.11.2015.
 */
class ClientActor(memberId: String) extends Actor with ActorLogging with ConfigProvider with SwarmDiscovery {

  import utils.MemberUtils._

  val cluster = Cluster(context.system)
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()

  discoverAndJoin()

  override def preStart() = {
    cluster.subscribe(self, initialStateMode = InitialStateAsSnapshot,
      classOf[MemberEvent])
  }

  def receive = {
    case MemberUp(member) if member.id.contains(memberId) =>
      println(s"Member '$memberId' found, connecting...")
      val channel = Channel.create(memberId)
      println(s"Got channel: $channel")

      channel.source.groupedWithin(50, 5 seconds).collect {
        case x => BinnaryCmd(ByteString(new msg_altitude(0,1).encode()))
      }.runWith(channel.sink)
  }
}

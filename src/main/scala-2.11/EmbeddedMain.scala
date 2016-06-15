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
import actors.BaseEmbeddedActor
import actors.Messages._
import akka.actor.{Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{CurrentTopics, GetTopics}
import builders.Pragma
import mavlink.pixhawk.Ping
import mavlink.pixhawk._

/**
 * Created by max on 11/6/15.
 */
object EmbeddedMain extends App {

  @volatile var lastKnownLocalPosition:Option[PositionLocal] = None
  @volatile var lastKnownGlobalPosition:Option[PositionGlobal] = None

  Pragma {
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

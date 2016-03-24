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
import actors.Messages.{DevDiscover, MavLinkTelemetry, GCDiscover}
import akka.actor.{ActorRef, Actor}
import builders.EmbeddedNode
import mavlink.pixhawk.{SetPositionLocal, PositionLocal, DroneCommands}
import scala.concurrent.duration._
import scala.util.Random

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
      val pos = PositionLocal(xyz(0), xyz(1), xyz(2), vxyz(0) + Random.nextFloat(), vxyz(1) + Random.nextFloat(), vxyz(2) + Random.nextFloat())

      gcList foreach (_ ! Array(MavLinkTelemetry(pos.dueProtocol, "")))

    case SetPositionLocal(pos) =>
      vxyz = List(pos.vx, pos.vy, pos.vz)


    case "where are you man?" =>
      sender() ! PositionLocal(xyz(0), xyz(1), xyz(2), vxyz(0), vxyz(1), vxyz(2))

  }

}


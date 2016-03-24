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

import actors.Messages.{GCDiscover, Start}
import akka.actor._
import akka.cluster.{Member, Cluster}
import akka.cluster.ClusterEvent.{InitialStateAsEvents, MemberUp, MemberEvent, InitialStateAsSnapshot}
import akka.stream.ActorMaterializer
import scala.collection.JavaConversions._

/**
 * Created by max on 11/17/15.
 */
abstract class GCExtentions extends Actor with ActorLogging {

  def name:String = "gcextention"

  val cluster = Cluster(context.system)
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()

  override def preStart() = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent])
  }

  context.become(baseReceive)

  private def baseReceive:Receive = {
    case MemberUp(member) if member.hasRole("server") =>
      println(s"has server node: $member")
      context.actorSelection(member.address.toString + "/user/manager") ! Identify("client")

    case ActorIdentity(_, None) =>
      println("Manager actor not found on manager node, exiting...")
      self ! PoisonPill
      cluster.down(cluster.selfAddress)
      context.system.terminate()

    case ActorIdentity(_, Some(manager)) =>
      context.become(process(manager) orElse baseReceive )
      self ! Start

    case other => println("UNKNOWN: " + other)
  }

  def process(manager:ActorRef): Receive

  def receive:Receive = PartialFunction.empty[Any, Unit]

  def listMembersIds : List[String] = {
    cluster.state.members.flatMap(_.roles).filter(_.startsWith("id:")).map(_.substring("id:".length)) toList
  }

  def subscribeTelemetry(m:Member) = {
    m2a(m) ! GCDiscover(self)
  }

  def m2a(member:Member) = {
    val selector = member.address.toString + "/user/*"
    context.actorSelection(selector)
  }

  def byId(id:String) = {
    val addr = cluster.state.members.filter(_.getRoles.exists(r => r == "id:" + id)).head.address
    context.actorSelection( addr + "/user/*" )
  }

}

object NewMemberWithId {

  def unapply(m:MemberUp) : Option[String] = {
    m.member.roles.filter(_.startsWith("id:")).headOption.map(_.substring("id:".length))
  }

}

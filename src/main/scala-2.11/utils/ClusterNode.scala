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
package utils

import actors.Messages.DiscoverAndJoinSwarmCluster
import actors.SwarmDiscovery
import akka.actor
import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Send
import akka.cluster.{Member, Cluster}
import akka.util.Timeout
import scala.collection.JavaConversions._
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import akka.pattern._

import scala.util.{Failure, Success, Try}

/**
 * Created by max on 11/11/15.
 */
trait ClusterNode extends ConfigProvider {

  import MemberUtils._
  implicit val duration = 1 second
  implicit val timeout = Timeout(duration)
  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  val system = ActorSystem(config.getString("akka-sys-name"), config)
  val cluster = Cluster(system)

  SwarmDiscovery.discoverAndJoin()(system, config)

  val psMediator = DistributedPubSub(system).mediator

  def listMembers = cluster.state.getMembers.toList

  def members = cluster.state.getMembers.toList

  def listMemberIds = cluster.state.getMembers.flatMap { _.id}

  def member(index:Int) = {
    val path = members(index).address.toString + "/user/*"
    system.actorSelection(path)
  }

  def embedded(index:Int) = {
    val member = members(index)
    val path = s"${member.address.toString}/user/embedded.${member.id.get}"
    Await.result(system.actorSelection(path).resolveOne, 2 second)
  }

  def embedded(member:Member) = {
    val path = s"${member.address.toString}/user/embedded.${member.id.get}"
    Await.result(system.actorSelection(path).resolveOne, 2 second)
  }

  implicit class SyncUtils(m:ActorRef) {
    def ??(msg:Any)(implicit errHandler:Throwable => Any = th => th.toString()) : Any = {
      Try( Await.result( m ? msg, duration) ) match {
        case Success(x) => x
        case Failure(th) => errHandler(th)
      }
    }
  }

  implicit class MemberListImplicits(membersList:List[Member]) {
    def \\(name:String) : Option[Member] = membersList.find(m => m.id.map( id => id.contains(name)).getOrElse(false))
  }

  implicit class MemberImplicits(memberOpt:Option[Member]) {
    def ?(msg:Any) : Future[Any] = memberOpt match {
      case Some(m) =>
        val path = m.address.toString + "/user/*"
        system.actorSelection(path).ask(msg)

      case None => Future.failed(new Exception("Not member with such Id"))
    }
  }

  def sendToMember(memberId: String)(msg: Any) = {
    psMediator ! Send(s"/user/embedded.$memberId", msg, true)
  }

  def askMember(memberId: String)(msg: Any): Any = {
    Await.result(psMediator ? Send(s"/user/embedded.$memberId", msg, true), duration)
  }
}



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
import scala.concurrent.Await
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

  val mediator = DistributedPubSub(system).mediator

  def listMembers = cluster.state.getMembers.toList

  def listMemberIds = cluster.state.getMembers.flatMap { _.id}

  def member(index:Int) = {
    val path = listMembers(index).address.toString + "/user/*"
    system.actorSelection(path)
  }

  def embedded(index:Int) = {
    val member = listMembers(index)
    val path = s"${member.address.toString}/user/embedded.${member.id.get}"
    Await.result(system.actorSelection(path).resolveOne, 2 second)
  }

  def embedded(member:Member) = {
    val path = s"${member.address.toString}/user/embedded.${member.id.get}"
    Await.result(system.actorSelection(path).resolveOne, 2 second)
  }

  implicit class SyncUtils(m:ActorRef){
    def ??(msg:Any)(implicit errHandler:Throwable => Any = th => th.toString()) : Any = {
      Try( Await.result( m ? msg, duration) ) match {
        case Success(x) => x
        case Failure(th) => errHandler(th)
      }
    }
  }

  def sendToMember(memberId: String)(msg: Any) = {
    mediator ! Send(s"/user/embedded.$memberId", msg, true)
  }

  def askMember(memberId: String)(msg: Any): Any = {
    Await.result(mediator ? Send(s"/user/embedded.$memberId", msg, true), duration)
  }
}



package utils

import akka.actor.Actor
import akka.cluster.{Member, Cluster}
import akka.util.Timeout

import scala.collection.JavaConversions._
import scala.concurrent.Future
import akka.pattern._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
 * Created by max on 4/19/16.
 */
trait ClusterUtils {

  self: Actor =>

  import MemberUtils._

  implicit val duration = 1 second
  implicit val timeout = Timeout(duration)
  val cluster = Cluster(context.system)

  def members = cluster.state.getMembers.toList

  implicit class MemberListImplicits(membersList:List[Member]) {
    def \\(name:String) : Option[Member] = membersList.find(m => m.id.map( id => id.contains(name)).getOrElse(false))
  }

  implicit class MemberImplicits(memberOpt:Option[Member]) {
    def ?(msg:Any) : Future[Any] = memberOpt match {
      case Some(m) =>
        val path = m.address.toString + "/user/*"
        context.system.actorSelection(path).ask(msg)

      case None => Future.failed(new Exception("Not member with such Id"))
    }
  }

}

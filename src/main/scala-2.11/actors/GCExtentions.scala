package actors

import actors.Messages.Start
import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberUp, MemberEvent, InitialStateAsSnapshot}
import akka.stream.ActorMaterializer

/**
 * Created by max on 11/17/15.
 */
abstract class GCExtentions extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()

  override def preStart() = {
    cluster.subscribe(self, initialStateMode = InitialStateAsSnapshot,
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

}

object NewMemberWithId {

  def unapply(m:MemberUp) : Option[String] = {
    m.member.roles.filter(_.startsWith("id:")).headOption.map(_.substring("id:".length))
  }

}

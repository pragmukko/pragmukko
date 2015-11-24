package actors

import java.net.InetAddress

import actors.Messages._
import akka.actor._
import akka.cluster.{MemberStatus, Member, Cluster}
import akka.cluster.ClusterEvent._
import akka.http.scaladsl.util.FastFuture
import akka.util.{Timeout, ByteString}
import utils.{NetUtils, MemberUtils, ConfigProvider}
import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by max on 10/27/15.
 */
class ManageActor extends Actor with ActorLogging with ConfigProvider {

  import MemberUtils._
  val cluster = Cluster(context.system)

  cluster.joinSeedNodes(List(Address("akka.tcp", config.getString("akka-sys-name"), NetUtils.localHost.getHostAddress, config.getInt("akka.remote.netty.tcp.port"))))

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  // YI !!! very simple implementation
  val channels = mutable.HashMap.empty[String, ActorRef]

  def channelFor(id: String): Option[ActorRef] = {
    if (memberExists(id))
      Some(channels.getOrElseUpdate(id, {val history = context.actorOf(Props[TelemetryActor], s"channel-for-$id"); println(s"history created: $history"); history}))
    else None
  }

  def channelFor(member: Member): Option[ActorRef] = {
    member.id.flatMap { id => channelFor(id) }
  }

  def historyFutureFor(id: String): Future[ActorRef] = {
    if (memberExists(id)) {
      implicit val timeout = Timeout(2 seconds)
      val sel = context.actorSelection(s"channel-for-$id")
      println(s"searching for $sel...")
      sel.resolveOne()
    } else {
      Future.failed[ActorRef](MemberNotFound(id))
    }
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  private def getMember(memberId: String) = {
    cluster.state.members.find(_.id == Some(memberId))
  }
  private def memberExists(memberId: String) = {
    getMember(memberId) match {
      case Some(member) => member.status == MemberStatus.up
      case None => false
    }

  }

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}, roles: [{}]", member.address, member.roles mkString ", ")
      if (member.hasRole("embedded")) {
        channelFor(member) foreach { m2a(member) ! GCDiscover(_) }
      }

    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)

    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
      channelFor(member) foreach { _ ! PoisonPill }
      member.id.foreach{channels.remove}

    case ListMembers =>
      sender() ! cluster.state.members.toList

    case rth @ TelemetryHistory(memberId) =>
      channelFor(memberId) match {
        case Some(h) => h.forward(rth)
        case None => sender() ! List.empty[TelemetryRaw]
      }

    case mth @ MavLinkTelemetryHistory(memberId) =>
      channelFor(memberId) match {
        case Some(h) => h.forward(mth)
        case None => sender() ! List.empty[MavLinkTelemetry]
      }

    case BatchCmd(memberId, batch) =>
      val buff = batch.foldLeft(ByteString()) { (acc, item) => acc ++ ByteString(item.encode()) }
      m2a(memberId) foreach ( _ ! BinnaryCmd(buff) )

    case rl @ RegisterListener(listener, memberId) => channelFor(memberId).foreach(_.forward(rl))

    case ul @ UnregisterListener(listener, memberId) => channelFor(memberId).foreach(_.forward(ul))

    case TelemetryDiscovery(memberId) => println(s"sending channel for $memberId: '${channelFor(memberId)}'"); sender() ! channelFor(memberId)

    case _: MemberEvent =>

    case x => println(s"Unknown message: $x")
  }

  def m2a(member:Member) = {
    val selector = member.address.toString + "/user/*"
    context.actorSelection(selector)
  }

  def m2a(memberId: String): Option[ActorSelection] = {
    getMember(memberId) map {m => context.actorSelection(m.address.toString + "/user/*")}
  }


}

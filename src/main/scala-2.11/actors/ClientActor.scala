package actors

import actors.Messages.{DiscoverAndJoinSwarmCluster, BatchCmd, RegisterListener}
import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.stream.{OverflowStrategy, ActorMaterializer}
import akka.stream.scaladsl.Source
import api.Channel
import scala.concurrent.duration._
import org.mavlink.messages.pixhawk.msg_altitude

/**
 * Created by yishchuk on 06.11.2015.
 */
class ClientActor extends Actor with ActorLogging {

  val cluster = Cluster(context.system)
  implicit val system = context.system
  implicit val materializer = ActorMaterializer()

  context.actorOf(Props[UdpDiscoverer]) ! DiscoverAndJoinSwarmCluster

  override def preStart() = {
    cluster.subscribe(self, initialStateMode = InitialStateAsSnapshot,
      classOf[MemberEvent])
  }

  def receive = {
    case MemberUp(member) if member.hasRole("server") =>
      println(s"has server node: $member")
      context.actorSelection(member.address.toString + "/user/manager") ! Identify("client")

    case ActorIdentity(_, None) =>
      println("Manager actor not found on manager node, exiting...")
      self ! PoisonPill
      cluster.down(cluster.selfAddress)
      context.system.terminate()

    case ActorIdentity(_, Some(manager)) =>
      val channel = Channel.create(manager, "hello35")
      println(s"Got channel: $channel")
      Source.single("hello").runWith(channel.sink)
      Source.single(BatchCmd("hello35",List(new msg_altitude(0,1)))).runWith(channel.sink)

      channel.source.groupedWithin(50, 5 seconds).collect {
        case x => BatchCmd("hello35",List(new msg_altitude(0,1)))
      }.runWith(channel.sink)
  }
}

package actors

import actors.Messages._
import akka.actor.{PoisonPill, Actor, ActorLogging, ActorRef}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Send
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import akka.stream.actor.ActorSubscriberMessage.{OnComplete, OnNext}
import akka.stream.actor.{ActorPublisher}
import mavlink.MAVlinkJsonSerrializer
import spray.json.JsValue

import scala.collection.mutable

/**
 * Created by yishchuk on 03.11.2015.
 */
class WebSocketActor(memberId: String) extends Actor with ActorLogging {

  def receive = {
    case src: ActorRef => println(s"src: $src")

    case TextMessage.Strict(text) =>
      println(s"ws: $text")

    case OnComplete => println("COMPLETE")

    case x => println(s"U: $x")

  }
}
class SseActor(memberId: String) extends MediatedTelemetryPublisherActor("SSE", msg => MAVlinkJsonSerrializer.MAVLink2Json(msg.message))(memberId)

class WsActor (memberId: String) extends MediatedTelemetryPublisherActor("WS", msg => TextMessage(MAVlinkJsonSerrializer.MAVLink2Json(msg.message).toString()))(memberId)

class SinkActor(memberId: String) extends Actor with ActorLogging {

  val mediator = DistributedPubSub(context.system).mediator

  override def preStart() = {
    //manager ! TelemetryDiscovery(memberId)
  }

  def receive = {
    case Some(tele: ActorRef) => println(s"tele: $tele")
      context become {
        case OnComplete => println("COMPLETE")

        case x => println(s"UTE: $x"); tele ! x

      }
    case None => println(s"Couldn't find transport for $memberId, exiting"); self ! PoisonPill

    case OnComplete => println("COMPLETE")

    case cmd: BinnaryCmd =>
      println(s"sending $cmd to member '$memberId'")
      mediator ! Send(s"/user/embedded.$memberId", cmd, true)

    case x => println(s"U: $x"); //tele ! x

  }
}

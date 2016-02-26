package api

import actors.{SinkActor, WebSocketActor, SseActor}
import akka.actor.{ActorSystem, Props, ActorRef}
import akka.stream.actor.ActorSubscriberMessage.OnComplete
import akka.stream.scaladsl.{Sink, Source}
import spray.json.JsValue
import scala.language.existentials

/**
 * Created by yishchuk on 06.11.2015.
 */
case class Channel[A, B, Mat](source: Source[A,Mat], sink: Sink[B,Mat])

object Channel {
  def create(memberId: String)(implicit system: ActorSystem) = {
    val src = Source.actorPublisher[JsValue](Props(classOf[SseActor], memberId))
    val sinkActor = system.actorOf(Props(classOf[SinkActor], memberId))
    val sink = Sink.actorRef(sinkActor, OnComplete)

    new Channel(src, sink)
  }
}
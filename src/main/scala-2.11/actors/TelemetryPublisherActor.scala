package actors

import actors.Messages.{TelemetryStopped, UnregisterListener, RegisterListener, MavLinkTelemetry}
import akka.actor.{ActorLogging, ActorRef}
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}

import scala.collection.mutable

/**
 * Created by yishchuk on 06.11.2015.
 */
class TelemetryPublisherActor[T](name: String, transform: MavLinkTelemetry => T)(producer: ActorRef, memberId: String) extends ActorPublisher[T] with ActorLogging {

  val q = mutable.Queue[T]()

  override def preStart = {
    producer ! RegisterListener(self, memberId)
  }

  override def postStop = {
    producer ! UnregisterListener(self, memberId)
  }
  def receive = {
    case mls: Array[MavLinkTelemetry] =>
      mls foreach { msg =>
        val output = transform(msg)
        if (isActive && totalDemand > 0)
          onNext(output)
        else q.enqueue(output)
      }

    case Request(n) => (1L to n).foreach(_=>if (q.nonEmpty) onNext(q.dequeue()))

    case Cancel => println(s"$name: Cancel received"); context stop self

    case TelemetryStopped => onComplete()

    case x => println(s"$name: $x")
  }
}
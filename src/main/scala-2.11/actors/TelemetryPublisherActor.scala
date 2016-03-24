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

import actors.Messages.{TelemetryStopped, UnregisterListener, RegisterListener, MavLinkTelemetry}
import akka.actor.{ActorLogging, ActorRef}
import akka.cluster.Cluster
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{SubscribeAck, Subscribe}
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

class MediatedTelemetryPublisherActor[T](name: String, transform: MavLinkTelemetry => T)(memberId: String) extends ActorPublisher[T] with ActorLogging {

  import scala.concurrent.duration._
  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global
  val cluster = Cluster(context.system)

  val mediator = DistributedPubSub(context.system).mediator

  val subscriptionRepeater = context.system.scheduler.schedule(100 millis, 500 millis, mediator, Subscribe(memberId, self))

  val q = mutable.Queue[T]()

  override def preStart = {

    //producer ! RegisterListener(self, memberId)
  }

  override def postStop = {
    //producer ! UnregisterListener(self, memberId)
  }
  def receive = {
    case SubscribeAck(Subscribe(memberId, None, `self`)) =>
      println(s"subscribed to telemetry from $memberId")
      subscriptionRepeater.cancel()

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

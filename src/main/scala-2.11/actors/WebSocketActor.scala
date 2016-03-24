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

class WsActor (manager:ActorRef, memberId: String) extends TelemetryPublisherActor("WS", msg => TextMessage(MAVlinkJsonSerrializer.MAVLink2Json(msg.message).toString()))(manager, memberId)

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

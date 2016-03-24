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

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
package extentions.web

import actors.Messages._
import actors._
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Send
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Max-Age`}
import akka.http.scaladsl.model.ws.{Message, UpgradeToWebsocket}
import akka.http.scaladsl.server.{Directives, ExpectedWebsocketRequestRejection}
import akka.stream.actor.ActorSubscriberMessage.OnComplete
import akka.util.ByteString
import de.heikoseeberger.akkasse.{EventStreamMarshalling, ServerSentEvent}
import utils.MemberUtils
//import utils.sse.EventStreamTypedMarshalling

//import akka.http.scaladsl.server.directives.HeaderDirectives._
//import akka.http.scaladsl.server.directives.RouteDirectives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.config.Config
import mavlink.MAVlinkJsonSerrializer
import spray.json.{JsArray, JsValue, JsonParser, ParserInput}

/**
 * Created by yishchuk on 29.10.2015.
 */
class SwarmHttpService(managerOpt: Option[ActorRef] = None)(implicit val system: ActorSystem, val config: Config) extends CorsSupport with EventStreamMarshalling {
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val cluster = Cluster(system)

  val mediator = DistributedPubSub(system).mediator

  SwarmDiscovery.discoverAndJoin()

  import akka.pattern.ask
  import akka.util.Timeout

  import scala.concurrent.duration._
  implicit val timeout = Timeout(5 seconds)
  import MemberUtils._
  import akka.cluster.Member

  override val corsAllowOrigins: List[String] = List("*")

  override val corsAllowedHeaders: List[String] = List("Origin", "X-Requested-With", "Content-Type", "Accept", "Accept-Encoding", "Accept-Language", "Host", "Referer", "User-Agent")

  override val corsAllowCredentials: Boolean = true

  override val optionsCorsHeaders: List[HttpHeader] = List[HttpHeader](
    `Access-Control-Allow-Headers`(corsAllowedHeaders.mkString(", ")),
    `Access-Control-Max-Age`(60 * 60 * 24 * 20), // cache pre-flight response for 20 days
    `Access-Control-Allow-Credentials`(corsAllowCredentials)
  )

  implicit val telemetryRawListMarshaller = Marshaller.opaque { trs: List[TelemetryRaw] => HttpResponse(OK, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, trs.map(tr=>s"${tr.bs} [${tr.memberId}]").mkString("\n")))  }

  implicit val mlTelemetryListMarshaller = Marshaller.opaque { mlts: List[MavLinkTelemetry] => HttpResponse(OK, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, mlts.map(mlt=>s"${mlt.message} [${mlt.memberId}]").mkString("\n")))  }

  implicit val memberListMarshaller = Marshaller.opaque { members: List[Member] =>
    val js = members.map(member => s"""{"id":"${member.id.getOrElse(member.roles.headOption.getOrElse(""))}", "address":"${member.address.toString}"}""").mkString(",")
    HttpResponse(OK, entity = HttpEntity(ContentTypes.`application/json`, s"[$js]" ))
  }

  implicit val jsonListMarshaller = Marshaller.opaque { jsResp: JsArray => HttpResponse(OK, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, jsResp.toString)) }

  def start() = {
    Http().bindAndHandle(cors { routes }, config.getString("http.interface"), config.getInt("http.port"))
  }

  def stringToSseMessage(event: String): ServerSentEvent = {
    ServerSentEvent(event, Some("published"))
  }
  def jsToSseMessage(event: JsValue): ServerSentEvent = {
    ServerSentEvent(event.toString(), Some("published"))
  }

  val routes = {
    import Directives._

    logRequestResult("swarm-akka") {
      path ("ws" / Segment) { memberId =>
        get {
          optionalHeaderValueByType[UpgradeToWebsocket]() {
            case Some(upgrade) if managerOpt.isDefined =>
              val source = Source.actorPublisher[Message](Props(classOf[WsActor], managerOpt.get, memberId))
              val sinkActor = system.actorOf(Props(classOf[WebSocketActor], memberId))
              val sink = Sink.actorRef(sinkActor, OnComplete)

              complete(upgrade.handleMessagesWithSinkSource(sink, source))
            case None          => reject(ExpectedWebsocketRequestRejection)
          }
        }
      } ~
      path ("sse" / Segment) { memberId =>
        //import EventStreamTypedMarshalling._
        get {
          complete {
            Source.actorPublisher(Props(classOf[SseActor], memberId)).map(jsToSseMessage)
          }

        }
      } ~
      path("") {
        get {
            getFromResource(s"static/index.html")
        }
      } ~
      pathPrefix("cmd") {
        pathPrefix("history" / Segment) { memberId =>
          get {
            complete {
              managerOpt match {
                case Some(manager) => (manager ? TelemetryHistory(memberId)).mapTo[List[TelemetryRaw]]
                case None => List.empty[TelemetryRaw]
              }
            }
          }
        } ~
        pathPrefix ("mlhistory" / Segment) { memberId =>
          get {
            complete {
              managerOpt match {
                case Some(manager) =>
                  val res = manager ? MavLinkTelemetryHistory(memberId)
                  res.mapTo[List[MavLinkTelemetry]].map {
                    lst:List[MavLinkTelemetry] => JsArray(lst.map(p => MAVlinkJsonSerrializer.MAVLink2Json(p.message)): _*)
                  }
                case None => List.empty[MavLinkTelemetry]
              }
            }
          }
        } ~
        path("list") {
          get {
            complete { cluster.state.members.toList }
          }
        } ~
        pathPrefix("send" / Segment) { memberId =>
          post {
            decodeRequest {
              entity(as[String]) { strCmd =>
                val batch = MAVlinkJsonSerrializer.toMavlik(JsonParser(ParserInput(strCmd)))
                val buff = batch.foldLeft(ByteString()) { (acc, item) => acc ++ ByteString(item.encode()) }
                mediator ! Send(s"/user/embedded.$memberId", BinnaryCmd(buff), true)
                //manager ! BatchCmd(memberId, batch)
                complete(OK)
              }
            }
          }
        }
      } ~
        getFromResourceDirectory("static")

    }
  }

}

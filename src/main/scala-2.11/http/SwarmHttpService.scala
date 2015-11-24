package http

import actors.{WsActor, SseActor, WebSocketActor, Messages}
import actors.Messages._
import akka.actor.{Cancellable, Props, ActorRef, ActorSystem}
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Headers`, `Access-Control-Max-Age`, `Access-Control-Allow-Credentials`}
import akka.http.scaladsl.model.ws.{TextMessage, UpgradeToWebsocket, Message}
import akka.http.scaladsl.server.{Directives, ExpectedWebsocketRequestRejection}
import akka.stream.actor.ActorSubscriberMessage.OnComplete
import de.heikoseeberger.akkasse.{MediaTypes, WithHeartbeats, ServerSentEvent, EventStreamMarshalling}
import utils.MemberUtils
import utils.sse.EventStreamTypedMarshalling

import scala.concurrent.ExecutionContext

//import akka.http.scaladsl.server.directives.HeaderDirectives._
//import akka.http.scaladsl.server.directives.RouteDirectives._
import com.typesafe.config.Config
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller, ToResponseMarshallable}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling._
import akka.stream.{ ActorMaterializer, Materializer }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import mavlink.MAVlinkJsonSerrializer
import spray.json.{JsArray, JsValue, JsonParser, ParserInput}

/**
 * Created by yishchuk on 29.10.2015.
 */
class SwarmHttpService(val manager: ActorRef)(implicit val system: ActorSystem, val config: Config) extends CorsSupport with EventStreamMarshalling {
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.duration._
  implicit val timeout = Timeout(5 seconds)
  import akka.cluster.Member
  import MemberUtils._

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
    val js = members.map(member => s"""{"id":"${member.id.getOrElse("")}", "address":"${member.address.toString}"}""").mkString(",")
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
            case Some(upgrade) =>
              val source = Source.actorPublisher[Message](Props(classOf[WsActor], manager, memberId))
              val sinkActor = system.actorOf(Props(classOf[WebSocketActor], memberId))
              val sink = Sink.actorRef(sinkActor, OnComplete)

              complete(upgrade.handleMessagesWithSinkSource(sink, source))
            case None          => reject(ExpectedWebsocketRequestRejection)
          }
        }
      } ~
      path ("sse" / Segment) { memberId =>
        import EventStreamTypedMarshalling._
        get {
          complete {
            Source.actorPublisher(Props(classOf[SseActor], manager, memberId)).map(jsToSseMessage)
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
            val res = manager ? TelemetryHistory(memberId)
            complete { res.mapTo[List[TelemetryRaw]] }
          }
        } ~
        pathPrefix ("mlhistory" / Segment) { memberId =>
          get {
            val res = manager ? MavLinkTelemetryHistory(memberId)
            complete {
              res.mapTo[List[MavLinkTelemetry]].map {
                lst:List[MavLinkTelemetry] => JsArray(lst.map(p => MAVlinkJsonSerrializer.MAVLink2Json(p.message)): _*)
              }
            }
          }
        } ~
        path("list") {
          get {
            val res = manager ? ListMembers
            complete { res.mapTo[List[Member]] }
          }
        } ~
        pathPrefix("send" / Segment) { memberId =>
          post {
            decodeRequest {
              entity(as[String]) { strCmd =>
                val batch = MAVlinkJsonSerrializer.toMavlik(JsonParser(ParserInput(strCmd)))
                manager ! BatchCmd(memberId, batch)
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

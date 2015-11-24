package utils.sse

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, HttpCharsets}
import akka.stream.scaladsl.Source
import de.heikoseeberger.akkasse.{MediaTypes, ServerSentEvent}

import scala.concurrent.ExecutionContext

/**
 * Created by yishchuk on 04.11.2015.
 */
trait EventStreamTypedMarshalling {
  implicit def toResponseMarshaller2[T <: Any](implicit ec: ExecutionContext): ToResponseMarshaller[Source[ServerSentEvent, T]] =
    Marshaller.withFixedCharset(MediaTypes.`text/event-stream`, HttpCharsets.`UTF-8`) { messages =>
      val data = messages.map(_.toByteString)
      HttpResponse(entity = HttpEntity.CloseDelimited(MediaTypes.`text/event-stream`, data))
    }
}

object EventStreamTypedMarshalling extends EventStreamTypedMarshalling
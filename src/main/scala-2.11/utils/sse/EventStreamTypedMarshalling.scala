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
package utils.sse

import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, HttpCharsets}
import akka.stream.scaladsl.Source
import de.heikoseeberger.akkasse.{MediaTypes, ServerSentEvent}

import scala.concurrent.ExecutionContext

/**
 * Created by yishchuk on 04.11.2015.
 */
//trait EventStreamTypedMarshalling {
//  implicit def toResponseMarshaller2[T <: Any](implicit ec: ExecutionContext): ToResponseMarshaller[Source[ServerSentEvent, T]] =
//    Marshaller.withContentType(ContentType(MediaTypes.`text/event-stream`, HttpCharsets.`UTF-8`)) { messages =>
//      val data = messages.map(_.toByteString)
//      HttpResponse(entity = HttpEntity.CloseDelimited(MediaTypes.`text/event-stream`, data))
//    }
//}
//
//object EventStreamTypedMarshalling extends EventStreamTypedMarshalling

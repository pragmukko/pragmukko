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

import java.io.FileOutputStream

import actors.Messages._
import akka.actor._
import akka.util.ByteString
import spray.json.JsValue
import utils.{ConfigProvider, CircularBuffer}

/**
 * Created by yishchuk on 29.10.2015.
 */
class TelemetryActor extends Actor with ActorLogging with ConfigProvider with Stash {
  val historySize = config.getInt("history.size")
  lazy val buf = new CircularBuffer[TelemetryRaw](historySize)
  lazy val mlBuf = new CircularBuffer[MavLinkTelemetry](historySize)

  var listeners: List[ActorRef] = Nil

  def receive = {

    case DevDiscover =>
      println(s"discovered device ${sender()}")
      context become paired(sender())
      unstashAll()

    case x => println(s"TA: $x"); stash()
  }

  def paired(device: ActorRef): Receive = {
    case TelemetryRaw(data, _) =>
      appendFile(data.toArray)

    case trs: Array[TelemetryRaw] =>
      trs foreach { tel =>
        buf.push(tel)
      }

    case mlts: Array[MavLinkTelemetry] =>
      mlts foreach {mlt =>
        mlBuf.push(mlt)
      }
      listeners foreach {_ ! mlts}

    case jsMsgBatch: Array[JsValue] =>
      listeners foreach { _ ! jsMsgBatch }

    case TelemetryHistory(_) =>
      sender() ! buf.getAll

    case MavLinkTelemetryHistory(_) =>
      sender() ! mlBuf.getAll

    case (MavLinkTelemetryHistory(_), replyTo: ActorRef) =>
      replyTo ! mlBuf.getAll

    case RegisterListener(listener, _) =>
      listeners = listener :: listeners
      log.info(s"registered $listener")

    case UnregisterListener(listener, _) =>
      listeners = listeners.filterNot(_ == listener)
      log.info(s"unregistered $listener")

    case BatchCmd(_, batch) =>
      val buff = batch.foldLeft(ByteString()) { (acc, item) => acc ++ ByteString(item.encode()) }
      device ! BinnaryCmd(buff)

    case x => println(s"TA-p: $x")
  }

  override def postStop() = {
    listeners foreach { _ ! TelemetryStopped }
  }

  def appendFile(buff:Array[Byte]) = {
    val os = new FileOutputStream(s"mavlinkdata.bin", true)
    os.write(buff)
    os.close()
  }

}

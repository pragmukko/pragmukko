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
package builders

import actors.Messages.Start
import actors.{SwarmDiscovery, BaseEmbeddedActor, ExtReceiver, MavlinkGateActor}
import akka.actor.{Props, ActorSystem, Actor}
import utils.ConfigProvider

/**
 * Created by max on 11/6/15.
 */
trait Pragma extends ConfigProvider {

  type ReceiverType = BaseEmbeddedActor => PartialFunction[Any, Unit]

  case class EmbeddedNodeBuilder[AC <: BaseEmbeddedActor, HW <: Actor](receiver: Option[ReceiverType],
    embeddedActor:Class[AC],
    hardwareGateActor:Option[Class[HW]]) {

    def start() = {
      val system = ActorSystem(config.getString("akka-sys-name"), config)
      val ref = system.actorOf(Props(embeddedActor, hardwareGateActor), s"embedded.${config.getString("member-id")}")
      receiver foreach {
        rcvr =>
          ref ! ExtReceiver(rcvr)
          ref ! Start
      }

      if (config.getBoolean("discovery.start-responder")) {
        SwarmDiscovery.startResponder(system, config)
        //system.actorOf(Props[UdpDiscoveryResponder], "discovery-responder")
      }
    }

    def withEmbedded[T <: BaseEmbeddedActor](implicit tag : reflect.ClassTag[T]) = {
      EmbeddedNodeBuilder(this.receiver, tag.runtimeClass.asInstanceOf[Class[T]], this.hardwareGateActor)
    }

    def withHWGate[T <: Actor](implicit tag : reflect.ClassTag[T]) = {
      EmbeddedNodeBuilder(this.receiver, this.embeddedActor, Some(tag.runtimeClass.asInstanceOf[Class[T]]))
    }

    def withReceiver(rcvr:ReceiverType) = {
      EmbeddedNodeBuilder(Some(rcvr), this.embeddedActor, hardwareGateActor)
    }
  }

  def builder() : EmbeddedNodeBuilder[_,_] = {
    EmbeddedNodeBuilder(None, classOf[BaseEmbeddedActor], Some(classOf[MavlinkGateActor]))
  }


  def apply(receiver: ReceiverType) = {
    EmbeddedNodeBuilder(Some(receiver), classOf[BaseEmbeddedActor], Some(classOf[MavlinkGateActor])).start();
  }

  def apply() = {
    Pragma.builder().start()
  }

}

object Pragma extends Pragma

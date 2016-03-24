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
package org.drones.platform

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

import akka.actor._
import com.sandinh.paho.akka.ByteArrayConverters.Long2Array
import com.typesafe.config.{ConfigFactory, Config}
import scala.concurrent.duration._
import com.sandinh.paho.akka._
import com.sandinh.paho.akka.MqttPubSub._

class MQTTPubSub {

}

object MQTTPubSub extends App {

  val system = ActorSystem("swarm")
  lazy val config = ConfigFactory.load()

  val pubsub = system.actorOf(Props(classOf[MqttPubSub], PSConfig(
    brokerUrl = config.getString("mosquitto.broker-url"), //all params is optional except brokerUrl
    userName = if (config.getIsNull("mosquitto.username") || config.getString("mosquitto.username").isEmpty) null else config.getString("mosquitto.username"),
    password = if (config.getIsNull("mosquitto.password") || config.getString("mosquitto.password").isEmpty) null else config.getString("mosquitto.password"),
    //messages received when disconnected will be stash. Messages isOverdue after stashTimeToLive will be discard
    stashTimeToLive = FiniteDuration(config.getDuration("mosquitto.stash-ttl").toMillis, TimeUnit.MILLISECONDS),
    stashCapacity = config.getInt("mosquitto.stash-capacity"), //stash messages will be drop first haft elems when reach this size
    reconnectDelayMin = FiniteDuration(config.getDuration("mosquitto.reconnect-delay-min").toMillis, TimeUnit.MILLISECONDS), //for fine tuning re-connection logic
    reconnectDelayMax = FiniteDuration(config.getDuration("mosquitto.reconnect-delay-max").toMillis, TimeUnit.MILLISECONDS)
  )))

  //pubsub ! new Publish("test", "Hello (payload)".getBytes)

  class SubscribeActor(topic: String) extends Actor {
    pubsub ! Subscribe(topic, self)

    def receive = {
      case SubscribeAck(Subscribe(`topic`, `self`, _)) =>
        context become ready
    }

    def ready: Receive = {
      case msg: Message if "stop" == new String(msg.payload) =>
        println("stopping..."); self ! PoisonPill
      case msg: Message if "ping" == new String(msg.payload) =>
        pubsub ! new Publish(topic, "pong".getBytes)
      case msg: Message =>
        println(s"${msg.topic}: ${new String(msg.payload)}")
    }
  }

  val sub1 = system.actorOf(Props(classOf[SubscribeActor], "test"))

  val sub2 = system.actorOf(Props(classOf[SubscribeActor], "test2"))

  sys.addShutdownHook { system.terminate(); println("shutting down...") }
}


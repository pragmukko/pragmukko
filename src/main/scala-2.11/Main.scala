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


import actors.ClientActor
import akka.actor.{ActorSystem, Props}
import builders.{WebNode, Pragma, PragmaCap}
import utils.ConfigProvider


/**
 * Created by max on 10/26/15.
 */
object Main extends App with ConfigProvider {

  config.getString("type").toLowerCase match {
    case "embedded" => initEmbeddedRole()
    case "manager" => initManagerRole()
    case "client" => initClientRole()
    case "web" => initWebRole()
  }

  def initEmbeddedRole() = {
    println("Init embedded actor")
    Pragma()
  }

  def initManagerRole() = {
    println("Init manager actor")
    PragmaCap(restEndpoint = true)
    //WebNode()
  }

  def initClientRole() = {
    println("Init Client actor")
    val system = ActorSystem(config.getString("akka-sys-name"), config)
    system.actorOf(Props(classOf[ClientActor], "test"), "client")
  }

  def initWebRole() = {
    println("Init web")
    WebNode()
  }
}


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

import actors.SwarmDiscovery
import akka.actor.{Props, ActorSystem, Actor}
import http.SwarmHttpService
import utils.ConfigProvider

/**
 * Created by yishchuk on 18.11.2015.
 */
object WebNode extends ConfigProvider {
  def apply() = {
    val system = ActorSystem(config.getString("akka-sys-name"), config)

    new SwarmHttpService()(system, config).start()

    if (config.getBoolean("discovery.start-responder")) {
      SwarmDiscovery.startResponder(system, config)
    }

  }
}

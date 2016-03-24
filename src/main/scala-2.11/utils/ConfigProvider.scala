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
package utils

import com.typesafe.config.{ConfigValueFactory, ConfigFactory}
import scala.collection.JavaConversions._

/**
 * Created by max on 10/28/15.
 */
trait ConfigProvider {
  private val hostPath = "akka.remote.netty.tcp.hostname"

  lazy protected val additionalConfig = List.empty[(String, _ <: AnyRef)]

  private def configMap: Map[String, _ <: AnyRef] = (additionalConfig).toMap

  lazy val config = {

    var cfg = ConfigFactory.load()
    if (cfg.hasPath(hostPath) && (cfg.getString(hostPath) == "127.0.0.1" || cfg.getString(hostPath) == "localhost" || cfg.getString(hostPath) == "" )) {
      cfg = cfg.withValue(hostPath, ConfigValueFactory.fromAnyRef(NetUtils.localHost.getHostAddress))
    }
    cfg.withFallback(ConfigFactory.parseMap(configMap))
  }

}

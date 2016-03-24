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

import java.net.{InetAddress, NetworkInterface}

import akka.actor.Address
import com.typesafe.config.Config
import org.slf4j.LoggerFactory

/**
 * Created by yishchuk on 12.11.2015.
 */
object NetUtils {

  import scala.collection.JavaConverters._

  def logger = LoggerFactory.getLogger(this.getClass.getSimpleName)

  def broadcastAddress(cfg:Config) : InetAddress = {
    val path = "akka.remote.netty.tcp.hostname"
    val addr:InetAddress = {
      if (cfg.hasPath(path))
        InetAddress.getByName(cfg.getString(path))
      else {
        logger.info("Used local address " + localHost)
        localHost
      }
    }

    NetworkInterface.getNetworkInterfaces.asScala.toList.flatMap { ni =>
      ni.getInterfaceAddresses.asScala.toList
    }
      .filter(ia => ia.getAddress == addr)
      .map(ia =>  ia.getBroadcast)
      .head

  }

  lazy val localHost = {
    val local = NetworkInterface.getNetworkInterfaces.asScala flatMap { ni =>
      if (ni.isLoopback) None
      else {
        ni.getInterfaceAddresses.asScala.map(ia=>(Option(ia.getBroadcast), ia.getAddress)).collectFirst{
          case (Some(_), address) => address
        }
      }
    }
    val localHostName = local.next()
    localHostName
  }

}

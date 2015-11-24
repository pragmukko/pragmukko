package utils

import com.typesafe.config.{ConfigValue, ConfigFactory}

/**
 * Created by max on 10/28/15.
 */
trait ConfigProvider {

  private val addConfigStr = s""" akka.remote.netty.tcp.hostname = "${NetUtils.localHost.getHostAddress}"  """

  lazy val config = ConfigFactory.parseString(addConfigStr).withFallback(ConfigFactory.load())

}

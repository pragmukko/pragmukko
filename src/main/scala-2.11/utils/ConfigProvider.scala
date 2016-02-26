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
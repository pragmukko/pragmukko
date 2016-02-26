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

package utils

import java.net.{InetAddress, NetworkInterface}

import akka.actor.Address

/**
 * Created by yishchuk on 12.11.2015.
 */
object NetUtils {

  import scala.collection.JavaConverters._

  lazy val broadcastAddress = {
    val bcast = NetworkInterface.getNetworkInterfaces.asScala.toList flatMap { ni =>
      if (ni.isLoopback) None
      else {
        ni.getInterfaceAddresses.asScala flatMap { ia => Option(ia.getBroadcast) }
      }
    }
    bcast.head
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
    println(s"localhost: ${localHostName.getHostAddress}")
    localHostName
  }

}

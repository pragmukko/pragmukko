package MAVLink

import java.nio.file.{Files, Paths}

import akka.util.ByteString
import mavlink.MAVLinkParser
import org.mavlink.messages.MAVLinkMessage
import org.mavlink.messages.pixhawk.msg_attitude
import org.specs2.mutable._

/**
 * Created by max on 10/29/15.
 */
class MAVLinkParserSpec extends Specification {

  "MAVLinkParser " should {

    "parse MAVLink message stream" in {
      val initialAttitude = new msg_attitude()
      initialAttitude.yaw = 3.0f
      initialAttitude.pitch = 1.0f

      val buff1 = ByteString(initialAttitude.encode())
      val lst = MAVLinkParser.parse(buff1)
      val attittude = lst.head.asInstanceOf[msg_attitude]

      attittude.yaw must_==(3.0f)
    }

    "parse MAVLink stream" in {
      val buff = Files.readAllBytes(Paths.get("./src/test/resources/mavlinkdump/mavlinkdata.bin"))
      //val buff = Files.readAllBytes(Paths.get("mavlinkdata.bin"))
      val lst = MAVLinkParser.parse(ByteString(buff))
      lst.size must_==(5105)
    }

  }

}

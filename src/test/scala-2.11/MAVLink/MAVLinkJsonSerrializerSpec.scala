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
package MAVLink

import mavlink.MAVlinkJsonSerrializer
import org.mavlink.messages.pixhawk.{msg_highres_imu, msg_battery_status}
import org.specs2.mutable._
import spray.json.{ParserInput, JsonParser}

import scala.util.{Failure, Success}

/**
 * Created by max on 10/30/15.
 */
class MAVLinkJsonSerrializerSpec extends Specification {

  import utils.JsValueUtils._

  "MAVLinkJsonSerrializer " should {

    "Parse JSON in to MAVLinkMessage" in {
      val json =
        """{
          | "MAVType": "msg_battery_status",
          | "current_consumed": 1,
          | "temperature": 2,
          | "battery_remaining": 5,
          | "voltages" : [1, 2, 3, 4, 5, 6, 7, 8, 9, 0]
          | }""".stripMargin

      val msg = MAVlinkJsonSerrializer
        .parseJsObjToMavlink(JsonParser(ParserInput(json)).asJsObject)
        .asInstanceOf[msg_battery_status]


      msg.current_consumed === 1 and
        msg.temperature === 2 and
        msg.battery_remaining === 5 and
        msg.voltages === Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)
    }


    "Serrialize MAVLink message to JSON" in {

      val msg = new msg_highres_imu()
      msg.xacc = 1.0f
      msg.time_usec = 10000
      msg.yacc = 2.0f
      msg.zacc = 3.0f

      val json = MAVlinkJsonSerrializer.MAVLink2Json(msg)
      json.fields("xacc").asFloat === 1.0f and
        json.fields("yacc").asFloat === 2.0f and
        json.fields("zacc").asFloat === 3.0f and
        json.fields("time_usec").asLong === 10000 and
        json.fields("MAVType").asString === "msg_highres_imu"

    }

    "Desserialize serrialized message (end-to-end check)" in {

      val msg = new msg_highres_imu()
      msg.xacc = 1.0f
      msg.time_usec = 10000
      msg.yacc = 2.0f
      msg.zacc = 3.0f

      val des = MAVlinkJsonSerrializer.parseJsObjToMavlink(MAVlinkJsonSerrializer.MAVLink2Json(msg))
        .asInstanceOf[msg_highres_imu]

      des.xacc === 1.0f and des.yacc === 2.0f and des.zacc == 3.0f and des.time_usec == 10000

    }

  }

}

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
import java.io.FileOutputStream

import akka.util.ByteString
import org.mavlink.messages.pixhawk.msg_local_position_ned

import scala.util.Random

/**
 * Created by max on 11/5/15.
 */
object MAVLinkDataGenerator extends App {

  var positionX = 0f
  var positionY = 0f
  var startTime = 0l


  var duration = 1000l
  val buff = (1 to 40).foldLeft(ByteString()) {
    (acc, i) =>
          val (x,y) =  {
              if (i % 4 == 0 )
                (0f, -0.1f)
             else if (i % 4 == 1f)
                  (-0.1f, 0f)
             else if (i % 4 == 2f)
                  (0f, 0.1f)
              else
                (0.1f, 0f)
            }
          duration += 1000l
          acc ++ generateDirection(x, y, duration)
  }

  val os = new FileOutputStream(s"mavlinkdata.bin")
  os.write(buff.toArray)
  os.close()


  def generateDirection(vx:Float, vy:Float, duration:Long ) : ByteString = {
    // assuming data is sending 100 times per second
    (0l to (duration / 100l)).map {
      ts =>
        startTime += ts
        positionX += vx
        positionY += vy
        val msg = new msg_local_position_ned()
        msg.x = positionX
        msg.y = positionY
        msg.time_boot_ms = startTime + ts
        msg.vx = vx * (1 + Random.nextFloat() * 3)
        msg.vy = vy * (1 + Random.nextFloat() * 3)
        msg.vz = Random.nextFloat() * 3
        ByteString(msg.encode())
    }.foldLeft(ByteString())( _ ++ _ )
  }

}

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
import java.security.SecureRandom

import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by yishchuk on 30.10.2015.
 */
object MavLinkTcpServer extends App {
  import java.net._
  import java.io._
  import scala.io._

  val rnd = new SecureRandom()

  val server = new ServerSocket(8080)
  while (true) {
    val s = server.accept()
    println("Connection open " + s.getInetAddress)
    val input = new FileInputStream("src/test/resources/mavlinkdump/mavlinkdata.bin")
    val buf = Array.ofDim[Byte](1024)

    Future {
      Try {
        val out = s.getOutputStream()
        while (true) {
          val length = rnd.nextInt(420)
          input.read(buf, 0, length)
          out.write(buf, 0, length)
          out.flush()
          Thread.sleep(10)
        }
        println("Connection closed")
      } recover {
        case _ => input.close()
      }
    }
  }
}

import java.nio.file.{Paths, Files}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

object SimpleTcpServer extends App {

  import java.net._
  import java.io._
  import scala.io._

  val server = new ServerSocket(8080)
  println("Server started")
  while (true) {
    val s = server.accept()
    println("Connection open " + s.getInetAddress)
    Future {
      val dataPath = "mavlinkdata.bin"
      //val buff = Files.readAllBytes(Paths.get("./src/test/resources/mavlinkdump/mavlinkdata.bin"))
      val buff = Files.readAllBytes(Paths.get(dataPath))
      var offset = 0
      val out = new PrintStream(s.getOutputStream())

      while (!out.checkError()) {
        val size = (Random.nextDouble() * 64).toInt
        if ( (offset + size) >= buff.length) {
          offset = 0
        }
        out.write(buff, offset, size)
        offset += size
        out.flush()
        Thread.sleep(10)
      }
      s.close()
      println("Connection closed")
    }
  }
}

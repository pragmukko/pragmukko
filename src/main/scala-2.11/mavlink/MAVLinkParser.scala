package mavlink

import java.io.{EOFException, ByteArrayInputStream, DataInputStream}
import java.nio.ByteBuffer

import akka.util.ByteString
import org.mavlink.messages.MAVLinkMessage
import org.mavlink.{IMAVLinkMessage, MAVLinkReader}

import scala.annotation.tailrec
import scala.util.{Success, Failure, Try}

/**
 * Created by max on 10/28/15.
 */
object MAVLinkParser {

  def parse(bs:ByteString) : List[MAVLinkMessage] = {
    val dis = new DataInputStream(new ByteArrayInputStream(bs.toArray))
    val msgList = readMessages(new MAVLinkReader(dis, IMAVLinkMessage.MAVPROT_PACKET_START_V10))
    msgList
  }

  @tailrec
  private def readMessages(reader:MAVLinkReader, result:List[MAVLinkMessage] = Nil) : List[MAVLinkMessage] = {
    Try { reader.getNextMessage } match {
      case Success(msg:MAVLinkMessage) => readMessages(reader, result :+ msg)
      case Success(null) => result
      case Failure(th:EOFException) => result
      case _ => result
    }
  }

}

package mavlink.pixhawk

import actors.Messages.{MavLinkTelemetry, BinnaryCmd}
import akka.util.ByteString
import mavlink.MAVLinkParser
import org.mavlink.messages.MAVLinkMessage
import org.mavlink.messages.pixhawk.{msg_set_position_target_local_ned, msg_position_target_local_ned, msg_ping, msg_local_position_ned}

/**
 * Created by max on 11/11/15.
 */
trait DroneCommands {

  def moveTo(x:Float, y:Float, z:Float, vx:Float = 0, vy:Float = 0, vz:Float = 0) : BinnaryCmd = {
    val msg = new msg_set_position_target_local_ned()
    msg.x = x
    msg.y = y
    msg.z = z
    msg.vx = vx
    msg.vy = vy
    msg.vz = vz
    msg.type_mask = 0xFFF8
    BinnaryCmd(ByteString(msg.encode()))
  }

}

object Ping {
  def unapply(cmd:BinnaryCmd) : Option[msg_ping] = {
    MAVLinkParser.parse(cmd.bin).headOption match {
      case Some(x:msg_ping) => Some(x)
      case _ => None
    }
  }
}

object SetPosition {
  def unapply(cmd:BinnaryCmd) : Option[msg_set_position_target_local_ned] = {
    MAVLinkParser.parse(cmd.bin).headOption match {
      case Some(x:msg_set_position_target_local_ned) => Some(x)
      case _ => None
    }
  }
}

object DronePosition {
  def unapply(message:MAVLinkMessage) : Option[Position] = {
    message match {
      case p:msg_local_position_ned => Some(
        Position(p.x, p.y, p.z, p.vx, p.vy, p.vz)
      )
      case other =>
        //println(other)
        None
    }
  }
}

case class Position(x:Float, y:Float, z:Float, vx:Float, vy:Float, vz:Float) {
  override def toString =
    s"""x = $x
        |y = $y
        |z = $z
        |vx = $vx
        |vy = $vy
        |vz = $vz
       """.stripMargin

  def dueProtocol = {
    val msg = new msg_local_position_ned()
    msg.x = x
    msg.y = y
    msg.z = z
    msg.vx = vx
    msg.vy = vy
    msg.vz = vz
    msg
  }

}

object TelemetryBatch {
  def unapply(arr:Array[MavLinkTelemetry]) : Option[List[MAVLinkMessage]] = {
    Some( arr.map(_.message).toList )
  }
}

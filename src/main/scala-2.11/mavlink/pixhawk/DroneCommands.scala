package mavlink.pixhawk

import actors.Messages.{MavLinkTelemetry, BinnaryCmd}
import akka.util.ByteString
import mavlink.MAVLinkParser
import org.mavlink.messages.MAVLinkMessage
import org.mavlink.messages.pixhawk._

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

  def moveToGlobal(lat: Long, lon: Long, alt: Float, vx:Float = 0, vy:Float = 0, vz:Float = 0) : BinnaryCmd = {
    val msg = new msg_set_position_target_global_int()
    msg.lat_int = lat
    msg.lon_int = lon
    msg.alt = alt
    msg.vx = vx
    msg.vy = vy
    msg.vz = vz
    msg.type_mask = 0xFFF8 // TODO YI check 'type_mask'
    // TODO YI should 'coordinate_frame' be specified as well?
    BinnaryCmd(ByteString(msg.encode()))
  }

  def direction(vx:Float, vy:Float, vz:Float = 0) = moveTo(0, 0, 0, vx, vy, vz )

}

object DroneCommands extends DroneCommands

object Ping {
  def unapply(cmd:BinnaryCmd) : Option[msg_ping] = {
    MAVLinkParser.parse(cmd.bin).headOption match {
      case Some(x:msg_ping) => Some(x)
      case _ => None
    }
  }
}

object SetPositionLocal {
  def unapply(cmd:BinnaryCmd) : Option[msg_set_position_target_local_ned] = {
    MAVLinkParser.parse(cmd.bin).headOption match {
      case Some(x:msg_set_position_target_local_ned) => Some(x)
      case _ => None
    }
  }
}

object SetPositionGlobal {
  def unapply(cmd:BinnaryCmd) : Option[msg_set_position_target_global_int] = {
    MAVLinkParser.parse(cmd.bin).headOption match {
      case Some(x:msg_set_position_target_global_int) => Some(x)
      case _ => None
    }
  }
}

object DronePositionLocal {
  def unapply(message:MAVLinkMessage) : Option[PositionLocal] = {
    message match {
      case p:msg_local_position_ned => Some(
        PositionLocal(p.x, p.y, p.z, p.vx, p.vy, p.vz)
      )
      case other =>
        //println(other)
        None
    }
  }
}

object DronePositionNed {
  def unapply(message:MAVLinkMessage) : Option[PositionNed] = {
    message match {
      case p: msg_position_target_local_ned => Some(
        PositionNed(p.x, p.y, p.z, p.vx, p.vy, p.vz)
      )
      case other =>
        //println(other)
        None
    }
  }
}

object DronePositionGlobal {
  def unapply(message:MAVLinkMessage) : Option[PositionGlobal] = {
    message match {
      case p: msg_global_position_int => Some(PositionGlobal(p.lat, p.lon, p.alt, p.vx, p.vy, p.vz))
      case other =>
        //println(other)
        None
    }
  }
}

case class PositionGlobal(lat:Long, lon:Long, alt:Long, vx:Float, vy:Float, vz:Float) {
  override def toString =
    s"""lat = $lat
        |lon = $lon
        |alt = $alt
        |vx = $vx
        |vy = $vy
        |vz = $vz
       """.stripMargin

}

case class PositionNed(x:Float, y:Float, z:Float, vx:Float, vy:Float, vz:Float)

case class PositionLocal(x:Float, y:Float, z:Float, vx:Float, vy:Float, vz:Float) {
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

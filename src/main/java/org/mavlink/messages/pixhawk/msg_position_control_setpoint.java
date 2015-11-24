/**
 * Generated class : msg_position_control_setpoint
 * DO NOT MODIFY!
 **/
package org.mavlink.messages.pixhawk;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.IMAVLinkCRC;
import org.mavlink.MAVLinkCRC;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.mavlink.io.LittleEndianDataInputStream;
import org.mavlink.io.LittleEndianDataOutputStream;
/**
 * Class msg_position_control_setpoint
 * 
 **/
public class msg_position_control_setpoint extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT = 170;
  private static final long serialVersionUID = MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT;
  public msg_position_control_setpoint() {
    this(1,1);
}
  public msg_position_control_setpoint(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 18;
}

  /**
   * x position
   */
  public float x;
  /**
   * y position
   */
  public float y;
  /**
   * z position
   */
  public float z;
  /**
   * yaw orientation in radians, 0 = NORTH
   */
  public float yaw;
  /**
   * ID of waypoint, 0 for plain position
   */
  public int id;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  x = (float)dis.readFloat();
  y = (float)dis.readFloat();
  z = (float)dis.readFloat();
  yaw = (float)dis.readFloat();
  id = (int)dis.readUnsignedShort()&0x00FFFF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+18];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeFloat(x);
  dos.writeFloat(y);
  dos.writeFloat(z);
  dos.writeFloat(yaw);
  dos.writeShort(id&0x00FFFF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 18);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[24] = crcl;
  buffer[25] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_POSITION_CONTROL_SETPOINT : " +   "  x="+x+  "  y="+y+  "  z="+z+  "  yaw="+yaw+  "  id="+id;}
}

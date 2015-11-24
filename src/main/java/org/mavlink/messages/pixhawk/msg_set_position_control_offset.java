/**
 * Generated class : msg_set_position_control_offset
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
 * Class msg_set_position_control_offset
 * Message sent to the MAV to set a new offset from the currently controlled position
 **/
public class msg_set_position_control_offset extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET = 160;
  private static final long serialVersionUID = MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET;
  public msg_set_position_control_offset() {
    this(1,1);
}
  public msg_set_position_control_offset(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 18;
}

  /**
   * x position offset
   */
  public float x;
  /**
   * y position offset
   */
  public float y;
  /**
   * z position offset
   */
  public float z;
  /**
   * yaw orientation offset in radians, 0 = NORTH
   */
  public float yaw;
  /**
   * System ID
   */
  public int target_system;
  /**
   * Component ID
   */
  public int target_component;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  x = (float)dis.readFloat();
  y = (float)dis.readFloat();
  z = (float)dis.readFloat();
  yaw = (float)dis.readFloat();
  target_system = (int)dis.readUnsignedByte()&0x00FF;
  target_component = (int)dis.readUnsignedByte()&0x00FF;
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
  dos.writeByte(target_system&0x00FF);
  dos.writeByte(target_component&0x00FF);
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
return "MAVLINK_MSG_ID_SET_POSITION_CONTROL_OFFSET : " +   "  x="+x+  "  y="+y+  "  z="+z+  "  yaw="+yaw+  "  target_system="+target_system+  "  target_component="+target_component;}
}

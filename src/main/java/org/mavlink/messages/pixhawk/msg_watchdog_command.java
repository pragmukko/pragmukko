/**
 * Generated class : msg_watchdog_command
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
 * Class msg_watchdog_command
 * 
 **/
public class msg_watchdog_command extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_WATCHDOG_COMMAND = 183;
  private static final long serialVersionUID = MAVLINK_MSG_ID_WATCHDOG_COMMAND;
  public msg_watchdog_command() {
    this(1,1);
}
  public msg_watchdog_command(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_WATCHDOG_COMMAND;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 6;
}

  /**
   * Watchdog ID
   */
  public int watchdog_id;
  /**
   * Process ID
   */
  public int process_id;
  /**
   * Target system ID
   */
  public int target_system_id;
  /**
   * Command ID
   */
  public int command_id;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  watchdog_id = (int)dis.readUnsignedShort()&0x00FFFF;
  process_id = (int)dis.readUnsignedShort()&0x00FFFF;
  target_system_id = (int)dis.readUnsignedByte()&0x00FF;
  command_id = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+6];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeShort(watchdog_id&0x00FFFF);
  dos.writeShort(process_id&0x00FFFF);
  dos.writeByte(target_system_id&0x00FF);
  dos.writeByte(command_id&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 6);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[12] = crcl;
  buffer[13] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_WATCHDOG_COMMAND : " +   "  watchdog_id="+watchdog_id+  "  process_id="+process_id+  "  target_system_id="+target_system_id+  "  command_id="+command_id;}
}

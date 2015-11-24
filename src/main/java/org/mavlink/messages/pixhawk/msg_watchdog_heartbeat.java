/**
 * Generated class : msg_watchdog_heartbeat
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
 * Class msg_watchdog_heartbeat
 * 
 **/
public class msg_watchdog_heartbeat extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT = 180;
  private static final long serialVersionUID = MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT;
  public msg_watchdog_heartbeat() {
    this(1,1);
}
  public msg_watchdog_heartbeat(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 4;
}

  /**
   * Watchdog ID
   */
  public int watchdog_id;
  /**
   * Number of processes
   */
  public int process_count;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  watchdog_id = (int)dis.readUnsignedShort()&0x00FFFF;
  process_count = (int)dis.readUnsignedShort()&0x00FFFF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+4];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeShort(watchdog_id&0x00FFFF);
  dos.writeShort(process_count&0x00FFFF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 4);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[10] = crcl;
  buffer[11] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_WATCHDOG_HEARTBEAT : " +   "  watchdog_id="+watchdog_id+  "  process_count="+process_count;}
}

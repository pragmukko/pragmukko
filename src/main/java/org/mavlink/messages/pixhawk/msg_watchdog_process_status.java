/**
 * Generated class : msg_watchdog_process_status
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
 * Class msg_watchdog_process_status
 * 
 **/
public class msg_watchdog_process_status extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS = 182;
  private static final long serialVersionUID = MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS;
  public msg_watchdog_process_status() {
    this(1,1);
}
  public msg_watchdog_process_status(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 12;
}

  /**
   * PID
   */
  public long pid;
  /**
   * Watchdog ID
   */
  public int watchdog_id;
  /**
   * Process ID
   */
  public int process_id;
  /**
   * Number of crashes
   */
  public int crashes;
  /**
   * Is running / finished / suspended / crashed
   */
  public int state;
  /**
   * Is muted
   */
  public int muted;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  pid = (int)dis.readInt();
  watchdog_id = (int)dis.readUnsignedShort()&0x00FFFF;
  process_id = (int)dis.readUnsignedShort()&0x00FFFF;
  crashes = (int)dis.readUnsignedShort()&0x00FFFF;
  state = (int)dis.readUnsignedByte()&0x00FF;
  muted = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+12];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(pid&0x00FFFFFFFF));
  dos.writeShort(watchdog_id&0x00FFFF);
  dos.writeShort(process_id&0x00FFFF);
  dos.writeShort(crashes&0x00FFFF);
  dos.writeByte(state&0x00FF);
  dos.writeByte(muted&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 12);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[18] = crcl;
  buffer[19] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_WATCHDOG_PROCESS_STATUS : " +   "  pid="+pid+  "  watchdog_id="+watchdog_id+  "  process_id="+process_id+  "  crashes="+crashes+  "  state="+state+  "  muted="+muted;}
}

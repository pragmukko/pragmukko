/**
 * Generated class : msg_watchdog_process_info
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
 * Class msg_watchdog_process_info
 * 
 **/
public class msg_watchdog_process_info extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO = 181;
  private static final long serialVersionUID = MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO;
  public msg_watchdog_process_info() {
    this(1,1);
}
  public msg_watchdog_process_info(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 255;
}

  /**
   * Timeout (seconds)
   */
  public long timeout;
  /**
   * Watchdog ID
   */
  public int watchdog_id;
  /**
   * Process ID
   */
  public int process_id;
  /**
   * Process name
   */
  public char[] name = new char[100];
  public void setName(String tmp) {
    int len = Math.min(tmp.length(), 100);
    for (int i=0; i<len; i++) {
      name[i] = tmp.charAt(i);
    }
    for (int i=len; i<100; i++) {
      name[i] = 0;
    }
  }
  public String getName() {
    String result="";
    for (int i=0; i<100; i++) {
      if (name[i] != 0) result=result+name[i]; else break;
    }
    return result;
  }
  /**
   * Process arguments
   */
  public char[] arguments = new char[147];
  public void setArguments(String tmp) {
    int len = Math.min(tmp.length(), 147);
    for (int i=0; i<len; i++) {
      arguments[i] = tmp.charAt(i);
    }
    for (int i=len; i<147; i++) {
      arguments[i] = 0;
    }
  }
  public String getArguments() {
    String result="";
    for (int i=0; i<147; i++) {
      if (arguments[i] != 0) result=result+arguments[i]; else break;
    }
    return result;
  }
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  timeout = (int)dis.readInt();
  watchdog_id = (int)dis.readUnsignedShort()&0x00FFFF;
  process_id = (int)dis.readUnsignedShort()&0x00FFFF;
  for (int i=0; i<100; i++) {
    name[i] = (char)dis.readByte();
  }
  for (int i=0; i<147; i++) {
    arguments[i] = (char)dis.readByte();
  }
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+255];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(timeout&0x00FFFFFFFF));
  dos.writeShort(watchdog_id&0x00FFFF);
  dos.writeShort(process_id&0x00FFFF);
  for (int i=0; i<100; i++) {
    dos.writeByte(name[i]);
  }
  for (int i=0; i<147; i++) {
    dos.writeByte(arguments[i]);
  }
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 255);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[261] = crcl;
  buffer[262] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_WATCHDOG_PROCESS_INFO : " +   "  timeout="+timeout+  "  watchdog_id="+watchdog_id+  "  process_id="+process_id+  "  name="+getName()+  "  arguments="+getArguments();}
}

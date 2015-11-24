/**
 * Generated class : msg_point_of_interest
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
 * Class msg_point_of_interest
 * Notifies the operator about a point of interest (POI). This can be anything detected by the
                system. This generic message is intented to help interfacing to generic visualizations and to display
                the POI on a map.
 **/
public class msg_point_of_interest extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_POINT_OF_INTEREST = 191;
  private static final long serialVersionUID = MAVLINK_MSG_ID_POINT_OF_INTEREST;
  public msg_point_of_interest() {
    this(1,1);
}
  public msg_point_of_interest(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_POINT_OF_INTEREST;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 43;
}

  /**
   * X Position
   */
  public float x;
  /**
   * Y Position
   */
  public float y;
  /**
   * Z Position
   */
  public float z;
  /**
   * 0: no timeout, >1: timeout in seconds
   */
  public int timeout;
  /**
   * 0: Notice, 1: Warning, 2: Critical, 3: Emergency, 4: Debug
   */
  public int type;
  /**
   * 0: blue, 1: yellow, 2: red, 3: orange, 4: green, 5: magenta
   */
  public int color;
  /**
   * 0: global, 1:local
   */
  public int coordinate_system;
  /**
   * POI name
   */
  public char[] name = new char[26];
  public void setName(String tmp) {
    int len = Math.min(tmp.length(), 26);
    for (int i=0; i<len; i++) {
      name[i] = tmp.charAt(i);
    }
    for (int i=len; i<26; i++) {
      name[i] = 0;
    }
  }
  public String getName() {
    String result="";
    for (int i=0; i<26; i++) {
      if (name[i] != 0) result=result+name[i]; else break;
    }
    return result;
  }
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  x = (float)dis.readFloat();
  y = (float)dis.readFloat();
  z = (float)dis.readFloat();
  timeout = (int)dis.readUnsignedShort()&0x00FFFF;
  type = (int)dis.readUnsignedByte()&0x00FF;
  color = (int)dis.readUnsignedByte()&0x00FF;
  coordinate_system = (int)dis.readUnsignedByte()&0x00FF;
  for (int i=0; i<26; i++) {
    name[i] = (char)dis.readByte();
  }
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+43];
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
  dos.writeShort(timeout&0x00FFFF);
  dos.writeByte(type&0x00FF);
  dos.writeByte(color&0x00FF);
  dos.writeByte(coordinate_system&0x00FF);
  for (int i=0; i<26; i++) {
    dos.writeByte(name[i]);
  }
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 43);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[49] = crcl;
  buffer[50] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_POINT_OF_INTEREST : " +   "  x="+x+  "  y="+y+  "  z="+z+  "  timeout="+timeout+  "  type="+type+  "  color="+color+  "  coordinate_system="+coordinate_system+  "  name="+getName();}
}

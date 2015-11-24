/**
 * Generated class : msg_brief_feature
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
 * Class msg_brief_feature
 * 
 **/
public class msg_brief_feature extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_BRIEF_FEATURE = 195;
  private static final long serialVersionUID = MAVLINK_MSG_ID_BRIEF_FEATURE;
  public msg_brief_feature() {
    this(1,1);
}
  public msg_brief_feature(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_BRIEF_FEATURE;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 53;
}

  /**
   * x position in m
   */
  public float x;
  /**
   * y position in m
   */
  public float y;
  /**
   * z position in m
   */
  public float z;
  /**
   * Harris operator response at this location
   */
  public float response;
  /**
   * Size in pixels
   */
  public int size;
  /**
   * Orientation
   */
  public int orientation;
  /**
   * Orientation assignment 0: false, 1:true
   */
  public int orientation_assignment;
  /**
   * Descriptor
   */
  public int[] descriptor = new int[32];
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  x = (float)dis.readFloat();
  y = (float)dis.readFloat();
  z = (float)dis.readFloat();
  response = (float)dis.readFloat();
  size = (int)dis.readUnsignedShort()&0x00FFFF;
  orientation = (int)dis.readUnsignedShort()&0x00FFFF;
  orientation_assignment = (int)dis.readUnsignedByte()&0x00FF;
  for (int i=0; i<32; i++) {
    descriptor[i] = (int)dis.readUnsignedByte()&0x00FF;
  }
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+53];
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
  dos.writeFloat(response);
  dos.writeShort(size&0x00FFFF);
  dos.writeShort(orientation&0x00FFFF);
  dos.writeByte(orientation_assignment&0x00FF);
  for (int i=0; i<32; i++) {
    dos.writeByte(descriptor[i]&0x00FF);
  }
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 53);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[59] = crcl;
  buffer[60] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_BRIEF_FEATURE : " +   "  x="+x+  "  y="+y+  "  z="+z+  "  response="+response+  "  size="+size+  "  orientation="+orientation+  "  orientation_assignment="+orientation_assignment+  "  descriptor="+descriptor;}
}

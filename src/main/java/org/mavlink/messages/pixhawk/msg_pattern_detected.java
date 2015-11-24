/**
 * Generated class : msg_pattern_detected
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
 * Class msg_pattern_detected
 * 
 **/
public class msg_pattern_detected extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_PATTERN_DETECTED = 190;
  private static final long serialVersionUID = MAVLINK_MSG_ID_PATTERN_DETECTED;
  public msg_pattern_detected() {
    this(1,1);
}
  public msg_pattern_detected(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_PATTERN_DETECTED;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 106;
}

  /**
   * Confidence of detection
   */
  public float confidence;
  /**
   * 0: Pattern, 1: Letter
   */
  public int type;
  /**
   * Pattern file name
   */
  public char[] file = new char[100];
  public void setFile(String tmp) {
    int len = Math.min(tmp.length(), 100);
    for (int i=0; i<len; i++) {
      file[i] = tmp.charAt(i);
    }
    for (int i=len; i<100; i++) {
      file[i] = 0;
    }
  }
  public String getFile() {
    String result="";
    for (int i=0; i<100; i++) {
      if (file[i] != 0) result=result+file[i]; else break;
    }
    return result;
  }
  /**
   * Accepted as true detection, 0 no, 1 yes
   */
  public int detected;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  confidence = (float)dis.readFloat();
  type = (int)dis.readUnsignedByte()&0x00FF;
  for (int i=0; i<100; i++) {
    file[i] = (char)dis.readByte();
  }
  detected = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+106];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeFloat(confidence);
  dos.writeByte(type&0x00FF);
  for (int i=0; i<100; i++) {
    dos.writeByte(file[i]);
  }
  dos.writeByte(detected&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 106);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[112] = crcl;
  buffer[113] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_PATTERN_DETECTED : " +   "  confidence="+confidence+  "  type="+type+  "  file="+getFile()+  "  detected="+detected;}
}

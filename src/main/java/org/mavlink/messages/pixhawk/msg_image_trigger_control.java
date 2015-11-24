/**
 * Generated class : msg_image_trigger_control
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
 * Class msg_image_trigger_control
 * 
 **/
public class msg_image_trigger_control extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL = 153;
  private static final long serialVersionUID = MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL;
  public msg_image_trigger_control() {
    this(1,1);
}
  public msg_image_trigger_control(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 1;
}

  /**
   * 0 to disable, 1 to enable
   */
  public int enable;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  enable = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+1];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeByte(enable&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 1);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[7] = crcl;
  buffer[8] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_IMAGE_TRIGGER_CONTROL : " +   "  enable="+enable;}
}

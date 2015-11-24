/**
 * Generated class : msg_set_cam_shutter
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
 * Class msg_set_cam_shutter
 * 
 **/
public class msg_set_cam_shutter extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_SET_CAM_SHUTTER = 151;
  private static final long serialVersionUID = MAVLINK_MSG_ID_SET_CAM_SHUTTER;
  public msg_set_cam_shutter() {
    this(1,1);
}
  public msg_set_cam_shutter(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_SET_CAM_SHUTTER;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 11;
}

  /**
   * Camera gain
   */
  public float gain;
  /**
   * Shutter interval, in microseconds
   */
  public int interval;
  /**
   * Exposure time, in microseconds
   */
  public int exposure;
  /**
   * Camera id
   */
  public int cam_no;
  /**
   * Camera mode: 0 = auto, 1 = manual
   */
  public int cam_mode;
  /**
   * Trigger pin, 0-3 for PtGrey FireFly
   */
  public int trigger_pin;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  gain = (float)dis.readFloat();
  interval = (int)dis.readUnsignedShort()&0x00FFFF;
  exposure = (int)dis.readUnsignedShort()&0x00FFFF;
  cam_no = (int)dis.readUnsignedByte()&0x00FF;
  cam_mode = (int)dis.readUnsignedByte()&0x00FF;
  trigger_pin = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+11];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeFloat(gain);
  dos.writeShort(interval&0x00FFFF);
  dos.writeShort(exposure&0x00FFFF);
  dos.writeByte(cam_no&0x00FF);
  dos.writeByte(cam_mode&0x00FF);
  dos.writeByte(trigger_pin&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 11);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[17] = crcl;
  buffer[18] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_SET_CAM_SHUTTER : " +   "  gain="+gain+  "  interval="+interval+  "  exposure="+exposure+  "  cam_no="+cam_no+  "  cam_mode="+cam_mode+  "  trigger_pin="+trigger_pin;}
}

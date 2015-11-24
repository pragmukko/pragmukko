/**
 * Generated class : msg_vibration
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
 * Class msg_vibration
 * Vibration levels and accelerometer clipping
 **/
public class msg_vibration extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_VIBRATION = 241;
  private static final long serialVersionUID = MAVLINK_MSG_ID_VIBRATION;
  public msg_vibration() {
    this(1,1);
}
  public msg_vibration(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_VIBRATION;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 32;
}

  /**
   * Timestamp (micros since boot or Unix epoch)
   */
  public long time_usec;
  /**
   * Vibration levels on X-axis
   */
  public float vibration_x;
  /**
   * Vibration levels on Y-axis
   */
  public float vibration_y;
  /**
   * Vibration levels on Z-axis
   */
  public float vibration_z;
  /**
   * first accelerometer clipping count
   */
  public long clipping_0;
  /**
   * second accelerometer clipping count
   */
  public long clipping_1;
  /**
   * third accelerometer clipping count
   */
  public long clipping_2;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  time_usec = (long)dis.readLong();
  vibration_x = (float)dis.readFloat();
  vibration_y = (float)dis.readFloat();
  vibration_z = (float)dis.readFloat();
  clipping_0 = (int)dis.readInt()&0x00FFFFFFFF;
  clipping_1 = (int)dis.readInt()&0x00FFFFFFFF;
  clipping_2 = (int)dis.readInt()&0x00FFFFFFFF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+32];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeLong(time_usec);
  dos.writeFloat(vibration_x);
  dos.writeFloat(vibration_y);
  dos.writeFloat(vibration_z);
  dos.writeInt((int)(clipping_0&0x00FFFFFFFF));
  dos.writeInt((int)(clipping_1&0x00FFFFFFFF));
  dos.writeInt((int)(clipping_2&0x00FFFFFFFF));
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 32);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[38] = crcl;
  buffer[39] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_VIBRATION : " +   "  time_usec="+time_usec+  "  vibration_x="+vibration_x+  "  vibration_y="+vibration_y+  "  vibration_z="+vibration_z+  "  clipping_0="+clipping_0+  "  clipping_1="+clipping_1+  "  clipping_2="+clipping_2;}
}

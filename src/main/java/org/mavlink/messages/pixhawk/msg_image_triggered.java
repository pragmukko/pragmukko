/**
 * Generated class : msg_image_triggered
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
 * Class msg_image_triggered
 * 
 **/
public class msg_image_triggered extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_IMAGE_TRIGGERED = 152;
  private static final long serialVersionUID = MAVLINK_MSG_ID_IMAGE_TRIGGERED;
  public msg_image_triggered() {
    this(1,1);
}
  public msg_image_triggered(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_IMAGE_TRIGGERED;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 52;
}

  /**
   * Timestamp
   */
  public long timestamp;
  /**
   * IMU seq
   */
  public long seq;
  /**
   * Roll angle in rad
   */
  public float roll;
  /**
   * Pitch angle in rad
   */
  public float pitch;
  /**
   * Yaw angle in rad
   */
  public float yaw;
  /**
   * Local frame Z coordinate (height over ground)
   */
  public float local_z;
  /**
   * GPS X coordinate
   */
  public float lat;
  /**
   * GPS Y coordinate
   */
  public float lon;
  /**
   * Global frame altitude
   */
  public float alt;
  /**
   * Ground truth X
   */
  public float ground_x;
  /**
   * Ground truth Y
   */
  public float ground_y;
  /**
   * Ground truth Z
   */
  public float ground_z;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  timestamp = (long)dis.readLong();
  seq = (int)dis.readInt()&0x00FFFFFFFF;
  roll = (float)dis.readFloat();
  pitch = (float)dis.readFloat();
  yaw = (float)dis.readFloat();
  local_z = (float)dis.readFloat();
  lat = (float)dis.readFloat();
  lon = (float)dis.readFloat();
  alt = (float)dis.readFloat();
  ground_x = (float)dis.readFloat();
  ground_y = (float)dis.readFloat();
  ground_z = (float)dis.readFloat();
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+52];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeLong(timestamp);
  dos.writeInt((int)(seq&0x00FFFFFFFF));
  dos.writeFloat(roll);
  dos.writeFloat(pitch);
  dos.writeFloat(yaw);
  dos.writeFloat(local_z);
  dos.writeFloat(lat);
  dos.writeFloat(lon);
  dos.writeFloat(alt);
  dos.writeFloat(ground_x);
  dos.writeFloat(ground_y);
  dos.writeFloat(ground_z);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 52);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[58] = crcl;
  buffer[59] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_IMAGE_TRIGGERED : " +   "  timestamp="+timestamp+  "  seq="+seq+  "  roll="+roll+  "  pitch="+pitch+  "  yaw="+yaw+  "  local_z="+local_z+  "  lat="+lat+  "  lon="+lon+  "  alt="+alt+  "  ground_x="+ground_x+  "  ground_y="+ground_y+  "  ground_z="+ground_z;}
}

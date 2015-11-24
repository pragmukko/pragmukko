/**
 * Generated class : msg_image_available
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
 * Class msg_image_available
 * 
 **/
public class msg_image_available extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_IMAGE_AVAILABLE = 154;
  private static final long serialVersionUID = MAVLINK_MSG_ID_IMAGE_AVAILABLE;
  public msg_image_available() {
    this(1,1);
}
  public msg_image_available(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_IMAGE_AVAILABLE;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 92;
}

  /**
   * Camera id
   */
  public long cam_id;
  /**
   * Timestamp
   */
  public long timestamp;
  /**
   * Until which timestamp this buffer will stay valid
   */
  public long valid_until;
  /**
   * The image sequence number
   */
  public long img_seq;
  /**
   * Position of the image in the buffer, starts with 0
   */
  public long img_buf_index;
  /**
   * Shared memory area key
   */
  public long key;
  /**
   * Exposure time, in microseconds
   */
  public long exposure;
  /**
   * Camera gain
   */
  public float gain;
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
   * Image width
   */
  public int width;
  /**
   * Image height
   */
  public int height;
  /**
   * Image depth
   */
  public int depth;
  /**
   * Camera # (starts with 0)
   */
  public int cam_no;
  /**
   * Image channels
   */
  public int channels;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  cam_id = (long)dis.readLong();
  timestamp = (long)dis.readLong();
  valid_until = (long)dis.readLong();
  img_seq = (int)dis.readInt()&0x00FFFFFFFF;
  img_buf_index = (int)dis.readInt()&0x00FFFFFFFF;
  key = (int)dis.readInt()&0x00FFFFFFFF;
  exposure = (int)dis.readInt()&0x00FFFFFFFF;
  gain = (float)dis.readFloat();
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
  width = (int)dis.readUnsignedShort()&0x00FFFF;
  height = (int)dis.readUnsignedShort()&0x00FFFF;
  depth = (int)dis.readUnsignedShort()&0x00FFFF;
  cam_no = (int)dis.readUnsignedByte()&0x00FF;
  channels = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+92];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeLong(cam_id);
  dos.writeLong(timestamp);
  dos.writeLong(valid_until);
  dos.writeInt((int)(img_seq&0x00FFFFFFFF));
  dos.writeInt((int)(img_buf_index&0x00FFFFFFFF));
  dos.writeInt((int)(key&0x00FFFFFFFF));
  dos.writeInt((int)(exposure&0x00FFFFFFFF));
  dos.writeFloat(gain);
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
  dos.writeShort(width&0x00FFFF);
  dos.writeShort(height&0x00FFFF);
  dos.writeShort(depth&0x00FFFF);
  dos.writeByte(cam_no&0x00FF);
  dos.writeByte(channels&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 92);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[98] = crcl;
  buffer[99] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_IMAGE_AVAILABLE : " +   "  cam_id="+cam_id+  "  timestamp="+timestamp+  "  valid_until="+valid_until+  "  img_seq="+img_seq+  "  img_buf_index="+img_buf_index+  "  key="+key+  "  exposure="+exposure+  "  gain="+gain+  "  roll="+roll+  "  pitch="+pitch+  "  yaw="+yaw+  "  local_z="+local_z+  "  lat="+lat+  "  lon="+lon+  "  alt="+alt+  "  ground_x="+ground_x+  "  ground_y="+ground_y+  "  ground_z="+ground_z+  "  width="+width+  "  height="+height+  "  depth="+depth+  "  cam_no="+cam_no+  "  channels="+channels;}
}

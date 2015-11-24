/**
 * Generated class : msg_detection_stats
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
 * Class msg_detection_stats
 * 
 **/
public class msg_detection_stats extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_DETECTION_STATS = 205;
  private static final long serialVersionUID = MAVLINK_MSG_ID_DETECTION_STATS;
  public msg_detection_stats() {
    this(1,1);
}
  public msg_detection_stats(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_DETECTION_STATS;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 48;
}

  /**
   * Number of detections
   */
  public long detections;
  /**
   * Number of cluster iterations
   */
  public long cluster_iters;
  /**
   * Best score
   */
  public float best_score;
  /**
   * Latitude of the best detection * 1E7
   */
  public long best_lat;
  /**
   * Longitude of the best detection * 1E7
   */
  public long best_lon;
  /**
   * Altitude of the best detection * 1E3
   */
  public long best_alt;
  /**
   * Best detection ID
   */
  public long best_detection_id;
  /**
   * Best cluster ID
   */
  public long best_cluster_id;
  /**
   * Best cluster ID
   */
  public long best_cluster_iter_id;
  /**
   * Number of images already processed
   */
  public long images_done;
  /**
   * Number of images still to process
   */
  public long images_todo;
  /**
   * Average images per seconds processed
   */
  public float fps;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  detections = (int)dis.readInt()&0x00FFFFFFFF;
  cluster_iters = (int)dis.readInt()&0x00FFFFFFFF;
  best_score = (float)dis.readFloat();
  best_lat = (int)dis.readInt();
  best_lon = (int)dis.readInt();
  best_alt = (int)dis.readInt();
  best_detection_id = (int)dis.readInt()&0x00FFFFFFFF;
  best_cluster_id = (int)dis.readInt()&0x00FFFFFFFF;
  best_cluster_iter_id = (int)dis.readInt()&0x00FFFFFFFF;
  images_done = (int)dis.readInt()&0x00FFFFFFFF;
  images_todo = (int)dis.readInt()&0x00FFFFFFFF;
  fps = (float)dis.readFloat();
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+48];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(detections&0x00FFFFFFFF));
  dos.writeInt((int)(cluster_iters&0x00FFFFFFFF));
  dos.writeFloat(best_score);
  dos.writeInt((int)(best_lat&0x00FFFFFFFF));
  dos.writeInt((int)(best_lon&0x00FFFFFFFF));
  dos.writeInt((int)(best_alt&0x00FFFFFFFF));
  dos.writeInt((int)(best_detection_id&0x00FFFFFFFF));
  dos.writeInt((int)(best_cluster_id&0x00FFFFFFFF));
  dos.writeInt((int)(best_cluster_iter_id&0x00FFFFFFFF));
  dos.writeInt((int)(images_done&0x00FFFFFFFF));
  dos.writeInt((int)(images_todo&0x00FFFFFFFF));
  dos.writeFloat(fps);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 48);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[54] = crcl;
  buffer[55] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_DETECTION_STATS : " +   "  detections="+detections+  "  cluster_iters="+cluster_iters+  "  best_score="+best_score+  "  best_lat="+best_lat+  "  best_lon="+best_lon+  "  best_alt="+best_alt+  "  best_detection_id="+best_detection_id+  "  best_cluster_id="+best_cluster_id+  "  best_cluster_iter_id="+best_cluster_iter_id+  "  images_done="+images_done+  "  images_todo="+images_todo+  "  fps="+fps;}
}

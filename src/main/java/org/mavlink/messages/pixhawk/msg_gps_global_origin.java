/**
 * Generated class : msg_gps_global_origin
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
 * Class msg_gps_global_origin
 * Once the MAV sets a new GPS-Local correspondence, this message announces the origin (0,0,0) position
 **/
public class msg_gps_global_origin extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_GPS_GLOBAL_ORIGIN = 49;
  private static final long serialVersionUID = MAVLINK_MSG_ID_GPS_GLOBAL_ORIGIN;
  public msg_gps_global_origin() {
    this(1,1);
}
  public msg_gps_global_origin(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_GPS_GLOBAL_ORIGIN;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 12;
}

  /**
   * Latitude (WGS84), in degrees * 1E7
   */
  public long latitude;
  /**
   * Longitude (WGS84), in degrees * 1E7
   */
  public long longitude;
  /**
   * Altitude (AMSL), in meters * 1000 (positive for up)
   */
  public long altitude;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  latitude = (int)dis.readInt();
  longitude = (int)dis.readInt();
  altitude = (int)dis.readInt();
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+12];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(latitude&0x00FFFFFFFF));
  dos.writeInt((int)(longitude&0x00FFFFFFFF));
  dos.writeInt((int)(altitude&0x00FFFFFFFF));
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 12);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[18] = crcl;
  buffer[19] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_GPS_GLOBAL_ORIGIN : " +   "  latitude="+latitude+  "  longitude="+longitude+  "  altitude="+altitude;}
}

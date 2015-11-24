/**
 * Generated class : msg_home_position
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
 * Class msg_home_position
 * This message can be requested by sending the MAV_CMD_GET_HOME_POSITION command. The position the system will return to and land on. The position is set automatically by the system during the takeoff in case it was not explicitely set by the operator before or after. The position the system will return to and land on. The global and local positions encode the position in the respective coordinate frames, while the q parameter encodes the orientation of the surface. Under normal conditions it describes the heading and terrain slope, which can be used by the aircraft to adjust the approach. The approach 3D vector describes the point to which the system should fly in normal flight mode and then perform a landing sequence along the vector.
 **/
public class msg_home_position extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_HOME_POSITION = 242;
  private static final long serialVersionUID = MAVLINK_MSG_ID_HOME_POSITION;
  public msg_home_position() {
    this(1,1);
}
  public msg_home_position(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_HOME_POSITION;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 52;
}

  /**
   * Latitude (WGS84), in degrees * 1E7
   */
  public long latitude;
  /**
   * Longitude (WGS84, in degrees * 1E7
   */
  public long longitude;
  /**
   * Altitude (AMSL), in meters * 1000 (positive for up)
   */
  public long altitude;
  /**
   * Local X position of this position in the local coordinate frame
   */
  public float x;
  /**
   * Local Y position of this position in the local coordinate frame
   */
  public float y;
  /**
   * Local Z position of this position in the local coordinate frame
   */
  public float z;
  /**
   * World to surface normal and heading transformation of the takeoff position. Used to indicate the heading and slope of the ground
   */
  public float[] q = new float[4];
  /**
   * Local X position of the end of the approach vector. Multicopters should set this position based on their takeoff path. Grass-landing fixed wing aircraft should set it the same way as multicopters. Runway-landing fixed wing aircraft should set it to the opposite direction of the takeoff, assuming the takeoff happened from the threshold / touchdown zone.
   */
  public float approach_x;
  /**
   * Local Y position of the end of the approach vector. Multicopters should set this position based on their takeoff path. Grass-landing fixed wing aircraft should set it the same way as multicopters. Runway-landing fixed wing aircraft should set it to the opposite direction of the takeoff, assuming the takeoff happened from the threshold / touchdown zone.
   */
  public float approach_y;
  /**
   * Local Z position of the end of the approach vector. Multicopters should set this position based on their takeoff path. Grass-landing fixed wing aircraft should set it the same way as multicopters. Runway-landing fixed wing aircraft should set it to the opposite direction of the takeoff, assuming the takeoff happened from the threshold / touchdown zone.
   */
  public float approach_z;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  latitude = (int)dis.readInt();
  longitude = (int)dis.readInt();
  altitude = (int)dis.readInt();
  x = (float)dis.readFloat();
  y = (float)dis.readFloat();
  z = (float)dis.readFloat();
  for (int i=0; i<4; i++) {
    q[i] = (float)dis.readFloat();
  }
  approach_x = (float)dis.readFloat();
  approach_y = (float)dis.readFloat();
  approach_z = (float)dis.readFloat();
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
  dos.writeInt((int)(latitude&0x00FFFFFFFF));
  dos.writeInt((int)(longitude&0x00FFFFFFFF));
  dos.writeInt((int)(altitude&0x00FFFFFFFF));
  dos.writeFloat(x);
  dos.writeFloat(y);
  dos.writeFloat(z);
  for (int i=0; i<4; i++) {
    dos.writeFloat(q[i]);
  }
  dos.writeFloat(approach_x);
  dos.writeFloat(approach_y);
  dos.writeFloat(approach_z);
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
return "MAVLINK_MSG_ID_HOME_POSITION : " +   "  latitude="+latitude+  "  longitude="+longitude+  "  altitude="+altitude+  "  x="+x+  "  y="+y+  "  z="+z+  "  q="+q+  "  approach_x="+approach_x+  "  approach_y="+approach_y+  "  approach_z="+approach_z;}
}

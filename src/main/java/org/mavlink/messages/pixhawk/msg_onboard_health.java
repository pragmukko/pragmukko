/**
 * Generated class : msg_onboard_health
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
 * Class msg_onboard_health
 * 
 **/
public class msg_onboard_health extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_ONBOARD_HEALTH = 206;
  private static final long serialVersionUID = MAVLINK_MSG_ID_ONBOARD_HEALTH;
  public msg_onboard_health() {
    this(1,1);
}
  public msg_onboard_health(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_ONBOARD_HEALTH;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 39;
}

  /**
   * Uptime of system
   */
  public long uptime;
  /**
   * RAM size in GiB
   */
  public float ram_total;
  /**
   * Swap size in GiB
   */
  public float swap_total;
  /**
   * Disk total in GiB
   */
  public float disk_total;
  /**
   * Temperature
   */
  public float temp;
  /**
   * Supply voltage V
   */
  public float voltage;
  /**
   * Network load inbound KiB/s
   */
  public float network_load_in;
  /**
   * Network load outbound in KiB/s
   */
  public float network_load_out;
  /**
   * CPU frequency
   */
  public int cpu_freq;
  /**
   * CPU load in percent
   */
  public int cpu_load;
  /**
   * RAM usage in percent
   */
  public int ram_usage;
  /**
   * Swap usage in percent
   */
  public int swap_usage;
  /**
   * Disk health (-1: N/A, 0: ERR, 1: RO, 2: RW)
   */
  public int disk_health;
  /**
   * Disk usage in percent
   */
  public int disk_usage;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  uptime = (int)dis.readInt()&0x00FFFFFFFF;
  ram_total = (float)dis.readFloat();
  swap_total = (float)dis.readFloat();
  disk_total = (float)dis.readFloat();
  temp = (float)dis.readFloat();
  voltage = (float)dis.readFloat();
  network_load_in = (float)dis.readFloat();
  network_load_out = (float)dis.readFloat();
  cpu_freq = (int)dis.readUnsignedShort()&0x00FFFF;
  cpu_load = (int)dis.readUnsignedByte()&0x00FF;
  ram_usage = (int)dis.readUnsignedByte()&0x00FF;
  swap_usage = (int)dis.readUnsignedByte()&0x00FF;
  disk_health = (int)dis.readByte();
  disk_usage = (int)dis.readUnsignedByte()&0x00FF;
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+39];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(uptime&0x00FFFFFFFF));
  dos.writeFloat(ram_total);
  dos.writeFloat(swap_total);
  dos.writeFloat(disk_total);
  dos.writeFloat(temp);
  dos.writeFloat(voltage);
  dos.writeFloat(network_load_in);
  dos.writeFloat(network_load_out);
  dos.writeShort(cpu_freq&0x00FFFF);
  dos.writeByte(cpu_load&0x00FF);
  dos.writeByte(ram_usage&0x00FF);
  dos.writeByte(swap_usage&0x00FF);
  dos.write(disk_health&0x00FF);
  dos.writeByte(disk_usage&0x00FF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 39);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[45] = crcl;
  buffer[46] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_ONBOARD_HEALTH : " +   "  uptime="+uptime+  "  ram_total="+ram_total+  "  swap_total="+swap_total+  "  disk_total="+disk_total+  "  temp="+temp+  "  voltage="+voltage+  "  network_load_in="+network_load_in+  "  network_load_out="+network_load_out+  "  cpu_freq="+cpu_freq+  "  cpu_load="+cpu_load+  "  ram_usage="+ram_usage+  "  swap_usage="+swap_usage+  "  disk_health="+disk_health+  "  disk_usage="+disk_usage;}
}

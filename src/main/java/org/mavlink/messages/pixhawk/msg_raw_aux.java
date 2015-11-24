/**
 * Generated class : msg_raw_aux
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
 * Class msg_raw_aux
 * 
 **/
public class msg_raw_aux extends MAVLinkMessage {
  public static final int MAVLINK_MSG_ID_RAW_AUX = 172;
  private static final long serialVersionUID = MAVLINK_MSG_ID_RAW_AUX;
  public msg_raw_aux() {
    this(1,1);
}
  public msg_raw_aux(int sysId, int componentId) {
    messageType = MAVLINK_MSG_ID_RAW_AUX;
    this.sysId = sysId;
    this.componentId = componentId;
    length = 16;
}

  /**
   * Barometric pressure (hecto Pascal)
   */
  public long baro;
  /**
   * ADC1 (J405 ADC3, LPC2148 AD0.6)
   */
  public int adc1;
  /**
   * ADC2 (J405 ADC5, LPC2148 AD0.2)
   */
  public int adc2;
  /**
   * ADC3 (J405 ADC6, LPC2148 AD0.1)
   */
  public int adc3;
  /**
   * ADC4 (J405 ADC7, LPC2148 AD1.3)
   */
  public int adc4;
  /**
   * Battery voltage
   */
  public int vbat;
  /**
   * Temperature (degrees celcius)
   */
  public int temp;
/**
 * Decode message with raw data
 */
public void decode(LittleEndianDataInputStream dis) throws IOException {
  baro = (int)dis.readInt();
  adc1 = (int)dis.readUnsignedShort()&0x00FFFF;
  adc2 = (int)dis.readUnsignedShort()&0x00FFFF;
  adc3 = (int)dis.readUnsignedShort()&0x00FFFF;
  adc4 = (int)dis.readUnsignedShort()&0x00FFFF;
  vbat = (int)dis.readUnsignedShort()&0x00FFFF;
  temp = (int)dis.readShort();
}
/**
 * Encode message with raw data and other informations
 */
public byte[] encode() throws IOException {
  byte[] buffer = new byte[8+16];
   LittleEndianDataOutputStream dos = new LittleEndianDataOutputStream(new ByteArrayOutputStream());
  dos.writeByte((byte)0xFE);
  dos.writeByte(length & 0x00FF);
  dos.writeByte(sequence & 0x00FF);
  dos.writeByte(sysId & 0x00FF);
  dos.writeByte(componentId & 0x00FF);
  dos.writeByte(messageType & 0x00FF);
  dos.writeInt((int)(baro&0x00FFFFFFFF));
  dos.writeShort(adc1&0x00FFFF);
  dos.writeShort(adc2&0x00FFFF);
  dos.writeShort(adc3&0x00FFFF);
  dos.writeShort(adc4&0x00FFFF);
  dos.writeShort(vbat&0x00FFFF);
  dos.writeShort(temp&0x00FFFF);
  dos.flush();
  byte[] tmp = dos.toByteArray();
  for (int b=0; b<tmp.length; b++) buffer[b]=tmp[b];
  int crc = MAVLinkCRC.crc_calculate_encode(buffer, 16);
  crc = MAVLinkCRC.crc_accumulate((byte) IMAVLinkCRC.MAVLINK_MESSAGE_CRCS[messageType], crc);
  byte crcl = (byte) (crc & 0x00FF);
  byte crch = (byte) ((crc >> 8) & 0x00FF);
  buffer[22] = crcl;
  buffer[23] = crch;
  dos.close();
  return buffer;
}
public String toString() {
return "MAVLINK_MSG_ID_RAW_AUX : " +   "  baro="+baro+  "  adc1="+adc1+  "  adc2="+adc2+  "  adc3="+adc3+  "  adc4="+adc4+  "  vbat="+vbat+  "  temp="+temp;}
}

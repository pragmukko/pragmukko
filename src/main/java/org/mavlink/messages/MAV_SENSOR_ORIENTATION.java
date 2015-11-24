/**
 * Generated class : MAV_SENSOR_ORIENTATION
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;
/**
 * Interface MAV_SENSOR_ORIENTATION
 * Enumeration of sensor orientation, according to its rotations
 **/
public interface MAV_SENSOR_ORIENTATION {
    /**
     * Roll: 0, Pitch: 0, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_NONE = 0;
    /**
     * Roll: 0, Pitch: 0, Yaw: 45
     */
    public final static int MAV_SENSOR_ROTATION_YAW_45 = 1;
    /**
     * Roll: 0, Pitch: 0, Yaw: 90
     */
    public final static int MAV_SENSOR_ROTATION_YAW_90 = 2;
    /**
     * Roll: 0, Pitch: 0, Yaw: 135
     */
    public final static int MAV_SENSOR_ROTATION_YAW_135 = 3;
    /**
     * Roll: 0, Pitch: 0, Yaw: 180
     */
    public final static int MAV_SENSOR_ROTATION_YAW_180 = 4;
    /**
     * Roll: 0, Pitch: 0, Yaw: 225
     */
    public final static int MAV_SENSOR_ROTATION_YAW_225 = 5;
    /**
     * Roll: 0, Pitch: 0, Yaw: 270
     */
    public final static int MAV_SENSOR_ROTATION_YAW_270 = 6;
    /**
     * Roll: 0, Pitch: 0, Yaw: 315
     */
    public final static int MAV_SENSOR_ROTATION_YAW_315 = 7;
    /**
     * Roll: 180, Pitch: 0, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180 = 8;
    /**
     * Roll: 180, Pitch: 0, Yaw: 45
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180_YAW_45 = 9;
    /**
     * Roll: 180, Pitch: 0, Yaw: 90
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180_YAW_90 = 10;
    /**
     * Roll: 180, Pitch: 0, Yaw: 135
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180_YAW_135 = 11;
    /**
     * Roll: 0, Pitch: 180, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_PITCH_180 = 12;
    /**
     * Roll: 180, Pitch: 0, Yaw: 225
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180_YAW_225 = 13;
    /**
     * Roll: 180, Pitch: 0, Yaw: 270
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180_YAW_270 = 14;
    /**
     * Roll: 180, Pitch: 0, Yaw: 315
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180_YAW_315 = 15;
    /**
     * Roll: 90, Pitch: 0, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90 = 16;
    /**
     * Roll: 90, Pitch: 0, Yaw: 45
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90_YAW_45 = 17;
    /**
     * Roll: 90, Pitch: 0, Yaw: 90
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90_YAW_90 = 18;
    /**
     * Roll: 90, Pitch: 0, Yaw: 135
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90_YAW_135 = 19;
    /**
     * Roll: 270, Pitch: 0, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_270 = 20;
    /**
     * Roll: 270, Pitch: 0, Yaw: 45
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_270_YAW_45 = 21;
    /**
     * Roll: 270, Pitch: 0, Yaw: 90
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_270_YAW_90 = 22;
    /**
     * Roll: 270, Pitch: 0, Yaw: 135
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_270_YAW_135 = 23;
    /**
     * Roll: 0, Pitch: 90, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_PITCH_90 = 24;
    /**
     * Roll: 0, Pitch: 270, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_PITCH_270 = 25;
    /**
     * Roll: 0, Pitch: 180, Yaw: 90
     */
    public final static int MAV_SENSOR_ROTATION_PITCH_180_YAW_90 = 26;
    /**
     * Roll: 0, Pitch: 180, Yaw: 270
     */
    public final static int MAV_SENSOR_ROTATION_PITCH_180_YAW_270 = 27;
    /**
     * Roll: 90, Pitch: 90, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90_PITCH_90 = 28;
    /**
     * Roll: 180, Pitch: 90, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180_PITCH_90 = 29;
    /**
     * Roll: 270, Pitch: 90, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_270_PITCH_90 = 30;
    /**
     * Roll: 90, Pitch: 180, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90_PITCH_180 = 31;
    /**
     * Roll: 270, Pitch: 180, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_270_PITCH_180 = 32;
    /**
     * Roll: 90, Pitch: 270, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90_PITCH_270 = 33;
    /**
     * Roll: 180, Pitch: 270, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_180_PITCH_270 = 34;
    /**
     * Roll: 270, Pitch: 270, Yaw: 0
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_270_PITCH_270 = 35;
    /**
     * Roll: 90, Pitch: 180, Yaw: 90
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90_PITCH_180_YAW_90 = 36;
    /**
     * Roll: 90, Pitch: 0, Yaw: 270
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_90_YAW_270 = 37;
    /**
     * Roll: 315, Pitch: 315, Yaw: 315
     */
    public final static int MAV_SENSOR_ROTATION_ROLL_315_PITCH_315_YAW_315 = 38;
}

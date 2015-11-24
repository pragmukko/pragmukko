/**
 * Generated class : MAV_AUTOPILOT
 * DO NOT MODIFY!
 **/
package org.mavlink.messages;
/**
 * Interface MAV_AUTOPILOT
 * Micro air vehicle / autopilot classes. This identifies the individual model.
 **/
public interface MAV_AUTOPILOT {
    /**
     * Generic autopilot, full support for everything
     */
    public final static int MAV_AUTOPILOT_GENERIC = 0;
    /**
     * Reserved for future use.
     */
    public final static int MAV_AUTOPILOT_RESERVED = 1;
    /**
     * SLUGS autopilot, http://slugsuav.soe.ucsc.edu
     */
    public final static int MAV_AUTOPILOT_SLUGS = 2;
    /**
     * ArduPilotMega / ArduCopter, http://diydrones.com
     */
    public final static int MAV_AUTOPILOT_ARDUPILOTMEGA = 3;
    /**
     * OpenPilot, http://openpilot.org
     */
    public final static int MAV_AUTOPILOT_OPENPILOT = 4;
    /**
     * Generic autopilot only supporting simple waypoints
     */
    public final static int MAV_AUTOPILOT_GENERIC_WAYPOINTS_ONLY = 5;
    /**
     * Generic autopilot supporting waypoints and other simple navigation commands
     */
    public final static int MAV_AUTOPILOT_GENERIC_WAYPOINTS_AND_SIMPLE_NAVIGATION_ONLY = 6;
    /**
     * Generic autopilot supporting the full mission command set
     */
    public final static int MAV_AUTOPILOT_GENERIC_MISSION_FULL = 7;
    /**
     * No valid autopilot, e.g. a GCS or other MAVLink component
     */
    public final static int MAV_AUTOPILOT_INVALID = 8;
    /**
     * PPZ UAV - http://nongnu.org/paparazzi
     */
    public final static int MAV_AUTOPILOT_PPZ = 9;
    /**
     * UAV Dev Board
     */
    public final static int MAV_AUTOPILOT_UDB = 10;
    /**
     * FlexiPilot
     */
    public final static int MAV_AUTOPILOT_FP = 11;
    /**
     * PX4 Autopilot - http://pixhawk.ethz.ch/px4/
     */
    public final static int MAV_AUTOPILOT_PX4 = 12;
    /**
     * SMACCMPilot - http://smaccmpilot.org
     */
    public final static int MAV_AUTOPILOT_SMACCMPILOT = 13;
    /**
     * AutoQuad -- http://autoquad.org
     */
    public final static int MAV_AUTOPILOT_AUTOQUAD = 14;
    /**
     * Armazila -- http://armazila.com
     */
    public final static int MAV_AUTOPILOT_ARMAZILA = 15;
    /**
     * Aerob -- http://aerob.ru
     */
    public final static int MAV_AUTOPILOT_AEROB = 16;
    /**
     * ASLUAV autopilot -- http://www.asl.ethz.ch
     */
    public final static int MAV_AUTOPILOT_ASLUAV = 17;
}

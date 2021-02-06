/////////////////////////////////////////////////////////////////////
// File: Proximity.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Uses the MaxBotix MB1013 Ultrasonic Proximity sensor to
// measure distance.
//
// Authors: Elliott DuCharme and Larry Basegio of FRC Team #5914.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 1/11/2020(ish).
// Ultrasonic, proximity, ultrasonic proximity, etc. are all synonyms
// for the MB1013 Ultrasonic Proximity sensor. Also, prox is what I
// (Elliott) will usually say/type because it's shorter and easier
// to type than proximity.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;

class ProximitySensor {

    // Magic numbers.
    // Resolution of the proximity sensor.
    private final static double SENSOR_RES = 1024;

    // Millimeters per inch.
    private final static double MM_PER_INCH = 25.4;

    // Voltage that the proximity sensor measures.
    private static double measured_voltage;

    // Creating the sensor.
    private static AnalogInput mb1013;

    // Constructor.
    ProximitySensor() {

        mb1013 = new AnalogInput(0);

        measured_voltage = 0.0;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getVoltage()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the voltage from the proximity sensor.
    //
    // Arguments: void.
    //
    // Returns: Returns the voltage, which we use in getDistance().
    //
    // Remarks: You would think that the prox sensor would return a normal,
    // real number like 10 in, 14 ft, etc. But in reality, it uses some weird
    // voltage stuff. IDK for sure how it works, but Larry and I figured it
    // out somehow.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public static double getVoltage() {
        // Uses built in function of MB1013 to return the voltage.
        return mb1013.getVoltage();
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getDistance()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Uses the voltage from getVoltage(), plus some fancy math
    // stuff, to measure the distance to an object.
    //
    // Arguments: void.
    //
    // Returns: Distance to an object in inches.
    //
    // Remarks: The sensor can be a little unreliable at times, and the
    // texture/material of the object can affect how reliably it measures.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public static double getDistance() {
        double distance;
        measured_voltage = getVoltage();
        distance = (measured_voltage * SENSOR_RES) / MM_PER_INCH;

        return distance;
    }

}
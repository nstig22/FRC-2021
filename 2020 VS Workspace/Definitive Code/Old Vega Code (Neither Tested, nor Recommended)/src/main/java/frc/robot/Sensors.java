/////////////////////////////////////////////////////////////////////
// File: Sensors.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Class used for housing and reading all the sensors at once.
// It makes a lot more sense to read all of them with one function,
// compared to sloppily reading them at random intervals.
// Plus we don't have to have long lines and stuff for reading a gyro
// or something: we just have it read the variable from this class, 
// which contains the current angle.
//
// Authors: Noah Stigeler and Elliott DuCharme.
//
// Environment: Microsoft VSCode Java
//
// Remarks: Created on 2/08/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.util.Color;

class Sensors {

    // Creating an instance of DriveThread in Sensors.java.
    DriveThread driveThread = new DriveThread("DriveThread");

    // Variables for storing sensor values.
    // Gyro angles.
    double driveGyroAngle;
    double wormDriveGyroAngle;

    // Proximity sensor distance value (in inches).
    double proximitySensorDistance;

    // Creating the drive encoder, and also the right worm drive encoder..
    CANEncoder frontLeftEncoder, rightWormDriveEncoder;

    // Double for storing encoder readings for the left drive motor.
    double frontLeftDriveEncValue;

    // Double for storing the right worm drive motor encoder values.
    double rightWormDriveEncoderValue;

    // Creating the 2 gyros.
    ADXRS450_Gyro driveGyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
    ADXRS450_Gyro wormDriveGyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS1);

    // Creating the proximity sensor, with a channel of 0.
    static AnalogInput proximitySensor = new AnalogInput(0);

    // Boolean used for the ballLimitSwitch, which tells the code if the robot is
    // currently carrying balls. Defaults to true, since we start with balls in
    // Autonomous.
    boolean robotCarryingBalls = true;

    // Creating the limit switch used for keeping track of how if the robot is still
    // carrying balls, or if it needs to go and get some more.
    DigitalInput ballLimitSwitch = new DigitalInput(0);

    // Magic numbers for proximity sensor.
    // Resolution of the proximity sensor.
    private final static double PROX_SENSOR_RES = 1024;

    // Millimeters per inch; used for proximity sensor.
    private final static double MM_PER_INCH = 25.4;

    // Voltage that the proximity sensor measures.
    // We use this, and convert it to inches.
    private static double measuredVoltage = 0.0;

    // Creating the color sensor.
    private ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

    // Magic numbers.
    // Minimum values for color measurement.
    // If below these, can't read color values properly.
    private final double MIN_COLOR_SENSOR_IR_VALUE = 10;
    private final int MIN_COLOR_SENSOR_PROX_VALUE = 50;

    // Creating an instance of the ColorMatch class.
    // This is used for matching colors with the color sensor.
    ColorMatch colorMatcher = new ColorMatch();

    // Assigning these RGB values to variables, so the color sensor can look for
    // these RGB values.
    private final Color colorBlue = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color colorGreen = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color colorRed = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color colorYellow = ColorMatch.makeColor(0.361, 0.524, 0.113);

    // Sensors class constructor.
    Sensors() {

        // Adding the colors to look for to the color matcher.
        colorMatcher.addColorMatch(colorBlue);
        colorMatcher.addColorMatch(colorGreen);
        colorMatcher.addColorMatch(colorRed);
        colorMatcher.addColorMatch(colorYellow);

        frontLeftEncoder = new CANEncoder(driveThread.frontLeftDriveMotor);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: readSensors()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Method used for reading all of the sensors on the robot
    // at once, and assigning their values to variables to be used in
    // other files and other functions. This helps simplify things because
    // the sensors don't need to be read multiple times.
    // This function is only called in robotPeriodic(). This is because
    // code in there is always running, thus eliminating the need for a
    // sensor thread, or equivalent.
    //
    // Arguments: None
    //
    // Returns: void
    //
    // Remarks: Every sensor on the robot lives in this file and is read
    // using this method in robotPeriodic().
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void readSensors() {

        // Assigns these variables with their measurements.
        driveGyroAngle = driveGyro.getAngle();
        wormDriveGyroAngle = wormDriveGyro.getAngle();

        // Get if the robot is carrying ball(s) (true),
        // or if it does not have any balls (false).
        robotCarryingBalls = ballLimitSwitch.get();

        // Get the distance from the proximity sensor to the nearest object.
        proximitySensorDistance = getDistanceProxSensor();

        // Get the color that the color sensor sees.
        getColorStringColorSensor();

        // Getting encoder values for the drive motor and the worm drive encoder.
        frontLeftDriveEncValue = frontLeftEncoder.getPosition();
        rightWormDriveEncoderValue = rightWormDriveEncoder.getPosition();
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getDistance()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for the proximitySensor.
    // Uses the voltage from proximitySensor.getVoltage(), plus
    // some fancy math stuff, to measure the distance to an object.
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
    public double getDistanceProxSensor() {
        double distance;
        measuredVoltage = proximitySensor.getVoltage();
        distance = (measuredVoltage * PROX_SENSOR_RES) / MM_PER_INCH;

        return distance;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getColorString()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the current color that the color sensor sees.
    //
    // Arguments: void.
    //
    // Returns: A string containing the color it sees (colorString).
    //
    // Remarks:
    //
    /////////////////////////////////////////////////////////////////////
    String getColorStringColorSensor() {
        // Get instances of each of these classes.
        Color detectedColor;
        ColorMatchResult colorMatchResult;

        // String for storing the detected color.
        // E.g, if the color sensor sees Blue, it will store this String
        // as "Blue".
        String colorOutput;

        // The color that the color sensor sees.
        detectedColor = colorSensor.getColor();

        // The result after running the color matching algorithm.
        colorMatchResult = colorMatcher.matchClosestColor(detectedColor);

        // If the color match result is a color, assign the String that value.
        // Else, return unknown.
        if (colorMatchResult.color == colorBlue) {
            colorOutput = "Blue";
        } else if (colorMatchResult.color == colorRed) {
            colorOutput = "Red";
        } else if (colorMatchResult.color == colorGreen) {
            colorOutput = "Green";
        } else if (colorMatchResult.color == colorYellow) {
            colorOutput = "Yellow";
        } else {
            colorOutput = "Unknown";
        }

        // If the color sensor is too far away to be able to read colors properly,
        // return "Invalid".
        if (ColorValidity() == false) {
            colorOutput = "Invalid";
        }

        // Returns the detected color.
        return (colorOutput);

    }

    /////////////////////////////////////////////////////////////////////
    // Function: getColorSensorIR()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the IR value.
    //
    // Arguments: void.
    //
    // Returns: double representing the IR reading.
    //
    // Remarks: Created on 1/11/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    double getColorSensorIR() {

        // Value used for storing the IR reading.
        double IR_Reading;

        // Get the IR value from the color sensor.
        IR_Reading = colorSensor.getIR();

        // Return the value.
        return (IR_Reading);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getProximity()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the proximity to the object.
    //
    // Arguments: void.
    //
    // Returns: A value proportional to the proximity
    // (the closer you are, the higher the value).
    //
    // Remarks: Saturates at 255 (max value it can return).
    // Experimentally, the minimum value is 40 or 50.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    int getColorSensorProximity() {

        // Stores the proximity value for the color sensor.
        int proximity;

        // Gets the proximity value from the color sensor.
        proximity = colorSensor.getProximity();

        // Returns the proximity value from the color sensor.
        return (proximity);

    }

    /////////////////////////////////////////////////////////////////////
    // Function: ColorValidity()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Determines if we are close enough to get an accurate
    // color reading. Makes use of both IR and proximity features of
    // the sensor.
    //
    // Arguments: void.
    //
    // Returns: True or false, depending on if it's close enough to get
    // an accurate reading. True means that we can have a valid reading
    // (we're close enough). False means we're not close enough.
    //
    // Remarks: Initial testing indicated that the values MIN_IR_VALUE and
    // MIN_PROX_VALUE are sufficient.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    boolean ColorValidity() {

        // Stores the color sensor's IR and proximity values.
        int proximity;
        double IR_Reading;

        // Gets those values.
        IR_Reading = getColorSensorIR();

        proximity = getColorSensorProximity();

        // If we're close enough, return true. Else, return false.
        if ((proximity > MIN_COLOR_SENSOR_PROX_VALUE) && (IR_Reading > MIN_COLOR_SENSOR_IR_VALUE)) {
            return (true);
        } else {
            return (false);
        }

    }

}
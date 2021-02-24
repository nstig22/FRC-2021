/////////////////////////////////////////////////////////////////////
// File: DriveThread.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: This Thread is responsible for the joystick controls for
// the robot in teleop.
//
// Authors: Elliott DuCharme and Larry Basegio.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/27/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

// Creating the class that extends RobotDrive and implements the Runnable interface.
class DriveThread extends RobotDrive implements Runnable {

    // Creating an instance of the Thread class, named driveThread.
    Thread driveThread;

    // Getting a reference to the Runtime class.
    Runtime runtime = Runtime.getRuntime();

    // Toggle for if the drive joystick axes are inverted or not.
    boolean invertDriveToggle = false;

    // Doubles used for the joystick and analog trigger
    // values in the Mecanum drive deadband.
    double leftXAxisPS4, leftYAxisPS4, zAxisTriggers;

    // Used for the Mecanum drive deadband.
    final double PS4_MEC_DRIVE_DEADBAND = 0.2;

    // Constructor.
    DriveThread(String name) {

        // Name of the Thread.
        String threadName = name;

        // Actually creating the Thread.
        driveThread = new Thread(this, threadName);
        driveThread.start(); // Start the Thread.
    }

    // The stuff that is actually run while the Thread is running.
    public void run() {

        // While the Thread is alive, do stuff.
        while (driveThread.isAlive() == true) {

            // If we're in autonomous, run auto code. Else, normal joystick drive stuff.
            if (DriverStation.getInstance().isAutonomous()) {

                // Run auto stuff only once.
                if (autoOnce == true) {
                    // TODO Auto stuff here...
                    autoOnce = false;
                }

            } else {

                // Getting the values of these to be used for Mecanum drive stuff.
                leftXAxisPS4 = PS4.getX();
                leftYAxisPS4 = PS4.getY();
                zAxisTriggers = getZAxisTriggers();

                // If the driver presses the Touchpad button on the PS4,
                // change the variable to its opposite state, thus either
                // inverting the drive or un-inverting it.
                // TODO develop new inversion
                if (PS4.getRawButton(constants.PS4_TOUCHPAD)) {
                    invertDriveToggle = !invertDriveToggle;
                    Timer.delay(0.1);
                }

                /*
                 * Long if statement that acts as a deadband for the drive. Basically, if the
                 * absolute value of X, Y, OR Z axis values are greater than 0.2, run the
                 * Mecanum drive. Else, don't run the Mecanum drive stuff. The arguments for
                 * this function don't match up with the actual joystick axes for some reason.
                 * Depending on the robot, you might have to experiment with these. Z = Right
                 * joystick X axis (changed to the analog triggers using getZAxisTriggers()); Y
                 * = Left joystick Y axis; X = left joystick X axis. In this case, ySpeed is the
                 * strafing stuff, xSpeed is for driving forward/backward, and zRotation is for
                 * turning left/right.
                 */

                // TODO Try using drive Cartesian with the driveGyro angle parameter.
                // If either axis is being pressed, run the drive in 1 of 2 ways,
                // depending on the toggle.
                if (((Math.abs(leftXAxisPS4) > PS4_MEC_DRIVE_DEADBAND)
                        || (Math.abs(leftYAxisPS4) > PS4_MEC_DRIVE_DEADBAND)
                        || (Math.abs(zAxisTriggers) > PS4_MEC_DRIVE_DEADBAND))) {

                    // If the invert drive toggle is false, drive normally.
                    if (invertDriveToggle == false) {
                        mecanumDrive.driveCartesian(getZAxisTriggers(), -leftYAxisPS4, leftXAxisPS4);
                    }

                    // If the toggle is true, the same function but the signs are different.
                    else if (invertDriveToggle == true) {
                        mecanumDrive.driveCartesian(-getZAxisTriggers(), leftYAxisPS4, -leftXAxisPS4);
                    }

                } else {
                    // Else, don't run the drive motors.
                    mecanumDrive.driveCartesian(0, 0, 0);
                }

            }

        }

    }

    /////////////////////////////////////////////////////////////////////
    // Function: getZAxisTriggers()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the value for the "Z" axis using the 2 analog triggers.
    //
    // Arguments: none
    //
    // Returns: double zAxis: the value from -1 to 1, which is used for
    // controlling the speed and direction for strafing in teleop.
    //
    // Remarks: Created on 2/22/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public double getZAxisTriggers() {

        // This variable is used for setting the Z axis for strafing.
        double zAxis;

        // These variables are for getting the values for the left and right
        // analog triggers, respectively.
        double leftAnalogTrigger;
        double rightAnalogTrigger;

        // Axes 2 and 3 are the left and right analog triggers, respectively.
        // You have to add 1 because the triggers start at -1 and go to 1.
        // Adding 1 makes them start at 0 when not being pressed.
        leftAnalogTrigger = PS4.getRawAxis(3) + 1;
        rightAnalogTrigger = PS4.getRawAxis(4) + 1;

        // Do the math for getting the value for strafing.
        // Example 1: if the driver presses the right one down, that value will be 1 - 0
        // = 100% speed (1).
        // Example 2: if the driver presses the left one down, that value will be 0 - 1
        // ; -100% speed (-1).
        zAxis = rightAnalogTrigger - leftAnalogTrigger;

        // Return the value, to be used elsewhere.
        return zAxis;
    }

}
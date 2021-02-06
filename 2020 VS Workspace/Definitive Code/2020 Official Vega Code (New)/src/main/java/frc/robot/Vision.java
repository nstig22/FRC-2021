/////////////////////////////////////////////////////////////////////
// File: Vision.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Used for vision stuff for the Limelight.
//
// Authors: Elliott DuCharme and Larry Basegio.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 3/05/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

// Extends Auto which also extends Robot.
class Vision extends Autonomous {

    // NetworkTable for the Limelight.
    private NetworkTable networkTable = NetworkTableInstance.getDefault().getTable("limelight");

    // Constants for the LED mode of the Limelight.
    // These control how the LED's work on it.
    // Blink, not-blinking, off, etc.
    final int LED_MODE_CURR_PIPELINE_LED_MODE = 0; // Use the LED Mode set in the current pipeline.
    final int LED_MODE_FORCE_OFF = 1; // Force the LED's off.
    final int LED_MODE_FORCE_BLINK = 2; // Force the LED's to blink.
    final int LED_MODE_FORCE_ON = 3; // Force the LED's on.

    // Constants for the camera mode of the Limelight.
    // If the camera is processing vision or not.
    final int CAM_MODE_VISION_PROCESSOR = 0; // Processing vision.
    final int CAM_MODE_DRIVER_CAMERA = 1; // Like a normal camera (increases exposure, disables vision processing).

    // Constants for the camera stream.
    // These control how the Limelight displays the camera stream in Shuffleboard.
    final int STREAM_MODE_STANDARD = 0; // Side-by-side; works very well.
    // The secondary camera stream is placed in the lower-right corner of the
    // primary camera stream.
    final int STREAM_MODE_PIP_MAIN = 1;
    // The primary camera stream is placed in the lower-right corner of the
    // secondary camera stream.
    final int STREAM_MODE_PIP_SECONDARY = 2;

    // The minimum angles our TX can be for accurate shooting.
    // Used in alignCenterInnerPort().
    // Basically, if our tx is less than/greater than these, strafe right/left.
    final double MIN_ALLOWED_TX_POS = 5; // Degrees.
    final double MIN_ALLOWED_TX_NEG = -5; // Degrees.

    // High goal diameter; used in alignCenterInnerPort().
    final int HIGH_GOAL_DIAMETER = 23; // Inches.

    // Constructor.
    Vision() {

        // TODO put this stuff in robotPeriodic()?
        // Set these to our desired initial values.
        // If necessary, we can change them later.
        setLEDMode(LED_MODE_FORCE_ON);
        setCamMode(CAM_MODE_VISION_PROCESSOR);
        setStreamMode(STREAM_MODE_STANDARD);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: setLEDMode()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Sets how the LED's on the Limelight will work.
    // Throws an exception if the argument is not between 0 and 3.
    //
    // Arguments: int ledMode: 0 through 3. Check top of file for what
    // they represent.
    //
    // Returns: void
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void setLEDMode(int ledMode) {

        // If out of range, throw an error. The error is definitely not
        // necessary, but it's a good programming practice.
        if ((ledMode > 3) || (ledMode < 0)) {

            throw new IllegalArgumentException("ledMode cannot be greater than 3 and less than 0.");

        } else if ((ledMode <= 3) && (ledMode >= 0)) {

            // Else if the passed-in value is in the range, set the value.
            networkTable.getEntry("ledMode").setValue(ledMode);
        }

    }

    /////////////////////////////////////////////////////////////////////
    // Function: getLEDMode()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the current LED mode on the Limelight.
    //
    // Arguments: none
    //
    // Returns: NetworkTableEntry: the value of the LED mode.
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public NetworkTableEntry getLEDMode() {
        NetworkTableEntry ledMode = networkTable.getEntry("ledMode");
        return ledMode;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: setCamMode()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Sets the Limelight operation mode: Vision processor or
    // Driver Camera (increases exposure, disables vision processing).
    // Throws an error (exception) if the argument is not 0 or 1.
    //
    // Arguments: int camMode. Must be either 0 or 1.
    // Check top of the file for what they are.
    //
    // Returns: void
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void setCamMode(int camMode) {

        // If out of range, throw an exception.
        // Else if in range, set the value.
        if ((camMode != 0) || (camMode != 1)) {

            throw new IllegalArgumentException("camMode can only be 0 or 1.");

        } else if ((camMode == 0) && (camMode == 1)) {

            networkTable.getEntry("camMode").setValue(camMode);
        }

    }

    /////////////////////////////////////////////////////////////////////
    // Function: getCamMode()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the camera mode on the Limelight.
    //
    // Arguments: none
    //
    // Returns: NetworkTableEntry: the value of the LED mode.
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public NetworkTableEntry getCamMode() {
        NetworkTableEntry camMode = networkTable.getEntry("camMode");
        return camMode;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: setStreamMode()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Sets the way the Limelight displays the 2 camera streams
    // in Shuffleboard.
    //
    // Arguments: int streamMode. Must be between 0 and 2.
    // Check top of file for what they represent.
    // Throws an error (exception) if the argument is not between 0 and 2.
    //
    // Returns: void
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void setStreamMode(int streamMode) {

        // If out of range, throw an error. The error is definitely not
        // necessary, but it's a good programming practice.
        if ((streamMode > 2) || (streamMode < 0)) {

            throw new IllegalArgumentException("streamMode must be between 0 and 2.");

        } else if ((streamMode <= 2) && (streamMode >= 0)) {

            // Else if the passed-in value is in the range, set the value.
            networkTable.getEntry("streamMode").setValue(streamMode);
        }
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getStreamMode()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the stream mode of the Limelight.
    //
    // Arguments: none
    //
    // Returns: NetworkTableEntry: the value of the LED mode.
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public NetworkTableEntry getStreamMode() {
        NetworkTableEntry streamMode = networkTable.getEntry("streamMode");
        return streamMode;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getTvIsTargetFound()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets a boolean representing tv: whether the Limelight
    // has any valid targets or not (0 or 1).
    //
    // Arguments: none
    //
    // Returns: A boolean for if the target is found.
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public boolean getTvIsTargetFound() {

        // Get the value from the NetworkTable.
        NetworkTableEntry tvNetworkTable = networkTable.getEntry("tv");
        double tv = tvNetworkTable.getDouble(0);

        // Return if the target is found or not.
        if (tv == 0) {
            return false;
        } else if (tv == 1) {
            return true;
        } else {
            return false; // Default return statement.
        }

    }

    /////////////////////////////////////////////////////////////////////
    // Function: getTxHorizontalOffset()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the tx value from the Limelight. This is the horizontal
    // offset from the crosshair to the target (-27 degrees to 27 degrees).
    //
    // Arguments: none
    //
    // Returns: double tx
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public double getTxHorizontalOffset() {

        // Get the value from the NetworkTable.
        NetworkTableEntry txNetworkTable = networkTable.getEntry("tx");
        double tx = txNetworkTable.getDouble(0.0);

        // Return the tx value.
        return tx;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getTyVerticalOffset()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the ty value from Limelight. This is the vertical
    // offset from the crosshair to the target (-24.85 to 24.85 degrees).
    //
    // Arguments: none
    //
    // Returns: double ty
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public double getTyVerticalOffset() {

        // Get the value from the NetworkTable.
        NetworkTableEntry tyNetworkTable = networkTable.getEntry("ty");
        double ty = tyNetworkTable.getDouble(0.0);

        // Return the tx value.
        return ty;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: getTaTargetArea()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Gets the ta value from Limelight. This is target area of
    // the Limelight (0% to 100%). I think this is how much of the image
    // is composed of the target stuff.
    //
    // Arguments: none
    //
    // Returns: double ta
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public double getTaTargetArea() {

        // Get the value from the NetworkTable.
        // Gets an instance of the NetworkTable, and gets the ta entry.
        NetworkTableEntry taNetworkTable = networkTable.getEntry("ta");
        double ta = taNetworkTable.getDouble(0.0);

        // Return the ta value.
        return ta;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: calcMaxAngleTx()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Calculates the maximum angle that we can be off of the target
    // and still have the ball we fire go in the high goal.
    //
    // Arguments: none.
    //
    // Returns: A double representing the max angle.
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public double calcMaxAngleTx() {

        // Do this math and get an angle in Radians.
        double maxAngleRad = Math.atan(((HIGH_GOAL_DIAMETER / 2) / proximitySensorClass.getDistance()));

        // Return the value in degrees.
        return Math.toDegrees(maxAngleRad);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: alignCenterInnerPort()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for aligning the robot in the center(ish) of the
    // inner port. Can be used for auto and teleop.
    //
    // Arguments: none
    //
    // Returns: void
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////

    // TODO try this function!

    public void alignCenterInnerPort() {

        // The max angle from the previous function.
        double maxTx = calcMaxAngleTx();
        double tx = getTxHorizontalOffset();

        // While our current tx is either greater than or less than what
        // we want, strafe to align with the inner port.
        while ((tx > maxTx) || (tx < -maxTx)) {

            if (tx > maxTx) {

                // Strafe right to align with the inner port.
                robotDrive.strafeLeft(0.6);

            } else if (tx < -maxTx) {

                // Strafe left to align with the inner port.
                robotDrive.strafeRight(0.6);
            }

            // Read this value every time the while loop loops.
            tx = getTxHorizontalOffset();
        }

        // Stop the motors, because we're done moving.
        driveThread.frontLeftDriveMotor.set(0);
        driveThread.backLeftDriveMotor.set(0);
        driveThread.frontRightDriveMotor.set(0);
        driveThread.backRightDriveMotor.set(0);
    }

}
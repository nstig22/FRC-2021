
/////////////////////////////////////////////////////////////////////
//  File:  Sensors.java
/////////////////////////////////////////////////////////////////////
//
//  Purpose:  Reads all the sensors.  This is not a thread but an
//            implementation intended to be called within 
//            robotPeriodic().  When implemented with robotPeriodic()
//            It was not easy to stop it.  Implication is that
//            robotPeriodic() is a thread of sorts.
//
//  Programmer:
//
//  Environment:Microsoft VS for FIRST FRC
//
//  Inception Date:  December 2019
//
//  Revisions:  
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

package frc.robot;

class Sensors {

    int inner_lift_position;
    double tilt_position;
    static double drive_position;
    static double drive_angle;
    static double tilt_gyro_angle;

    // Constructor
    Sensors() {
        inner_lift_position = 0;
        tilt_position = 0;
        drive_position = 0;
        drive_angle = 0;
    }

    // Entry point for the thread. This is where any desired
    // action needs to be implemented. It will run in "parallel"
    // or "time share" with other active threads. The prototype
    // of this function is fixed. It cannot be overidden with
    // a version that passes in variables. This makes it necessary
    // to create static variables within Robot.java that can be
    // seen within this function.

    // Read the four sensors
    int readSensors() {
        // Inner Lift redline encoder
        // inner_lift_position = Robot.MagEncoder.get();

        // Intake tilt encoder (NEOS motor)
        // tilt_position = Robot.tilt_enc.getPosition();

        // Get the position associated with the left drive.
        drive_position = Robot.left_enc.getPosition();

        // Gyro associated with robot direction control
        // drive_angle = Robot.driveGyro.getAngle();

        // tilt_gyro_angle = Robot.tiltGyro.getAngle();

        return (0);
    }

}

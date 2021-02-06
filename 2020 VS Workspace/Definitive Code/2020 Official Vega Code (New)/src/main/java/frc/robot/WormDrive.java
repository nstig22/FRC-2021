/////////////////////////////////////////////////////////////////////
// File: WormDrive.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Houses motors, functions and other stuff for raising and 
// lowering the lift (worm drive).
//
// Authors: Elliott DuCharme and Larry Basegio.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/29/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.SpeedControllerGroup;

class WormDrive extends Robot {

    // Creating the motors for the worm drive.
    CANSparkMax leftWormDriveMotor = new CANSparkMax(constants.LEFT_WORM_DRIVE_MOTOR_ID, MotorType.kBrushless);
    CANSparkMax rightWormDriveMotor = new CANSparkMax(constants.RIGHT_WORM_DRIVE_MOTOR_ID, MotorType.kBrushless);

    // Grouping the motors together.
    SpeedControllerGroup wormDriveMotors = new SpeedControllerGroup(leftWormDriveMotor, rightWormDriveMotor);

    // Constructor.
    WormDrive() {

        // Setting the worm drive motors in brake mode
        // (so the lift stays up, obviously).
        leftWormDriveMotor.setIdleMode(IdleMode.kBrake);
        rightWormDriveMotor.setIdleMode(IdleMode.kBrake);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: adjustShooterAngleManual()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Adjusts the angle of the ball shooter manually, by running the
    // worm drive motors either forwards or backwards.
    //
    // Arguments: double wormDriveSpeed:
    // The speed the worm drive motors should run at.
    //
    // Returns: void
    //
    // Remarks: Created on 2/29/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void adjustShooterAngleManual(double wormDriveSpeed) {

        // If the driver pushes the Square Button on the PS4 Controller,
        // set the worm drive motors to go backwards (lower it).
        if (PS4.getRawButton(constants.PS4_X_BUTTON) == true) {

            wormDriveMotors.set(-wormDriveSpeed);

            // If the driver pushes the Triangle Button on the PS4 Controller,
            // set the worm drive motors to go forwards (raise it up).
        } else if (PS4.getRawButton(constants.PS4_SQUARE_BUTTON) == true) {

            wormDriveMotors.set(wormDriveSpeed);
        }

        // If the driver is an idiot and is pressing BOTH the Square Button AND the
        // Triangle Button at the same time, OR (||) if the driver is pushing neither
        // button, set the motor speed to 0.
        else if (((PS4.getRawButton(constants.PS4_X_BUTTON) == true)
                && (PS4.getRawButton(constants.PS4_SQUARE_BUTTON) == true))
                || ((PS4.getRawButton(constants.PS4_X_BUTTON) == false)
                        && (PS4.getRawButton(constants.PS4_SQUARE_BUTTON) == false))) {

            wormDriveMotors.set(0);
        }

    }
}
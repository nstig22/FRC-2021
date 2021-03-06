/////////////////////////////////////////////////////////////////////
// File: WormDrive.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Houses motors, functions and other stuff for raising and 
// lowering the lift (worm drive).
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/29/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

class WormDrive extends Constants {

    // Creating the motors for the worm drive.
    TalonFX wormDriveMotor = new TalonFX(WORM_DRIVE_MOTOR_ID);
    TalonFX climbMotor = new TalonFX(CLIMB_MOTOR_ID);

    // Constructor.
    WormDrive() {

        // Setting the worm drive motors in brake mode
        // (so the lift stays up, obviously).
        wormDriveMotor.setNeutralMode(NeutralMode.Brake);
        climbMotor.setNeutralMode(NeutralMode.Brake);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: adjustShooterAngleManual()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Adjusts the angle of the ball shooter manually, by running the
    // worm drive motors either forwards or backwards.
    //
    // Arguments: none
    //
    // Returns: void
    //
    // Remarks: Created on 2/29/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void adjustShooterAngleManual() {

        // If the driver pushes the Circle Button on the PS Controller,
        // set the worm drive motors to go backwards (lower it).
        if (PS.getRawButton(PS_CIRCLE_BUTTON) == true) {

            wormDriveMotor.set(ControlMode.PercentOutput, 0.3);

            // If the driver pushes the Triangle Button on the PS Controller,
            // set the worm drive motors to go forwards (raise it up).
        } else if (PS.getRawButton(PS_TRIANGLE_BUTTON) == true) {

            wormDriveMotor.set(ControlMode.PercentOutput, -0.4);
        }

        // If the driver is an idiot and is pressing BOTH the Circle Button AND the
        // Triangle Button at the same time, OR (||) if the driver is pushing neither
        // button, set the motor speed to 0.
        else if (PS.getRawButton(PS_CIRCLE_BUTTON) == false && PS.getRawButton(PS_TRIANGLE_BUTTON) == false) {
            wormDriveMotor.set(ControlMode.PercentOutput, 0);
        }

    }

    public void runClimb() {
        climbMotor.set(ControlMode.PercentOutput, -PS.getRawAxis(PS_R_ANALOG_TRIG_ID));

        System.out.println(climbMotor.getSelectedSensorPosition());
    }
}
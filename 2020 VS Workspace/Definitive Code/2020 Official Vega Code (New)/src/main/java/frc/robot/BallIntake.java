/////////////////////////////////////////////////////////////////////
// File: BallIntake.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Houses motors, functions, and other stuff for intaking
// lemons (power cells).
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

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

// Extends allows us to inherit all of the things in Robot.java,
// without having to do something nasty and disastrous like Robot robot = new Robot();
class BallIntake extends Robot {

    // Creating the Falcon 500 for the ball intake, and assigning it an ID.
    WPI_TalonFX ballIntakeMotor = new WPI_TalonFX(constants.BALL_INTAKE_MOTOR_ID);

    BallIntake() {

        // Setting the intake Falcon 500 motor in brake mode.
        ballIntakeMotor.setNeutralMode(NeutralMode.Brake);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: intakeBalls(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Function used for intaking balls.
    // Runs the intake motor itself, and also the top belt of the shooter.
    //
    // Arguments: double intakeSpeed (speed of the intake motor),
    // double topBeltSpeed (speed of the belt on the top of the robot).
    //
    // Returns: void
    //
    // Remarks: Created on 2/29/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void intakeBalls(double intakeSpeed, double topBeltSpeed) {

        // Run the intake motor at the inputted speed.
        ballIntakeMotor.set(intakeSpeed);

        // Run the back shooter motors at the inputted speed.
        ballShooter.backShooterMotors.set(topBeltSpeed);

    }
}
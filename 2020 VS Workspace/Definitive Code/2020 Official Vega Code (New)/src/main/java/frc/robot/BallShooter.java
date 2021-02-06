/////////////////////////////////////////////////////////////////////
// File: BallShooter.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Houses motors, functions, and other stuff for shooting
// lemons (power cells).
//
// Authors: Elliott DuCharme and Larry Basegio.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/29/2020.
// First tried using inheritance in this file on 2/29/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;

// Extends allows us to inherit all of the things in Robot.java,
// without having to do something nasty and disastrous like Robot robot = new Robot();
class BallShooter extends Robot {

    // Creating the 4 shooter Falcon 500s, and assigning them their ID's.
    WPI_TalonFX frontLeftShooterMotor = new WPI_TalonFX(constants.FRONT_LEFT_SHOOTER_MOTOR_ID);
    WPI_TalonFX frontRightShooterMotor = new WPI_TalonFX(constants.FRONT_RIGHT_SHOOTER_MOTOR_ID);
    WPI_TalonFX backLeftShooterMotor = new WPI_TalonFX(constants.BACK_LEFT_SHOOTER_MOTOR_ID);
    WPI_TalonFX backRightShooterMotor = new WPI_TalonFX(constants.BACK_RIGHT_SHOOTER_MOTOR_ID);

    // Creating the SpeedControllerGroups linking the front and back
    // shooter motors together.
    SpeedControllerGroup frontShooterMotors = new SpeedControllerGroup(frontLeftShooterMotor, frontLeftShooterMotor);
    SpeedControllerGroup backShooterMotors = new SpeedControllerGroup(backRightShooterMotor, backLeftShooterMotor);

    // Constructor.
    BallShooter() {

        // Setting the shooter motors in brake mode.
        frontLeftShooterMotor.setNeutralMode(NeutralMode.Brake);
        frontRightShooterMotor.setNeutralMode(NeutralMode.Brake);
        backLeftShooterMotor.setNeutralMode(NeutralMode.Brake);
        backRightShooterMotor.setNeutralMode(NeutralMode.Brake);

        // Invert some of the motors, so they spin the right way.
        frontRightShooterMotor.setInverted(true);
        backRightShooterMotor.setInverted(true);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: ballShoot()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Function used for shooting balls.
    // You can input what speeds you want the motors to run at when the
    // button is pressed.
    //
    // Arguments: double frontFalconSpeed, double backFalconSpeed
    // Speeds for the front and back motors, respectively.
    //
    // Returns: void
    //
    // Remarks: Created on 2/29/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void ballShoot(double frontFalconSpeed, double backFalconSpeed) {

        frontShooterMotors.set(frontFalconSpeed);
        backShooterMotors.set(backFalconSpeed);

    }

}
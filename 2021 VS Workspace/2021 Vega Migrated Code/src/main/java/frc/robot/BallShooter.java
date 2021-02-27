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

class BallShooter extends Constants {

    // Creating the 4 shooter Falcon 500s, and assigning them their ID's.
    WPI_TalonFX frontLeftShooterMotor;
    WPI_TalonFX frontRightShooterMotor;
    WPI_TalonFX backLeftShooterMotor;
    WPI_TalonFX backRightShooterMotor;

    // Creating the SpeedControllerGroups linking the front and back
    // shooter motors together.
    SpeedControllerGroup frontShooterMotors;
    SpeedControllerGroup backShooterMotors;

    // Constructor.
    BallShooter() {

        frontLeftShooterMotor = new WPI_TalonFX(FRONT_LEFT_SHOOTER_MOTOR_ID);
        frontRightShooterMotor = new WPI_TalonFX(FRONT_RIGHT_SHOOTER_MOTOR_ID);
        backLeftShooterMotor = new WPI_TalonFX(BACK_LEFT_SHOOTER_MOTOR_ID);
        backRightShooterMotor = new WPI_TalonFX(BACK_RIGHT_SHOOTER_MOTOR_ID);

        frontShooterMotors = new SpeedControllerGroup(frontLeftShooterMotor, frontRightShooterMotor);
        backShooterMotors = new SpeedControllerGroup(backRightShooterMotor, backLeftShooterMotor);

        // Setting the shooter motors in coast mode.
        frontLeftShooterMotor.setNeutralMode(NeutralMode.Coast);
        frontRightShooterMotor.setNeutralMode(NeutralMode.Coast);
        backLeftShooterMotor.setNeutralMode(NeutralMode.Coast);
        backRightShooterMotor.setNeutralMode(NeutralMode.Coast);

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

        // Timer.delay(1);

        backShooterMotors.set(backFalconSpeed);

        // Timer.delay(1);

    }

}
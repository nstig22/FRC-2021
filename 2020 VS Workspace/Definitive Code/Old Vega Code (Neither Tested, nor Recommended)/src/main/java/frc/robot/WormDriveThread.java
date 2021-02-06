/////////////////////////////////////////////////////////////////////
// File: WormDriveThread.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Thread used for controlling the worm drive for raising and
// lowering the ball shooter, and also the climb.
//
// Authors: Elliot DuCharme and Noah Stigeler.
//
// Environment: Microsoft VSCode Java
//
// Remarks: Created on 2/08/2020 at 5:47 PM.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;

class WormDriveThread implements Runnable {

    // Name of the Thread.
    String threadName;

    // Creating instance of the Thread class by
    // creating a thread (reserving memory for this object).
    Thread wormDriveThread;

    // Creating an instance of the Robot class in here.
    // Used for accessing the other Thread classes,
    // which are created in that file.
    Robot robot = new Robot();

    // Creating an instance of the DriveThread class in here.
    // DriveThread driveThread = new DriveThread("DriveThread");

    // Creating an instance of the Variables class in here.
    Variables variables = new Variables();

    // Creating an instance of the Sensors class.
    Sensors sensors = new Sensors();

    // Getting a reference to the Runtime class.
    // We use this stuff for garbage collection.
    // According to page 461 chapter 11 of Java: The Complete Reference 9th edition
    // by Herbert Schildt, you can't instantiate a Runtime object.
    // But, you can get a reference to it. Using this, you can control
    // the state and behavior of the Java Virtual Machine.
    // Lots of cool functions in this section: totalMemory(), freeMemory(), etc.
    // Worth a look.
    Runtime runtime = Runtime.getRuntime();

    // Creating the motors used to power the worm drive.
    CANSparkMax rightWormDriveMotor;
    // leftWormDriveMotor;
    // WPI_TalonFX rightWormDriveMotor, leftWormDriveMotor;

    // Creating a SpeedControllerGroup for grouping the 2 worm drive motors.
    // SpeedControllerGroup wormDriveMotors;

    // Magic number for controlling how fast we want the
    // worm drive motors to spin in the function down below.
    final double WORM_DRIVE_MOTORS_SPEED = 0.5;

    // Resolution of the NEO motor encoders.
    // 0.1/42 = 0.024ish.
    final double NEO_ENCODER_RESOLUTION = 0.0024;

    // WormDriveThread constructor.
    // The name of the Thread is passed in as an argument.
    WormDriveThread(String name) {

        // Assigning the name of the Thread to the argument.
        threadName = name;

        // Creating the 2 worm drive motors.
        rightWormDriveMotor = new CANSparkMax(variables.RIGHT_WORM_DRIVE_MOTOR_ID, MotorType.kBrushless);
        // leftWormDriveMotor = new CANSparkMax(variables.LEFT_WORM_DRIVE_MOTOR_ID,
        // MotorType.kBrushless);
        // rightWormDriveMotor = new WPI_TalonFX(variables.RIGHT_WORM_DRIVE_MOTOR_ID);
        // leftWormDriveMotor = new WPI_TalonFX(variables.LEFT_WORM_DRIVE_MOTOR_ID);

        // Set the worm drive motors in brake mode,
        // which should help it keep the ball shooter up where we want it.
        rightWormDriveMotor.setIdleMode(IdleMode.kBrake);
        // leftWormDriveMotor.setIdleMode(IdleMode.kBrake);
        // rightWormDriveMotor.setNeutralMode(NeutralMode.Brake);
        // leftWormDriveMotor.setNeutralMode(NeutralMode.Brake);

        // Grouping the motors.
        // wormDriveMotors = new SpeedControllerGroup(rightWormDriveMotor,
        // leftWormDriveMotor);

        // Creating the Worm Drive Thread.
        wormDriveThread = new Thread(wormDriveThread, threadName);
        wormDriveThread.start(); // Start the Thread.
    }

    // Function that actually runs stuff.
    public void run() {

        while (wormDriveThread.isAlive() == true) {

            // While this Thread is running, have this function ready to go.
            adjustShooterAngle(WORM_DRIVE_MOTORS_SPEED);

            try {
                wormDriveThread.join();
            } catch (InterruptedException e) {
                System.out.println(threadName + " Interrupted.");
            }

            // Print out when the Thread is exiting, and force garbage collection (freeing
            // of memory resources) (.gc()).
            System.out.println(threadName + " Exiting");
            runtime.gc();

        }
    }

    /////////////////////////////////////////////////////////////////////
    // Function: adjustShooterAngle()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Adjusts the angle of the ball shooter manually, by running the
    // worm drive motors either forwards or backwards.
    //
    // Arguments: double wormDriveSpeed
    //
    // Returns: void
    //
    // Remarks: Created on 2/08/2020 at 6:01 PM.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void adjustShooterAngle(double wormDriveSpeed) {

        // If the driver pushes the Square Button on the PS4 Controller,
        // set the worm drive Falcon to go backwards (lower it).
        if (robot.driveThread.PS4.getRawButton(variables.PS4_X_BUTTON) == true) {

            rightWormDriveMotor.set(-wormDriveSpeed);

            // If the driver pushes the Triangle Button on the PS4 Controller,
            // set the worm drive Falcon to go forwards (raise it up).
        } else if (robot.driveThread.PS4.getRawButton(variables.PS4_SQUARE_BUTTON) == true) {

            rightWormDriveMotor.set(wormDriveSpeed);
        }

        // If the driver is an idiot and is pressing BOTH the Square Button AND (&&) the
        // Triangle Button at the same time, OR (||) if the driver is pushing neither
        // button, set the motor speed to 0.
        else if (((robot.driveThread.PS4.getRawButton(variables.PS4_X_BUTTON) == true)
                && (robot.driveThread.PS4.getRawButton(variables.PS4_SQUARE_BUTTON) == true))
                || ((robot.driveThread.PS4.getRawButton(variables.PS4_X_BUTTON) == false)
                        && (robot.driveThread.PS4.getRawButton(variables.PS4_SQUARE_BUTTON) == false))) {

            rightWormDriveMotor.set(0);
        }

    }

    /////////////////////////////////////////////////////////////////////
    // Function: wormDriveControlAutoGyro(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Controls the worm drive motors in autonomous with the gyro.
    //
    // Arguments: double targetAngle (our angle we want to be at).
    //
    // Returns: void
    //
    // Remarks: Created on 2/14/2020 at 1:48 PM.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void wormDriveControlAutoGyro(double targetAngle) {

        // Get our initial gyro angle.
        double initAngle = sensors.wormDriveGyroAngle;

        // If our initial angle is greater than OR less than our intended angle,
        // move it to there.
        // Else if it's already there, do nothing.
        if (initAngle > targetAngle) {

            while (sensors.wormDriveGyroAngle > targetAngle) {

                rightWormDriveMotor.set(-WORM_DRIVE_MOTORS_SPEED);

                if (initAngle == targetAngle) {
                    rightWormDriveMotor.set(0);
                    break; // Break out of the while loop.
                }

            }

            // If our initial angle is less than our intended target angle,
            // move it to that angle.
        } else if (initAngle < targetAngle) {

            while (sensors.wormDriveGyroAngle < targetAngle) {

                rightWormDriveMotor.set(WORM_DRIVE_MOTORS_SPEED);

                if (initAngle == targetAngle) {
                    rightWormDriveMotor.set(0);
                    break; // Break out of the while loop.
                }

            }

            // If we're already at our target angle, do nothing.
        } else if (sensors.wormDriveGyroAngle == targetAngle) {
            // Do nothing.
        }

        // Delay so the robot has adequate time to adjust the arm.
        Timer.delay(0.75);

        // End of wormDriveControlAutoGyro(...).
    }

    /////////////////////////////////////////////////////////////////////
    // Function: wormDriveControlAutoEncoder(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Controls the worm drive motors in autonomous with the encoder.
    //
    // Arguments: double targetEncCounts (our encoder value we want to be at).
    //
    // Returns: void
    //
    // Remarks: Created on 2/14/2020 at 3:59 PM.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void wormDriveControlAutoEncoder(double targetEncCounts) {

        // Get our initial encoder counts.
        double initCounts = sensors.rightWormDriveEncoderValue;

        // If our initial encoder reading is greater than OR less than our intended
        // reading, move it to there.
        // Else if it's already there, do nothing.
        if (initCounts > targetEncCounts) {

            while (sensors.rightWormDriveEncoderValue > targetEncCounts) {

                rightWormDriveMotor.set(-WORM_DRIVE_MOTORS_SPEED);

                // Once we get there, stop the motors and break out of the while loop.
                if (sensors.rightWormDriveEncoderValue == targetEncCounts) {
                    rightWormDriveMotor.set(0);
                    break; // Break out of the while loop.
                }

            }

            // If our initial angle is less than our intended target angle,
            // move it to that angle.
        } else if (initCounts < targetEncCounts) {

            while (sensors.rightWormDriveEncoderValue < targetEncCounts) {

                rightWormDriveMotor.set(WORM_DRIVE_MOTORS_SPEED);

                // Once we get there, stop the motors and break out of the while loop.
                if (sensors.rightWormDriveEncoderValue == targetEncCounts) {
                    rightWormDriveMotor.set(0);
                    break; // Break out of the while loop.
                }

            }

            // If we're already at our target angle, do nothing.
        } else if (sensors.rightWormDriveEncoderValue == targetEncCounts) {
            // Do nothing.
        }

        // Delay so the robot has adequate time to adjust the arm.
        Timer.delay(0.75);

        // End of wormDriveControlAutoEncoder(...).

    }

}
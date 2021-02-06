/////////////////////////////////////////////////////////////////////
// File: BallShootThread.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Thread used for controlling the mechanisms for shooting
// lemons (power cells).
//
// Authors: Elliot DuCharme and Noah Stigeler.
//
// Environment: Microsoft VSCode Java
//
// Remarks: Created on 2/08/2020.
// "This" for the first argument wasn't working, even though in
// DriveThread that is how we do it...?
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;

class BallShootThread implements Runnable {

    // Name of the Thread.
    String threadName;

    // Creating instance of the Thread class by
    // creating a thread (reserving memory for this object).
    Thread ballShootThread;

    // Creating an instance of the Robot class in here.
    // Used for accessing the other Thread classes,
    // which are created in that file.
    Robot robot = new Robot();

    // Creating an instance of the DriveThread
    // Just used for getting the PS4 button press.
    // DriveThread driveThread = new DriveThread("DriveThread");

    // Creating an instance of the Variables class.
    Variables variables = new Variables();

    // Creating an instance of the Sensors class.
    // Used for the ballShoot(...) function.
    Sensors sensors = new Sensors();

    // Creating an instance of the ComputeTrajectory class in here.
    ComputeTrajectory computeTrajectory = new ComputeTrajectory(Robot.x0, sensors.proximitySensorDistance, Robot.y0,
            Robot.y, Robot.v);

    // Getting a reference to the Runtime class.
    // We use this stuff for garbage collection.
    // According to page 461 chapter 11 of Java: The Complete Reference 9th edition
    // by Herbert Schildt, you can't instantiate a Runtime object.
    // But, you can get a reference to it. Using this, you can control
    // the state and behavior of the Java Virtual Machine.
    // Lots of cool functions in this section: totalMemory(), freeMemory(), etc.
    // Worth a look.
    Runtime runtime = Runtime.getRuntime();

    // Creating the motors for the ball shooter.
    WPI_TalonFX frontLeftShooterMotor, frontRightShooterMotor, backLeftShooterMotor, backRightShooterMotor;

    // Magic numbers for controlling the speeds of the shooter motors.
    final double FRONT_SHOOTER_MOTORS_SPEED = 1; // 100%
    final double BACK_SHOOTER_MOTORS_SPEED = 0.75; // 75%

    // Grouping motors together, so it's easier to control them.
    SpeedControllerGroup frontShooterMotors, backShooterMotors;

    // BallShootThread constructor.
    // The name of the Thread is passed in as an argument.
    BallShootThread(String name) {

        // Assigning the name of the Thread to the argument.
        threadName = name;

        // Creating the 4 shooter Falcon 500s, and assigning them their ID's.
        frontLeftShooterMotor = new WPI_TalonFX(variables.FRONT_LEFT_SHOOTER_MOTOR_ID);
        frontRightShooterMotor = new WPI_TalonFX(variables.FRONT_RIGHT_SHOOTER_MOTOR_ID);
        backLeftShooterMotor = new WPI_TalonFX(variables.BACK_LEFT_SHOOTER_MOTOR_ID);
        backRightShooterMotor = new WPI_TalonFX(variables.BACK_RIGHT_SHOOTER_MOTOR_ID);

        // Creating the SpeedControllerGroups linking the front and back
        // shooter motors together.
        frontShooterMotors = new SpeedControllerGroup(frontLeftShooterMotor, frontLeftShooterMotor);
        backShooterMotors = new SpeedControllerGroup(backRightShooterMotor, backLeftShooterMotor);

        // Setting the shooter motors in brake mode.
        frontLeftShooterMotor.setNeutralMode(NeutralMode.Brake);
        frontRightShooterMotor.setNeutralMode(NeutralMode.Brake);
        backLeftShooterMotor.setNeutralMode(NeutralMode.Brake);
        backRightShooterMotor.setNeutralMode(NeutralMode.Brake);

        // Invert shooter motors, so they spin the right way.
        frontRightShooterMotor.setInverted(true);
        backRightShooterMotor.setInverted(true);

        // Actually creating the Thread.
        ballShootThread = new Thread(ballShootThread, threadName);
        ballShootThread.start(); // Start the Thread.

    }

    // Function that actually runs stuff.
    public void run() {

        // While the Thread is alive, do stuff.
        while (ballShootThread.isAlive() == true) {

            // If the driver pushes the Right Bumper on the PS4 Controller,
            // run the ballShoot() function.
            if (robot.driveThread.PS4.getRawButton(variables.PS4_CIRCLE_BUTTON) == true) {
                ballShoot(FRONT_SHOOTER_MOTORS_SPEED, BACK_SHOOTER_MOTORS_SPEED);
            } else {
                // Do not move the motors.
                ballShoot(0, 0);
            }

            // If the driver pushes the X Button on the PS4 Controller, run the
            // adjustAngleOfShooter() function.
            // This function is the magic function that uses math and stuff to compute
            // trajectory and stuff.
            // if (robot.driveThread.PS4.getRawButton(variables.PS4_X_BUTTON) == true) {
            // computeTrajectory.adjustAngleOfShooter();
            // } else {
            // Do not move the motors.
            // ballShoot(0, 0);
            // }

            // Thread class provides the join() method which allows one thread to wait until
            // another thread completes its execution.
            // Basically, if t is a Thread object whose thread is currently executing, then
            // t.join() will make sure that t is terminated before the next instruction is
            // executed by the program.
            try {
                ballShootThread.join();
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
    // Remarks: Used in adjustAngleOfShooter() in ComputeTrajectory.java.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void ballShoot(double frontFalconSpeed, double backFalconSpeed) {

        // While the robot has at least 1 ball left, run the shooter motors at the
        // passed-in speed.
        // while (sensors.robotCarryingBalls == true) {

        frontShooterMotors.set(frontFalconSpeed);
        backShooterMotors.set(backFalconSpeed);

        // Timer.delay(0.35);

        // }
    }

}
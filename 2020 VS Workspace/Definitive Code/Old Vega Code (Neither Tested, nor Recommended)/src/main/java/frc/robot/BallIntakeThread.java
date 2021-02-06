/////////////////////////////////////////////////////////////////////
// File: BallIntakeThread.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: 
//
// Authors: Elliot DuCharme and Noah Stigeler.
//
// Environment: Microsoft VSCode Java
//
// Remarks: Created on 2/08/2020.
// This for the first argument wasn't working, even though in
// DriveThread that is how we do it...?
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

class BallIntakeThread implements Runnable {

    // Name of the Thread.
    String threadName;

    // Creating an instance of the Robot class in here.
    // Used for accessing the other Thread classes,
    // which are created in that file.
    Robot robot = new Robot();

    // Creating an instance of the Variables class.
    Variables variables = new Variables();

    // Creating an instance of the DriveThread
    // Just used for getting the PS4 button press.
    // DriveThread driveThread = new DriveThread("DriveThread");

    // Creating instance of the Thread class by
    // creating a thread (reserving memory for this object).
    Thread ballIntakeThread;

    // Getting a reference to the Runtime class.
    // We use this stuff for garbage collection.
    // According to page 461 chapter 11 of Java: The Complete Reference 9th edition
    // by Herbert Schildt, you can't instantiate a Runtime object.
    // But, you can get a reference to it. Using this, you can control
    // the state and behavior of the Java Virtual Machine.
    // Lots of cool functions in this section: totalMemory(), freeMemory(), etc.
    // Worth a look.
    Runtime runtime = Runtime.getRuntime();

    // Creating the Falcon 500 for the ball intake.
    WPI_TalonFX ballIntakeMotor;

    // BallIntakeThread constructor.
    // The name of the Thread is passed in as an argument.
    BallIntakeThread(String name) {

        // Assigning the name of the Thread to the argument.
        threadName = name;

        // Creating and assigning the intake Falcon an ID.
        ballIntakeMotor = new WPI_TalonFX(variables.BALL_INTAKE_MOTOR_ID);

        // Setting the intake Falcon 500 motor in brake mode.
        ballIntakeMotor.setNeutralMode(NeutralMode.Brake);

        // Actually creating the Thread.
        ballIntakeThread = new Thread(ballIntakeThread, threadName);
        ballIntakeThread.start(); // Start the Thread.
    }

    // Function that actually runs stuff.
    public void run() {

        // While the Thread is alive, do stuff.
        while (ballIntakeThread.isAlive() == true) {

            // If the driver pushes the right bumper button on the PS4 Controller,
            // run the intake motors forward (inwards).
            if (robot.driveThread.PS4.getRawButton(variables.PS4_RIGHT_BUMPER) == true) {
                ballIntake(0.65, 0.5);

                // Else if the driver pushes the left bumper button on the PS4 Controller,
                // run the intake motors backwards (outward).
            } else if (robot.driveThread.PS4.getRawButton(variables.PS4_LEFT_BUMPER) == true) {
                ballIntake(-0.65, -0.5);
            } else {
                // Else, set the motors to 0 (don't run them).
                ballIntake(0, 0);
            }

            // Thread class provides the join() method which allows one thread to wait until
            // another thread completes its execution.
            // Basically, if t is a Thread object whose thread is currently executing, then
            // t.join() will make sure that t is terminated before the next instruction is
            // executed by the program.
            try {
                ballIntakeThread.join();
            } catch (InterruptedException e) {
                System.out.println(threadName + " Interrupted.");
            }

            // Print out when the Thread is exiting, and force
            // garbage collection (freeing of memory resources) (.gc()).
            System.out.println(threadName + " Exiting");
            runtime.gc();

        }

    }

    /////////////////////////////////////////////////////////////////////
    // Function: ballIntake(...)
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
    // Remarks: Rewrote on 2/16/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void ballIntake(double intakeSpeed, double topBeltSpeed) {

        // Run the intake motor at the inputted speed.
        ballIntakeMotor.set(intakeSpeed);

        // Run the back shooter motors at the inputted speed.
        robot.ballShootThread.backShooterMotors.set(topBeltSpeed);

    }
}
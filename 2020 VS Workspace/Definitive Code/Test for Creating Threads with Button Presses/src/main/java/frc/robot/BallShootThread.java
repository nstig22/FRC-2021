/////////////////////////////////////////////////////////////////////
// File: BallShootThread.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Thread used for controlling the mechanisms for shooting
// lemons (power cells).
//
// Authors: Elliot DuCharme and Larry Basegio.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/27/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import edu.wpi.first.wpilibj.Timer;

class BallShootThread extends Constants implements Runnable {

    final double TARGET_VELOCITY = 0; // TODO get an actual value for this...

    int count = 3;

    String threadName;

    // Creating instance of the Thread class by
    // creating a thread (reserving memory for this object).
    Thread ballShootThread;

    // Getting a reference to the Runtime class.
    // We use this stuff for garbage collection.
    Runtime runtime = Runtime.getRuntime();

    // BallShootThread constructor.
    // The name of the Thread is passed in as an argument.
    BallShootThread(String name) {

        // Name of the Thread.
        threadName = name;

        // Actually creating the Thread.
        ballShootThread = new Thread(ballShootThread, threadName);
        ballShootThread.start(); // Start the Thread.
    }

    // Function that actually runs stuff.
    public void run() {

        double proximitySensorDistance, frontLeftShooterVelocity, frontRightShooterVelocity;

        int backLeftShooterCounts, backRightShooterCounts, backLeftShooterTarget, backRightShooterTarget;

        // While the Thread is alive, do stuff.
        while (count > 0) {

            proximitySensorDistance = proximitySensor.getDistance();

            // TODO get lookup theta thing.

            ballShooter.frontShooterMotors.set(1);

            frontLeftShooterVelocity = ballShooter.frontLeftShooterMotor.getSelectedSensorVelocity();
            frontRightShooterVelocity = ballShooter.frontRightShooterMotor.getSelectedSensorVelocity();

            System.out.println("leftMotorVelocity: " + frontLeftShooterVelocity);
            System.out.println("rightMotorVelocity: " + frontRightShooterVelocity);

            backLeftShooterCounts = ballShooter.backLeftShooterMotor.getSelectedSensorPosition();
            backRightShooterCounts = ballShooter.backRightShooterMotor.getSelectedSensorPosition();

            backLeftShooterTarget = backLeftShooterCounts + 2608; // TODO make this number a magic number.
            backRightShooterTarget = backRightShooterCounts + 2608;

            while ((backLeftShooterTarget < backLeftShooterTarget)
                    && (backRightShooterCounts < backRightShooterTarget)) {

                ballShooter.backShooterMotors.set(0.2);
            }

            ballShooter.backShooterMotors.set(0);
            ballShooter.frontShooterMotors.set(0);

            count--;

            Timer.delay(2);
        }

        System.out.println(threadName + " exiting.");

        runtime.gc();
    }
}
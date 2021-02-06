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

// This Thread extends BallShooter (which also extends Robot).
class BallShootThread extends BallShooter implements Runnable {

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
        String threadName = name;

        // Actually creating the Thread.
        ballShootThread = new Thread(ballShootThread, threadName);
        ballShootThread.start(); // Start the Thread.
    }

    // Function that actually runs stuff.
    public void run() {

        // While the Thread is alive, do stuff.
        while (ballShootThread.isAlive() == true) {

            // If the X button on the PS4 is pressed,
            // and the square button is NOT pressed...
            if ((PS4.getRawButton(constants.PS4_X_BUTTON) == true)
                    && (PS4.getRawButton(constants.PS4_SQUARE_BUTTON) == false)) {

                // Shoot balls with front motors at 100% and back motors at 40%.
                ballShoot(1, 0.4);

                // Else if the X button on the PS4 is not pressed,
                // and the square button IS pressed...
            } else if ((PS4.getRawButton(constants.PS4_X_BUTTON) == false)
                    && (PS4.getRawButton(constants.PS4_SQUARE_BUTTON) == true)) {

                // Run the ball shooter motors backwards at 100% and back motors at 40%.
                ballShoot(-1, -0.4);

            } else {

                // Else, don't run the motors.
                ballShoot(0, 0);
            }

        }

    }
}
/////////////////////////////////////////////////////////////////////
// File: WormDriveThread.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Thread used for controlling the worm drive for raising and
// lowering the ball shooter.
//
// Authors: Elliot DuCharme and Larry Basegio.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/29/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

class WormDriveThread extends WormDrive implements Runnable {

    // Creating instance of the Thread class by
    // creating a thread (reserving memory for this object).
    Thread wormDriveThread;

    // Getting a reference to the Runtime class.
    Runtime runtime = Runtime.getRuntime();

    // WormDriveThread constructor.
    // The name of the Thread is passed in as an argument.
    WormDriveThread(String name) {

        // Name of the Thread.
        String threadName = name;

        // Creating the Worm Drive Thread.
        wormDriveThread = new Thread(wormDriveThread, threadName);
        wormDriveThread.start(); // Start the Thread.

    }

    // Function that actually runs stuff.
    public void run() {

        while (wormDriveThread.isAlive() == true) {

            // While this Thread is running, have this function ready to go.
            // The passed in speed is 50%.
            adjustShooterAngleManual(0.5);
        }
    }

}
/////////////////////////////////////////////////////////////////////
// File: ClimbThread.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Thread used for controlling the mechanisms for climbing.
//
// Authors: Elliot DuCharme and Noah Stigeler.
//
// Environment: Microsoft VSCode Java
//
// Remarks: Created on 2/08/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

class ClimbThread implements Runnable {

    // Name of the Thread.
    String threadName;

    // Creating instance of the Thread class by
    // creating a thread (reserving memory for this object).
    Thread climbThread;

    // Creating an instance of the Robot class in here.
    // Used for accessing the other Thread classes,
    // which are created in that file.
    Robot robot = new Robot();

    // Getting a reference to the Runtime class.
    // We use this stuff for garbage collection.
    // According to page 461 chapter 11 of Java: The Complete Reference 9th edition
    // by Herbert Schildt, you can't instantiate a Runtime object.
    // But, you can get a reference to it. Using this, you can control
    // the state and behavior of the Java Virtual Machine.
    // Lots of cool functions in this section: totalMemory(), freeMemory(), etc.
    // Worth a look.
    Runtime runtime = Runtime.getRuntime();

    // TODO Create motors and methods and stuff for climbing.

    // ClimbThread constructor.
    // The name of the Thread is passed in as an argument.
    ClimbThread(String name) {

        // Assigning the name of the Thread to the argument.
        threadName = name;

        // Actually creating the Thread.
        climbThread = new Thread(this, threadName);
        climbThread.start(); // Start the Thread.

    }

    // Function that actually runs stuff.
    public void run() {

        // While the Thread is alive.
        while (climbThread.isAlive() == true) {

            // Thread class provides the join() method which allows one thread to wait until
            // another thread completes its execution.
            // Basically, if t is a Thread object whose thread is currently executing, then
            // t.join() will make sure that t is terminated before the next instruction is
            // executed by the program.
            try {
                climbThread.join();
            } catch (InterruptedException e) {
                System.out.println(threadName + " Interrupted.");
            }

            // Print out when the Thread is exiting, and force
            // garbage collection (freeing of memory resources) (.gc()).
            System.out.println(threadName + " Exiting");
            runtime.gc();

        }
    }

}
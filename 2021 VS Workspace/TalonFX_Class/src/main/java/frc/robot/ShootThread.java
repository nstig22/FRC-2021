/////////////////////////////////////////////////////////////////////
//  File:  ShootThread.java
/////////////////////////////////////////////////////////////////////
//
//Purpose:  Defines the thread class responsible for shooting the
//          ball.  Intended to be activated with a joystick button
//          or an autonomous call.
//
//Programmers:  
//
//Revisions:  Completely rewritten 02/01/2021
//
//Remarks: 
//
//         Implementing a drive thread for autonomous
//         operations.  Add the various movements within the run()
//         function.  Attempts have been made to simplify and make
//         functions consistent.
//

/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

/////////////////////////////////////////////////////////////////////
//  Class:  ShootThread
/////////////////////////////////////////////////////////////////////
//
//Purpose: Defines the parameters used to drive the robot.
//
//
//Remarks:  FRC iterative robot template.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
class ShootThread implements Runnable {
	String name;
	Thread t;
	Runtime r = Runtime.getRuntime();
	private Delay delay;
	
	// Constructor
	ShootThread(String threadname) {

		
		name = threadname;
		t = new Thread(this, name);
		System.out.println("New thread: " + t);
		delay = new Delay();
		t.start(); // Start the thread
	}

	public void run() {
		while (Robot.shoot.t.isAlive() == true) {

			Robot.shoot_thread_active = true;
				
			//  Need to call once to control motor speed.  This thread version
			//  is completely different from the version for teleop()
			Robot.lf_shoot.setVelocity(Robot.lf_shoot.getTargetVelocity(),Robot.lf_shoot.duration);
			delay.delay_milliseconds(5);
				
			// Wait for the thread to complete
			try {
				Robot.shoot.t.join();
			} catch (InterruptedException e) {
				System.out.println(name + "Interrupted.");
			}
		  
			System.out.println(name + "Exiting Shoot Thread");
			r.gc(); // force garbage collection (freeing of memory resources)

			// Reset flag
			Robot.shoot_thread_active = false;
		}

	    // Should get a false indication - why don't we see these screen outputs
		System.out.println("Thread Status = " + Robot.shoot.t.isAlive());
	
	}

}
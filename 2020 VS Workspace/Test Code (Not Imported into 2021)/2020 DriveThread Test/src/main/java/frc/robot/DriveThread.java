/////////////////////////////////////////////////////////////////////
//  File:  DriveThread.java
/////////////////////////////////////////////////////////////////////
//
//Purpose:  Defines the thread class responsible for robot movement forward
//          and backward, left and right turns during autonomous
//          operation.  All autonomous movements would be included
//          in the t.run() function.
//
//Programmers:  
//
//Revisions:  Completely rewritten 12/28/2019
//
//Remarks: 
//
//         12/28/2019:  Implementing a drive thread for autonomous
//         operations.  Add the various movements within the run()
//         function.  Attempts have been made to simplify and make
//         functions consistent.
//
//         This version directly reads the sensors and uses a
//         while() loop in that the thread will "share" CPU
//         time with the main execution thread or other threads
//         that are active.  
//
//         01/05/2020 - 01/30/2020:  Attempted to add acceleration
//         and decelaration to the autonomous movements.  I sure
//         hope that previous versions that worked have been
//         baked up.  At the end of this phase of development the
//         robot would accelerate, stop, and spin circles.  Cause
//         not known at this point, changes of 02/11/2020 are 
//         extensive and should aid in discovery.  All loop exit
//         strategies have been eliminated in driveFwd() for 
//         simplicity.  Focus will be on getting this single
//         function correct before proceeding.
//
//         02/11/2020:  Examination of the code indicated that
//         the large and frequent number of print statements will 
//         make determination of the cause of the issues very difficult.  
//         The following changes have been made regarding the implementation
//         of the key autonomous function "driveFwd( ... )".
//
//         1.  In the initial inequality following acceleration -
//             the condition regarding fraction had the wrong sign.
//             The fraction starts as 1.0 and decreases as the target
//             encoder value is approached.  Therefore the condition
//             for exiting the high speed while() loop should have
//             been when the fraction is less than "BRAKE_FRACTION",
//             i.e., while(( ... )&&(fraction>BRAKE_FRACTION)) exits
//             when fraction<BRAKE_FRACTION.
//         2.  Way too many print statements - impossible to tract
//             key moments, i.e., exits from while loops.
//         3.  Eliminated heading angle reads and corrections during 
//             acceleration and deceleration.
//         4.  The delays within the while() loop were increased
//             from 0.010 to 0.020 seconds.  I just didn't we needed
//             to update that fast.
//
//         It is anticipated that the parameters associated with
//         fixed moves may need to be adjusted for different distances.
//         At the front end of driveFwd() it may be necessary to
//         create a series of if(),elseif(),.. etc. to accomodate
//         a range of travel distances.  For a movement of 1 - 3 feet
//         there may not be a need for acceleration and the start
//         speed might be slow.  For distance of 15 feet the maximum
//         speed might be larger that 0.6, etc..  You get the idea.
//
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import edu.wpi.first.wpilibj.Timer;

/////////////////////////////////////////////////////////////////////
//  Class:  DriveThread
/////////////////////////////////////////////////////////////////////
//
//Purpose: Defines the parameters used to drive the robot.
//
//
//Remarks:  FRC iterative robot template.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
class DriveThread implements Runnable {
	String name;
	Thread t;
	Runtime r = Runtime.getRuntime();
	private Delay delay;

	// Fixed parameters for conversion of distance to encoder counts
	// final double WHEEL_DIAMETER = 8.0;
	final double INCHES_PER_FOOT = 12.0;
	final double CM_PER_METER = 100.0;

	// Encoders are now on the motors (NEOS). Output from the
	// encoder in this case is 1.0. A gear reduction of 12.75:1 implies
	// 12.75 per revolution of the output shaft. The precision of this
	// is 42 counts per motor shaft revolution times 12.75 of the gear
	// reduction = 1/672. An eight inch wheel diameter implies a distance
	// traveled of PI*8.0 = 25.17 inches. Distance resolution of the
	// encoder/gearbox combination is 25.17/672 = 0.037 inches/count. Should be
	// good enough.
	// However the output when reading the encoder function
	// is 1.0 for each revolution of the motor, or 12.75 for one revolution
	// of the output shaft. This implies that the inch to output
	// conversion is 25.17/12.75 or 1.572 inches per unit output
	final double ENCODER_RESOLUTION = 0.5065; // inches per output value

	// Fixed parameters for driveFwd(...)/driveBwd(...)
	final double START_SPEED = 0.1; // Also used in acceleration functions.
	final double MAX_SPEED = 0.6;
	final double MIN_SPEED = 0.1;
	final double BRAKE_SPEED = 0.3;
	final double BRAKE_FRACTION = 0.4;

	// Fixed parameters for console updates and while() loop escapes
	final int ENC_CONSOLE_UPDATE = 20;
	final int ENC_LOOP_ESCAPE = 250;
	final int GYRO_CONSOLE_UPDATE = 20;
	final int GYRO_LOOP_ESCAPE = 200;

	// Fixed parameters for gyro operation. Specified here to facilitate
	// changes without confusion in the various functions using these
	// variables.
	final double ROT_SPEED = 0.5; // Starting rotation speed for turning
	// As we approach the target we reduce the speed by this factor
	final double ROT_ATTEN = 1.5;
	// proximity (in degrees) to target angle stage 1 rotation attenuation rate
	final double ANGL_PROX_1 = 25.0;
	// proximity (in degrees) to target angle stage 2 rotation attenuation rate
	final double ANGL_PROX_2 = 5.0;

	public static double position1;
	public double initPosition1;

	public double init_Angle;

	// Constructor
	DriveThread(String threadname) {

		// We want to establish an initial encoder reading. This will enable reseting
		// encoder position to zero when we start moving. We use absolute values to
		// make the subsequent subtraction more easily interpreted.
		Robot.left_enc.setPosition(0.0);
		initPosition1 = Robot.left_enc.getPosition(); // should be zero
		position1 = initPosition1;
		init_Angle = Robot.driveGyro.getAngle();

		// The initial position should be zero.
		System.out.println("Left Encoder Initial Position = " + initPosition1);
		System.out.println("Initial Heading = " + init_Angle);

		name = threadname;
		t = new Thread(this, name);
		System.out.println("New thread: " + t);
		delay = new Delay();
		t.start(); // Start the thread
	}

	public void run() {
		while (Robot.auto_drive.t.isAlive() == true) {

			// Set the flag active so that any joystick
			// manipulations are disabled while this
			// thread is active. Note that delays within
			// this thread will not affect the main()
			// program.
			Robot.drive_thread_active = true;

			// The various member functions would be called here.
			// For example:
			// driveFwd(5.0); // move the robot forward 5.0 feet

			// delay.delay_milliseconds(250.0);

			// turnAbsolute(-90.0); // rotate 45 degrees CW

			// delay.delay_milliseconds(250.0);

			// turn2Heading(315.0); // turn to heading of 315 degrees

			// turnAbsolute(180);

			// delay.delay_milliseconds(100);

			// turnAbsolute(-90);

			// driveFwd(15.0);

			turnRight_Arcade(90);

			// Wait for the thread to complete
			try {
				Robot.auto_drive.t.join();
			} catch (InterruptedException e) {
				System.out.println(name + "Interrupted.");
			}

			System.out.println(name + "Exiting Drive Thread");
			r.gc(); // force garbage collection (freeing of memory resources)

			// Reset flag
			Robot.drive_thread_active = false;
		}

		// Should get a false indication
		System.out.println("Thread Status = " + Robot.auto_drive.t.isAlive());
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public int driveFwd(double distance)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Drives the robot forward the distance in feet specified
	// by the argument.
	//
	// Arguments:double distance, The distance to be traveled in feet.
	//
	// Returns: An double representing overshoot/undershoot of the movement
	// in inches.
	//
	// Remarks: 02/09/2020: Modified to include acceleration/deceleration
	// 02/11/2020: Noted that the inequality regarding fraction
	// should have been '>' vs. '<'
	// Increased delays within while() loops
	// Reduced print statements to make is easier
	// to determine position and fraction.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double driveFwd(double distance) {
		int loop_count = 0;

		double counts;
		double initial_position;
		double current_position;
		double target;

		double fraction;
		double heading;

		double error;

		// Determine where we are pointing - we want to maintain this heading during
		// this forward movement.
		heading = Robot.driveGyro.getAngle();
		Robot.left_enc.setPosition(0.0);

		// Read encoder #1, get the initial encoder position and assign it to
		// the current position. Calculate the number of encoder counts
		// necessary to reach the final destination (target).
		initial_position = Robot.left_enc.getPosition(); // should be zero

		System.out.println("initPos = " + initial_position);

		current_position = initial_position;
		counts = calcCounts_SAE(distance);
		target = initial_position + counts;

		// fraction starts out as equal to 1.0 and decreases as we approach the target.
		// fraction is counts remaining divided by total counts.
		fraction = Math.abs((target - current_position) / (target - initial_position));

		System.out
				.println("initial_position = " + initial_position + " target = " + target + " fraction = " + fraction);

		// We attempt a bit of proportional control as we approach the target. We want
		// to slow down so that we don't overshoot. These fractions appear to work.
		// We drive at high speed for 80% of the distance and then slow. On carpet this
		// seemed to work very well for distance of 10 feet.
		// We want braking enabled.
		// We also need to put a timer within the while() loop to provide an escape in
		// the event that the system gets lost during autonomous requiring a restart of
		// the
		// program.

		// get moving and accelerate to MAX_SPEED
		if (current_position < target) {
			accelerateFwd();
		}
		System.out.println("Acceleration complete");

		// Monitor position and continue to travel at max speed until
		// fraction<BRAKE_FRACTION. Note that fraction starts out at 1.0
		// and decreases as we approach the target encoder value.
		// 02/11/2020: Changed the sign of the inequality to '>'
		// Changed the delay from 0.01 to 0.02
		// Moved the position update outside of the while() loop
		while ((current_position < target) && (fraction > BRAKE_FRACTION)) {
			moveFwd(MAX_SPEED, heading);
			current_position = Robot.left_enc.getPosition();
			fraction = Math.abs((target - current_position) / (target - initial_position));
			Timer.delay(0.02);
		}

		// Where are we?
		System.out
				.println("current_position = " + current_position + " target = " + target + " fraction = " + fraction);
		System.out.println("Decelerating");
		// Ok, we should be at the braking fraction. Time to decelerate to BRAKE_SPEED
		decelerateFwd();

		// Continue at BRAKE_SPEED until we reach the target encoder value
		// 02/11/2020: Changed the delay from 0.01 to 0.02
		// 02/11/2020: Moved the position update outside of the while() loop
		while (current_position < target) {
			moveFwd(BRAKE_SPEED, heading);
			Timer.delay(0.02);
			current_position = Robot.left_enc.getPosition();
		}

		System.out
				.println("current_position = " + current_position + " target = " + target + " fraction = " + fraction);

		// stop movement, compute error (overshoot or undershoot)
		Robot.diff_drive.arcadeDrive(0, 0);
		current_position = Robot.left_enc.getPosition();
		error = calcDistance_SAE(current_position - target);
		System.out.println("error = " + error + " inches");

		return (error);
	}

	///////////////////////////////////////////////////////////////////////////
	// Function: void moveFwd(double speed,double target)
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Uses on_board gyro to drive the robot straight forward
	// given a target angle as an argument.
	// Requires use of the arcadeDrive function with the first
	// argument being the forward speed and the second being
	// the turn applied.
	//
	// Arguments: double speed. Must be between -1.0 and 1.0.
	// double heading - the target angle.
	//
	// Returns: void
	//
	// Remarks: This function will be called every 20 msec. Updating
	// the position and heading each time it is called will
	// overwhelm the system.
	// 02/11/2020: Commented out the print statement.
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public void moveFwd(double speed, double heading) {

		double corr = 0.2;
		double angle = 0;
		double delta; // The difference between the target and measured angle

		angle = Robot.driveGyro.getAngle();

		delta = angle - heading;

		// According to the documentation for DifferentialDrive.arcadeDrive(speed,turn)
		// the arguments are squared to accomodate lower drive speeds. If for example
		// the gain coefficient is 0.05 and the angle error is 5 degrees, the turning
		// argument would be 0.25*0.25 = 0.0625. This is a pretty slow correction.
		// We needed a larger correction factor - trying 0.2 for now. The range for
		// the turn is -1.0 to 1.0. Positive values are said to turn clockwise,
		// negatives counterclockwise.
		Robot.diff_drive.arcadeDrive(speed, -corr * delta);

		// System.out.println(" heading = " + heading + " angle = " + angle + " delta =
		// " + delta);

	}

	/////////////////////////////////////////////////////////////////////
	// Function: accelerateFwd( ... )
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Used for accelerating the robot with driveFwd().
	//
	// Arguments: double heading, i.e., the direction of travel.
	//
	// Returns: void
	//
	// Remarks: The listed speed increment and delay will allow
	// approximately 2 seconds to accelerate from a
	// speed of 0.1 to 0.6. We may want to alter these
	// variables depending on the distance.
	//
	// 02/11/2020: Simplified the acceleration phase of
	// the movement. Eliminated reading of angle and
	// direction correction. This allowed eliminating
	// the "heading" argument.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	void accelerateFwd() {
		double speed = 0.1;

		while (speed < MAX_SPEED) {
			Robot.diff_drive.arcadeDrive(speed, 0);
			delay.delay_milliseconds(20.0);
			speed += 0.01;
		}
	}

	/*
	 * void accelerateFwd(double heading) { int loop_count = 0;
	 * 
	 * double speed; double corr = 0.2; double angle = 0; double delta; // The
	 * difference between the target and measured angle
	 * 
	 * angle = Robot.driveGyro.getAngle();
	 * 
	 * delta = angle - heading;
	 * 
	 * speed = 0.1;
	 * 
	 * while (speed < MAX_SPEED) { speed += 0.01;
	 * Robot.diff_drive.arcadeDrive(speed, -corr * delta); //
	 * System.out.println("accelSpeed: \t" + speed + "\t" + "delta: \t" + delta);
	 * delay.delay_milliseconds(40.0); }
	 * 
	 * // continue at max speed. Robot.diff_drive.arcadeDrive(MAX_SPEED, -corr *
	 * delta); }
	 */
	/////////////////////////////////////////////////////////////////////
	// Function: decelerateFwd( ... )
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Used for declerating the robot with driveFwd().
	//
	// Arguments: double heading, i.e., the direction of travel
	//
	// Returns: void
	//
	// Remarks: The listed speed increment and delay will allow
	// approximately 2 seconds to decelerate from a
	// speed of 0.1 to 0.6. We may want to alter these
	// variables depending on the distance.
	//
	// 02/11/2020: Simplified the deceleration phase of
	// the movement. Eliminated reading of angle and
	// direction correction.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	void decelerateFwd() {
		double speed = MAX_SPEED;

		while (speed > MIN_SPEED) {
			Robot.diff_drive.arcadeDrive(speed, 0);
			delay.delay_milliseconds(20.0);
			speed -= 0.01;
		}
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public int driveBwd(double distance)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Constructor the the class. Creates the devices used to
	// control the robot drive system.
	//
	// Arguments:double distance, The distance to be traveled in inches.
	//
	// Returns: An int representing overshoot/undershoot of the movement
	// in inches.
	//
	// Remarks: It is assumed that the first time a movement is attempted
	// that it is a forward movement. The first movement appears
	// to determine the direction of the encoder counts. What
	// we observed during the last practice was that when we
	// started the program and the first motion was backwards
	// we saw increasing encoder counts. Assumed here is that
	// if the first movement on program start is forward then
	// reversing direction should create decreasing encoder
	// counts.
	// A possible option is to use the other encoder for reverse
	// motion. We shall see..
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double driveBwd(double distance) {
		int loop_count = 0;
		double counts;
		double initial_position;
		double current_position;
		double target;

		double fraction;
		double heading;

		double error;

		// Determine where we are pointing - we want to maintain this heading during
		// this
		// forward movement.
		heading = Robot.driveGyro.getAngle();
		Robot.left_enc.setPosition(0.0);

		// Read encoder #1, get the initial encoder position and assign it to
		// the current position. Calculate the number of encoder counts
		// necessary to reach the final destination (target).
		initial_position = Robot.left_enc.getPosition();
		current_position = initial_position;
		counts = calcCounts_SAE(distance);
		target = initial_position - counts;

		// fraction starts out as equal to 1.0 and decreases as we approach the target.
		// fraction is counts remaining divided by total counts.
		fraction = Math.abs((target - current_position) / (target - initial_position));

		System.out
				.println("initial_position = " + initial_position + " target = " + target + " fraction = " + fraction);

		// We attempt a bit of proportional control as we approach the target. We want
		// to slow down so that we don't overshoot. These fractions appear to work.
		// We drive at high speed for 80% of the distance and then slow. On carpet this
		// seemed to work very well for distance of 10 feet.
		// We want braking enabled.
		// We also need to put a timer within the while() loop to provide an escape in
		// the
		// event that the system gets lost during autonomous requiring a restart of the
		// program.

		// Need to test and determine sign of inequality. We had the curious behavior of
		// increasing encoder counts when moving backwards. It could have been due to
		// the fact that the first motion was backwards in our "spaghetti code". We will
		// Need to play around with the signs to get it working right.
		while (current_position > target) {
			if (fraction > BRAKE_FRACTION) {
				moveBwd(START_SPEED, heading);
			} else {
				moveBwd(BRAKE_SPEED, heading);
			}
			Timer.delay(0.01);
			// We don't want to stay longer than we have to. Assuming
			// that the 10 msec is reasonably accurate we limit the
			// move to 5 seconds for starters.
			loop_count++;
			if ((loop_count % ENC_CONSOLE_UPDATE) == 0) {
				// Provide periodic status
				System.out.println(
						"current_position = " + current_position + " target = " + target + " fraction = " + fraction);
			}
			if (loop_count == ENC_LOOP_ESCAPE) {
				break; // escape clause
			}
			current_position = Robot.left_enc.getPosition();
			fraction = Math.abs((target - current_position) / (target - initial_position));
		}

		// stop movement, compute error (overshoot or undershoot)
		Robot.diff_drive.arcadeDrive(0, 0);
		current_position = Robot.left_enc.getPosition();
		error = calcDistance_SAE(current_position - target);
		System.out.println("error = " + error + " inches");

		return (error);
	}

	///////////////////////////////////////////////////////////////////////////
	// Function: void moveBwd()
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Uses on_board gyro to drive the robot straight backward
	// given a target angle as an argument.
	// Requires use of the arcadeDrive function with the first
	// argument being the forward speed and the second being
	// the turn applied.
	//
	// Arguments: double speed. Must be between -1.0 and 1.0.
	// double angle - the target angle.
	//
	// Returns: void
	//
	// Remarks:
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public void moveBwd(double speed, double target) {

		double corr = 0.2;
		double angle = 0;
		double delta; // The difference between the target and measured angle

		angle = Robot.driveGyro.getAngle();

		delta = angle - target;

		// According to the documentation for DifferentialDrive.arcadeDrive(speed,turn)
		// the arguments are squared to accomodate lower drive speeds. If for example
		// the gain coefficient is 0.05 and the angle error is 5 degrees, the turning
		// argument would be 0.25*0.25 = 0.0625. This is a pretty slow correction.
		// We needed a larger correction factor - trying 0.2 for now. The range for
		// the turn is -1.0 to 1.0. Positive values are said to turn clockwise,
		// negatives counterclockwise.
		Robot.diff_drive.arcadeDrive(-speed, -corr * delta);

		// System.out.println(" target = " + target + " angle = " + angle + " delta = "
		// + delta);

	}

	///////////////////////////////////////////////////////////////////////////
	// Function: calcCounts_SAE(double distance)
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Computes the encoder readout corresponding to the submitted
	// distance.
	//
	// Arguments: Accepts a double representing the distance in feet.
	//
	// Returns: A double representing the encoder change associated
	// with that distance.
	//
	// Remarks: ENCODER_RESOLUTION is the value associated with one
	// inch of travel. Based on eight inch diameter wheels used
	// on the 2019 robot.
	//
	// Example: If we are traveling 10 inches, the expected change
	// in output from the encoder would be:
	//
	// 10.0/1.537 = 6.506
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public double calcCounts_SAE(double distance) {
		double enc_change;

		distance *= 12.0; // convert feet to inches.

		enc_change = distance * ENCODER_RESOLUTION;

		return (enc_change);

	}

	// Given the counts, returns the distance in inches.
	public double calcDistance_SAE(double counts) {
		double distance;

		distance = counts * ENCODER_RESOLUTION;

		return (distance);

	}

	///////////////////////////////////////////////////////////////////////////
	// Function: void calcCounts_Metric(double radius,double distance)
	///////////////////////////////////////////////////////////////////////////
	//
	// Purpose: Given the distance to be traveled in meters, this function
	// calculates the encoder change required to travel that distance.
	//
	// Arguments: double distance (in meters).
	//
	// Returns: A double representing the encoder change for specified distance.
	//
	// Remarks:
	//
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	public double calcCounts_Metric(double distance) {
		double enc_change;

		distance *= 100.0; // convert meters to centimeters
		distance /= 2.54; // convert centimeters to inches

		enc_change = distance / ENCODER_RESOLUTION;

		return (enc_change);

	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double turnRight_Arcade(double degrees)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotates the robot CW through the angle specified
	// by degrees.
	//
	// Returns: A double representing the error.
	//
	// Remarks: Uses Arcade drive to rotate the robot. Note that we
	// want motor braking enabled. Added escape count to exit
	// loop after a certain time when it doesn't reach the target.
	//
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double turnRight_Arcade(double degrees) {
		int count = 0;
		double rot_speed = ROT_SPEED;
		double angle; // current gyro angle
		double target; // target angle
		double error;

		angle = Robot.driveGyro.getAngle(); // this is our starting point
		target = angle + degrees;
		System.out.println("ANGLE = " + angle + " Target = " + target);

		while (angle < target) {
			rotatePosArcade(rot_speed);
			angle = Robot.driveGyro.getAngle();

			if (angle > (target - ANGL_PROX_1)) {
				rot_speed /= ROT_ATTEN;
			}
			if (angle > (target - ANGL_PROX_2)) {
				rot_speed /= ROT_ATTEN;
			}
			Timer.delay(0.01);
			count++;
			if ((count % GYRO_CONSOLE_UPDATE) == 0) {
				System.out.println("angle = " + angle);
			}
			if (count == GYRO_LOOP_ESCAPE)
				break;
		}
		Robot.diff_drive.arcadeDrive(0, 0);
		angle = Robot.driveGyro.getAngle();
		error = target - angle; // the error
		System.out.println("Turning Error = " + error + " degrees");
		return (error);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public void rotatePosArcade(double rot_spd)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Uses arcadeDrive(...) to rotate the robot CW
	//
	// Arguments:Accepts a double representing the rotation speed.
	//
	// Returns: void
	//
	// Remarks: According to the documentation of this function the
	// argument "rot_speed" is squared.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public int rotatePosArcade(double rot_spd) {

		if (rot_spd > 1.0) {
			return (-1);
		}
		Robot.diff_drive.arcadeDrive(0, rot_spd);
		return (0);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double turnLeft_Arcade(double degrees)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotates the robot CCW through the specified number
	// of degrees.
	//
	// Arguments: The degrees of CCW rotation.
	//
	// Returns: A double representing the error.
	//
	// Remarks: Uses Arcade drive to
	// rotate the robot. Note that we want motor braking
	// enabled.
	//
	//
	// The final variable ROT_ATTEN value is yet to be determined but
	// for the first iteration it is 2.0.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double turnLeft_Arcade(double degrees) {
		int count = 0;
		double rot_speed = ROT_SPEED;
		double angle; // current gyro angle
		double target;
		double error;

		// Current angle
		angle = Robot.driveGyro.getAngle();
		target = angle - degrees;

		while (angle > target) {
			rotateNegArcade(rot_speed);
			angle = Robot.driveGyro.getAngle();

			if (angle < (target + ANGL_PROX_1)) {
				rot_speed /= ROT_ATTEN;
			}
			if (angle < (target + ANGL_PROX_2)) {
				rot_speed /= ROT_ATTEN;
			}
			Timer.delay(0.01);
			count++;
			if ((count % GYRO_CONSOLE_UPDATE) == 0) {
			}
			if (count == GYRO_LOOP_ESCAPE)
				break;
		}
		Robot.diff_drive.arcadeDrive(0, 0);
		angle = Robot.driveGyro.getAngle();
		System.out.println("angle = " + angle);
		error = target - angle;
		System.out.println("Turning Error = " + error + " degrees");
		return (error);

	}

	/////////////////////////////////////////////////////////////////////
	// Function: public int rotateNegArcade(double rot_spd)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Uses arcadeDrive(...) to rotate the robot CCW
	//
	// Arguments:Accepts a double representing the rotation speed.
	//
	// Returns: Zero normally, will return -1 if an unacceptable
	// rotation speed is entered
	//
	// Remarks: According to the documentation of this function the
	// argument "rot_speed" is squared.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public int rotateNegArcade(double rot_spd) {
		if (rot_spd > 1.0) {
			return (-1);
		}
		Robot.diff_drive.arcadeDrive(0, -rot_spd);
		return (0);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double turnAbsolute(double degrees)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Rotate the number of degrees in the direction specified
	// by the sign of the argument "degrees". Negative arguments will
	// rotate CCW, positive arguments CW.
	//
	// Arguments: A double representing the number of degrees to rotate
	//
	//
	// Returns: A double representing the difference between the
	// achieved rotation and the requested rotation.
	//
	// Remarks: A few test cases:
	//
	// 1. Initial angle measurement is 110 degrees, we request a -35
	// degree rotation. Target is then 100-35=65 degrees. We
	// rotate ccw to 65 degrees.
	// 2. Initial angle measurement is -45 and we ask for 360. New
	// target is 315. We rotate cw all the way around to 315.
	//
	// Everything depends on the functions turnRight/Left_Arcade(...).
	// The braking algorithms will determine the accuracy.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double turnAbsolute(double degrees) {

		// Rotation Direction Flags
		boolean ccw = false;
		boolean cw = false;

		double target = 0.0;
		double angle = 0.0; // instantaneous angle measurement
		double result = 0.0;

		if (degrees < 0.0) { // we will rotate ccw
			ccw = true;
			cw = false;
		} else { // we rotate cw
			cw = true;
			ccw = false;
		}

		degrees = Math.abs(degrees);
		if (cw == true) {
			result = turnRight_Arcade(degrees);
		} else if (ccw == true) {
			result = turnLeft_Arcade(degrees);
		}

		return (result);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double turn2Heading(double heading)
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Turns the robot to a compass heading (0->360).
	//
	// Arguments:Accepts a double representing the target heading
	//
	// Returns: The Acheived heading or a ridiculous value in the
	// event that a negative heading is entered.
	//
	// Remarks:
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double turn2Heading(double heading) {
		double angle;
		double delta;
		double change;
		double result;

		// Heading represents a "compass" reading, i.e.,
		// an angle from zero to something less than 360 degrees.
		// Negative arguments are not allowed. Because we want
		// to return the achieved heading we do not allow
		// submission of negative arguments and should return
		// a negative double so that the user can recognize
		// an error.
		if ((heading < 0.0) || (heading > 360.0)) {
			System.out.println("Submitted arguments must be greater than zero and less than 360");
			return (-999.9);
		}

		// This function will return an angle between 0 and 360 degrees.
		angle = getHeading();

		delta = heading - angle; // number of degrees to turn

		System.out.println("angle = " + angle + " delta = " + delta);

		// Reduce delta to the smallest necessary rotation
		// It should be something less than 180.0 or greater
		// than -180.0
		if (delta > 180.0) {
			delta -= 360.0;
		}
		if (delta < -180.0) {
			delta += 360.0;
		}

		System.out.println("delta = " + delta);

		// Depending on the sign of delta we turn left (-) or right (+)
		change = turnAbsolute(delta);
		System.out.println("delta = " + delta + " change = " + change);

		// Measure the angle, convert to compass indication. Note that
		// reading of the gyro after a small rotation could still be
		// outside the range of compass indications. We want to put
		// it in the range of the compass (0->360)
		result = getHeading();
		return (result);
	}

	/////////////////////////////////////////////////////////////////////
	// Function: public double getHeading()
	/////////////////////////////////////////////////////////////////////
	//
	// Purpose: Utility routine to determine starting compass indication
	// of the robot.
	//
	// Arguments:none
	//
	// Returns: The current heading of the robot, 0->360 degrees.
	//
	// Remarks: Called twice within turn2Heading.
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	public double getHeading() {
		int debug = 1;
		double angle;

		// A heading represents a "compass" reading, i.e.,
		// an angle from zero to something less than 360 degrees.

		// In the event that the current gyroscope indication is greater than
		// 360 degrees, reduce it to something within the compass range.
		// The same argument applies if the initial indication is less
		// than -360.0 degrees.
		angle = Robot.driveGyro.getAngle();
		while (angle > 360.0) {
			angle -= 360.0;
			if (debug == 1) {
				System.out.println("Compass heading = " + angle);
			}
		}

		// In the event that the current gyroscope indication is less
		// than -360.0, increase it to something within the compass range.
		while (angle < -360.0) {
			angle += 360.0;
			if (debug == 1) {
				System.out.println("Compass heading = " + angle);
			}
		}
		System.out.println("Compass heading = " + angle);

		return (angle);
	}

}

/////////////////////////////////////////////////////////////////////
// Function:
/////////////////////////////////////////////////////////////////////
//
// Purpose:
//
// Arguments:
//
// Returns:
//
// Remarks:
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
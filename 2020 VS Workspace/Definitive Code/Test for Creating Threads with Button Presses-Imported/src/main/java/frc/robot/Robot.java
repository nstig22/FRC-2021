/////////////////////////////////////////////////////////////////////
// File: Robot.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: The main file that links every other class and thread
// together, plus some other stuff like.
//
// Authors: Elliott DuCharme and Larry Basegio.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/25/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot {

  // Used for running auto code once.
  boolean autoOnce = true;

  // Toggle for if the drive joystick axes are inverted or not.
  boolean invertDriveToggle = false;

  // Doubles used for the joystick and analog trigger
  // values in the Mecanum drive deadband.
  double leftXAxisPS4, leftYAxisPS4, zAxisTriggers;

  // Used for the Mecanum drive deadband.
  final double PS4_MEC_DRIVE_DEADBAND = 0.2;

  // Creating the PS4 controller, with an ID of 0.
  Joystick PS4 = new Joystick(0);

  Constants constants;
  BallShooter ballShooter;
  ProximitySensor proximitySensor;
  BallShootThread ballShootThread;

  @Override
  public void robotInit() {

    constants = new Constants();
    proximitySensor = new ProximitySensor();
    ballShooter = new BallShooter();
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {

  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopPeriodic() {

    // If the circle button on the PS4 is pressed,
    // and the triangle button is NOT pressed...
    // if ((PS4.getRawButton(constants.PS4_CIRCLE_BUTTON) == true)
    // && (PS4.getRawButton(constants.PS4_TRIANGLE_BUTTON) == false)) {

    // // Shoot balls with front motors at 100% and back motors at 40%.
    // ballShootThread = new BallShootThread("threadName");

    // // Else if the circle button on the PS4 is pressed,
    // // and the triangle button IS pressed...
    // } else if ((PS4.getRawButton(constants.PS4_CIRCLE_BUTTON) == true)
    // && (PS4.getRawButton(constants.PS4_TRIANGLE_BUTTON) == true)) {

    // // Run the ball shooter motors backwards at 100% and back motors at 40%.

    // } else {

    // // Else, don't run the motors.

    // }

    if (PS4.getRawButton(constants.PS4_CIRCLE_BUTTON)) {
      ballShootThread = new BallShootThread("ballShootThread");

      try {
        ballShootThread.ballShootThread.join();
      } catch (InterruptedException e) {
        System.out.println("Main thread interrupted.");
      }

    }

  }

  @Override
  public void testPeriodic() {
  }

  /////////////////////////////////////////////////////////////////////
  // Function: getZAxisTriggers()
  /////////////////////////////////////////////////////////////////////
  //
  // Purpose: Gets the value for the "Z" axis using the 2 analog triggers.
  //
  // Arguments: none
  //
  // Returns: double zAxis: the value from -1 to 1, which is used for
  // controlling the speed and direction for strafing in teleop.
  //
  // Remarks: Created on 2/22/2020.
  //
  /////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////
  public double getZAxisTriggers() {

    // This variable is used for setting the Z axis for strafing.
    double zAxis;

    // These variables are for getting the values for the left and right
    // analog triggers, respectively.
    double leftAnalogTrigger;
    double rightAnalogTrigger;

    // Axes 2 and 3 are the left and right analog triggers, respectively.
    // You have to add 1 because the triggers start at -1 and go to 1.
    // Adding 1 makes them start at 0 when not being pressed.
    leftAnalogTrigger = PS4.getRawAxis(3) + 1;
    rightAnalogTrigger = PS4.getRawAxis(4) + 1;

    // Do the math for getting the value for strafing.
    // Example 1: if the driver presses the right one down, that value will be 1 - 0
    // = 100% speed (1).
    // Example 2: if the driver presses the left one down, that value will be 0 - 1
    // ; -100% speed (-1).
    zAxis = rightAnalogTrigger - leftAnalogTrigger;

    // Return the value, to be used elsewhere.
    return zAxis;
  }

}
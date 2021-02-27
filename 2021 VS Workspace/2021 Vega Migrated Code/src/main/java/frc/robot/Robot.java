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
  final double PS4_MEC_DRIVE_DEADBAND = 0.1;

  // Creating the PS4 controller, with an ID of 0.
  Joystick PS4 = new Joystick(0);

  Constants constants;
  BallShooter ballShooter;
  RobotDrive robotDrive;
  DriveThread driveThread;
  WormDrive wormDrive;
  BallIntake ballIntake;
  // DriveThread driveThread = new DriveThread("driveThread");

  @Override
  public void robotInit() {

    constants = new Constants();
    ballShooter = new BallShooter();
    robotDrive = new RobotDrive();
    // driveThread = new DriveThread("name");
    wormDrive = new WormDrive();
    ballIntake = new BallIntake();
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {

    // Put SmartDashboard stuff here, so the SD can get that stuff and the program
    // can use that in autoPeriodic()...
  }

  @Override
  public void autonomousPeriodic() {

    // if (autoOnce == true) {
    // robotDrive.driveBwd(1);
    // robotDrive.strafeRightAuto(1);
    // robotDrive.strafeLeftAuto(2);
    // autoOnce = false;
    // }
  }

  @Override
  public void teleopPeriodic() {

    // TODO .join() stuff!

    wormDrive.adjustShooterAngleManual();

    // Getting the values of these to be used for Mecanum drive stuff.
    leftXAxisPS4 = PS4.getX();
    leftYAxisPS4 = PS4.getY();
    zAxisTriggers = getZAxisTriggers();

    // If the driver presses the Touchpad button on the PS4,
    // change the variable to its opposite state, thus either
    // inverting the drive or un-inverting it.
    // TODO develop new inversion
    if (PS4.getRawButton(constants.PS4_TOUCHPAD)) {
      invertDriveToggle = !invertDriveToggle;
      Timer.delay(0.1);
    }

    /*
     * Long if statement that acts as a deadband for the drive. Basically, if the
     * absolute value of X, Y, OR Z axis values are greater than 0.2, run the
     * Mecanum drive. Else, don't run the Mecanum drive stuff. The arguments for
     * this function don't match up with the actual joystick axes for some reason.
     * Depending on the robot, you might have to experiment with these. Z = Right
     * joystick X axis (changed to the analog triggers using getZAxisTriggers()); Y
     * = Left joystick Y axis; X = left joystick X axis. In this case, ySpeed is the
     * strafing stuff, xSpeed is for driving forward/backward, and zRotation is for
     * turning left/right.
     */

    // If either axis is being pressed, run the drive in 1 of 2 ways,
    // depending on the toggle.

    //  if (((Math.abs(leftXAxisPS4) > PS4_MEC_DRIVE_DEADBAND) ||
    //   (Math.abs(leftYAxisPS4) > PS4_MEC_DRIVE_DEADBAND) || (Math.abs(zAxisTriggers)
    //   > PS4_MEC_DRIVE_DEADBAND))) {


    // If the invert drive toggle is false, drive normally.
    if (invertDriveToggle == false) {
      robotDrive.mecanumDrive.driveCartesian(-leftYAxisPS4, getZAxisTriggers(), leftXAxisPS4);
    }

    // If the toggle is true, the same function but the signs are different.
    else if (invertDriveToggle == true) {
      robotDrive.mecanumDrive.driveCartesian(leftYAxisPS4, -getZAxisTriggers(), -leftXAxisPS4);
    }
  // } 

  // If the X button on the PS4 is pressed,
  // and the Square button is NOT pressed...
  if((PS4.getRawButton(constants.PS4_X_BUTTON)==true)&&(PS4.getRawButton(constants.PS4_SQUARE_BUTTON)==false)){

  // Shoot balls with front motors at 100% and back motors at 40%.
  ballShooter.ballShoot(1,0.2);

  // Else if the X button on the PS4 is pressed,
  // and the square button IS pressed...
  /*} else if((PS4.getRawButton(constants.PS4_X_BUTTON)==true)&&(PS4.getRawButton(constants.PS4_SQUARE_BUTTON)==true)){

  // Run the ball shooter motors backwards at 100% and back motors at 40%.
  ballShooter.ballShoot(-1,-0.2);
*/
  } else if(PS4.getRawButton(constants.PS4_SQUARE_BUTTON) == true) {
    ballShooter.ballShoot(0, 0.1);
  } else {

  // Else, don't run the motors.
  ballShooter.ballShoot(0,0);}

  // If the driver pushes the right bumper button on the PS4 Controller,
  // run the intake motors forward (inwards).
  if(PS4.getRawButton(constants.PS4_RIGHT_BUMPER)){

  ballIntake.intakeBalls(-0.25,-0.5);

  // Else if the driver pushes the left bumper button on the PS4 Controller,
  // run the intake motors backwards (outward).
  }else if(PS4.getRawButton(constants.PS4_LEFT_BUMPER)){

  ballIntake.intakeBalls(0.25,0.5);

  }else{

  // Else, set the motors to 0 (don't run them).
  ballIntake.intakeBalls(0,0);

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
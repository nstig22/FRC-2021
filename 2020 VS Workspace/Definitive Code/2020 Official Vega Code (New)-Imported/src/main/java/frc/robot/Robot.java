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

public class Robot extends TimedRobot {

  // Used for running auto code once.
  boolean autoOnce = true;

  // Creating the PS4 controller, with an ID of 0.
  Joystick PS4 = new Joystick(0);

  // Creating instances of these classes, so everything is all linked together.
  // When Robot is extended in other classes, all of this stuff can be accessed
  // in those files, too. Inheritance is amazing and works infinitely better than
  // doing something like Robot robot = new Robot(); That is terrible and nasty.
  BallIntake ballIntake = new BallIntake();
  BallIntakeThread ballIntakeThread = new BallIntakeThread("ballIntakeThread");
  BallShooter ballShooter = new BallShooter();
  BallShootThread ballShootThread = new BallShootThread("ballShootThread");
  Constants constants = new Constants();
  DriveThread driveThread = new DriveThread("driveThread");
  ProximitySensor proximitySensorClass = new ProximitySensor();
  RobotDrive robotDrive = new RobotDrive();
  WormDrive wormDrive = new WormDrive();
  WormDriveThread wormDriveThread = new WormDriveThread("wormDriveThread");

  @Override
  public void robotInit() {
  }

  @Override
  public void robotPeriodic() {

    try {

      ballIntakeThread.ballIntakeThread.join();
      ballShootThread.ballShootThread.join();
      driveThread.driveThread.join();
      wormDriveThread.wormDriveThread.join();

    } catch (InterruptedException e) {

      System.out.println("hi");

    }
  }

  @Override
  public void autonomousInit() {

    // Put SmartDashboard stuff here, so the SD can get that stuff and the program
    // can use that in autoPeriodic()...
  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testPeriodic() {
  }

}
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMSparkMax;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

// This is the first program of the year, mostly going to be used as a reference
// and also a no-magic solution to testing motors and controllers
// expect this program to always be updated with the latest IDs

public class Robot extends TimedRobot {

  //standard PS4 controller
  Joystick stick;
  //all falcon 500s
  TalonFX intake, backLeftShooter, backRightShooter, frontLeftShooter, frontRightShooter, wormScrew;

  //all spark MAXes and the NEOs they control
  PWMSparkMax frontLeftDrive, backLeftDrive, frontRightDrive, backRightDrive;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {

    stick = new Joystick(0);

    intake = new TalonFX(11);
    backLeftShooter = new TalonFX(7); //the "back" shooter motors are the belts that feed the actual two shooter wheels
    backRightShooter = new TalonFX(8);
    frontLeftShooter = new TalonFX(5); //the "front" shooter motors are the actual 4" diameter shooter wheels
    frontRightShooter = new TalonFX(6);
    wormScrew = new TalonFX(10); //raises and lowers the lift


    //THESE DRIVE MOTOR IDs ARE NOT ACCURATE, STILL NEED TO BE TESTED
    frontLeftDrive = new PWMSparkMax(1);
    frontRightDrive = new PWMSparkMax(2);
    backRightDrive = new PWMSparkMax(3);
    backLeftDrive = new PWMSparkMax(4);
    //THESE DRIVE MOTOR IDs ARE NOT ACCURATE, STILL NEED TO BE TESTED


    // this is the command to set to brake mode
    wormScrew.setNeutralMode(NeutralMode.Brake);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    // Here we run the intake using the right joystick
    // We will use the percentoutput mode of the TalonFX for our "no-magic" motor tests
    intake.set(ControlMode.PercentOutput, stick.getRawAxis(5) / 2);

    // Here we run the ball feeders into the shooter using the left joystick
    backLeftShooter.set(ControlMode.PercentOutput, -stick.getRawAxis(1) / 2);
    backRightShooter.set(ControlMode.PercentOutput, stick.getRawAxis(1) / 2);

    // here we run the actual shooter using the back right trigger
    frontLeftShooter.set(ControlMode.PercentOutput, stick.getRawAxis(4) + 1);
    frontRightShooter.set(ControlMode.PercentOutput, -stick.getRawAxis(4) - 1);

    // Basic if and else if for the worm screw, down(button5) and up(button6)
    if (stick.getRawButton(5) == true) {
      wormScrew.set(ControlMode.PercentOutput, 0.5);
    }
    if (stick.getRawButton(6) == true) {
      wormScrew.set(ControlMode.PercentOutput, -0.5);
    } else if (stick.getRawButton(5) == false && stick.getRawButton(6) == false) {
      wormScrew.set(ControlMode.PercentOutput, 0);
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }
}

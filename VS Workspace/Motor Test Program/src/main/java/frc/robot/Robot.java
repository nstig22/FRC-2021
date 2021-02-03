// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */

// This is used to test motors and also to test Github
public class Robot extends TimedRobot {

  Joystick stick;
  TalonFX intake, backLeftShooter, backRightShooter, frontLeftShooter, frontRightShooter, wormScrew;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    stick = new Joystick(0);
    intake = new TalonFX(11);
    backLeftShooter = new TalonFX(7);
    backRightShooter = new TalonFX(8);
    frontLeftShooter = new TalonFX(5);
    frontRightShooter = new TalonFX(6);
    wormScrew = new TalonFX(10);
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
    intake.set(ControlMode.PercentOutput, stick.getRawAxis(5) / 3);

    backLeftShooter.set(ControlMode.PercentOutput, -stick.getRawAxis(1) / 2);
    backRightShooter.set(ControlMode.PercentOutput, stick.getRawAxis(1) / 2);

    frontLeftShooter.set(ControlMode.PercentOutput, stick.getRawAxis(4) + 1);
    frontRightShooter.set(ControlMode.PercentOutput, -stick.getRawAxis(4) - 1);

    if (stick.getRawButton(5) == true) {
      wormScrew.set(ControlMode.PercentOutput, 0.5);
    } if (stick.getRawButton(6) == true) {
      wormScrew.set(ControlMode.PercentOutput, -0.5);
    } else if (stick.getRawButton(5) == false && stick.getRawButton(6) == false) {
      wormScrew.set(ControlMode.PercentOutput, 0);
    }
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

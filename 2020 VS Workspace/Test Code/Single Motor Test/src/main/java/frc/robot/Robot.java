/////////////////////////////////////////////////////////////////////
// File: Robot.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Program used for testing motor speeds.
//
// Authors: Elliott DuCharme of FRC Team #5914.
//
// Environment: Microsoft VSCode Java
//
// Remarks: Made for 2020 competition; probably won't be useful for
// future seasons.
//
// SD stands for SmartDashboard.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  // Magic numbers for motor ports.
  // final int MOTOR_775_PRO_PORT = 1;

  final int TEST_FALCON_PORT_1 = 3;
  final int TEST_FALCON_PORT_2 = 2;

  // Creating the 775 Pro Motor, and its controller.
  // WPI_TalonSRX motor775Pro = new WPI_TalonSRX(MOTOR_775_PRO_PORT);

  // Creating the Falcon 500.
  WPI_TalonFX testFalcon1 = new WPI_TalonFX(TEST_FALCON_PORT_1);
  WPI_TalonFX testFalcon2 = new WPI_TalonFX(TEST_FALCON_PORT_2);

  // Doubles used for controlling the motor speeds through SD.
  // Start as 0 so that they're not full blast right away.
  // double motor775ProSpeed = 0;
  double Falcon500Speed = 0;

  SpeedControllerGroup Falcon500Group = new SpeedControllerGroup(testFalcon1, testFalcon2);

  // SD stuff.
  // public static final String motor775ProSpeedChoice = "motor775ProSpeedChoice";
  // public SendableChooser<String> sendChooser775Pro = new SendableChooser<>();

  public static final String falconSpeedChoice = "falconSpeedChoice";
  public SendableChooser<String> sendChooserSpeedFalcon = new SendableChooser<>();

  @Override
  public void robotInit() {

    testFalcon2.setInverted(true);

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

    // Allows controlling individual motors through SD.
    // The values are a percantage from -100% to 100%.
    // motor775ProSpeed = SmartDashboard.getNumber(motor775ProSpeedChoice, 100) /
    // 100;
    // SmartDashboard.putNumber(motor775ProSpeedChoice, motor775ProSpeed * 100);

    Falcon500Speed = SmartDashboard.getNumber(falconSpeedChoice, 100) / 100;
    SmartDashboard.putNumber(falconSpeedChoice, Falcon500Speed * 100);

    // Sets the motor speeds to what the user inputs.
    // motor775Pro.set(motor775ProSpeed);

    Falcon500Group.set(Falcon500Speed);

  }

  @Override
  public void testPeriodic() {
  }
}
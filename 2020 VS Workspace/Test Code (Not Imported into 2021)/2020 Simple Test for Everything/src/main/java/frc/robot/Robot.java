/////////////////////////////////////////////////////////////////////
// File: Robot.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: This program is for testing miscellaneous stuff on the
// 2020 Robot. This code is terrible and definitely a perfect
// example of spaghetti code.
//
// Authors: Elliott DuCharme.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: 
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

public class Robot extends TimedRobot {

  // Creating the drive motors.
  CANSparkMax frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

  WPI_TalonFX climbFalcon;

  WPI_TalonFX fLShooter, bLshooter, frshooter, brshooter;

  WPI_TalonFX lworm;
  // CANSparkMax rworm, lworm;

  WPI_TalonFX intake;

  // Creating the Mecanum Drive.
  MecanumDrive mecanumDrive;

  // Creating the PS4 controller.
  Joystick PS4;

  SpeedControllerGroup wormDrive;

  // Doubles used for the joystick and analog trigger
  // values in the Mecanum drive deadband.
  double leftXAxisPS4, leftYAxisPS4, zAxisTriggers;

  // Used for the Mecanum drive deadband.
  final double PS4_MEC_DRIVE_DEADBAND = 0.2;

  // Toggle for if the drive joystick axes are inverted or not.
  boolean invertDriveToggle = false;

  // final double PS4_TRIGGER_DEADBAND_POSITIVE = 0.2;
  // final double PS4_TRIGGER_DEADBAND_NEGATIVE = -0.2;

  ADXRS450_Gyro driveGyro = new ADXRS450_Gyro();

  ProximitySensor proximitySensorClass = new ProximitySensor();

  @Override
  public void robotInit() {

    frontLeftMotor = new CANSparkMax(4, MotorType.kBrushless);
    frontRightMotor = new CANSparkMax(2, MotorType.kBrushless);
    backLeftMotor = new CANSparkMax(3, MotorType.kBrushless);
    backRightMotor = new CANSparkMax(1, MotorType.kBrushless);

    climbFalcon = new WPI_TalonFX(12);

    fLShooter = new WPI_TalonFX(5);
    bLshooter = new WPI_TalonFX(7);
    frshooter = new WPI_TalonFX(6);
    brshooter = new WPI_TalonFX(8);

    // rworm = new CANSparkMax(9, MotorType.kBrushless);
    // lworm = new CANSparkMax(10, MotorType.kBrushless);
    lworm = new WPI_TalonFX(10);

    intake = new WPI_TalonFX(11);

    // backLeftMotor.setInverted(false);
    // frontLeftMotor.setInverted(false);

    // backRightMotor.setInverted(true);
    // frontRightMotor.setInverted(true);

    // Adding the drive motors to the Mecanum Drive.
    mecanumDrive = new MecanumDrive(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);

    // Assigning the PS4 controller the ID of 0.
    PS4 = new Joystick(0);

    // Putting the drive motors in brake mode.
    frontLeftMotor.setIdleMode(IdleMode.kBrake);
    frontRightMotor.setIdleMode(IdleMode.kBrake);
    backLeftMotor.setIdleMode(IdleMode.kBrake);
    backRightMotor.setIdleMode(IdleMode.kBrake);

    fLShooter.setNeutralMode(NeutralMode.Brake);
    bLshooter.setNeutralMode(NeutralMode.Brake);
    frshooter.setNeutralMode(NeutralMode.Brake);
    brshooter.setNeutralMode(NeutralMode.Brake);

    // rworm.setIdleMode(IdleMode.kBrake);
    // lworm.setIdleMode(IdleMode.kBrake);
    lworm.setNeutralMode(NeutralMode.Brake);

    // Might be necessary, might not be...? *Shrug*
    lworm.setInverted(true);
    // rworm.setInverted(false);

    // wormDrive = new SpeedControllerGroup(rworm, lworm);

    climbFalcon.setNeutralMode(NeutralMode.Brake);

    // backRightMotor.setInverted(true);
    // frontRightMotor.setInverted(true);

    mecanumDrive.setSafetyEnabled(false);

  }

  @Override
  public void robotPeriodic() {

    // Has to be called constantly, for some reason...?
    NetworkTable networkTable = NetworkTableInstance.getDefault().getTable("limelight");
    networkTable.getEntry("camMode").setValue(0);
    networkTable.getEntry("ledMode").setValue(3);
    networkTable.getEntry("stream").setValue(0);

    // System.out.println("prox: " + proximitySensorClass.getDistance());

    // System.out.println("fl shooter velocity: " +
    // fLShooter.getSelectedSensorVelocity());
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopPeriodic() {

    if (PS4.getRawButton(3) == true) {
      fLShooter.set(1);
      frshooter.set(-1);
      Timer.delay(1);
      brshooter.set(-0.1);
      bLshooter.set(0.1);
    } else {
      fLShooter.set(0);
      frshooter.set(0);
      brshooter.set(0);
      bLshooter.set(0);
    }

    climbFalcon.set(-PS4.getRawAxis(5));

    if (PS4.getRawButton(1)) {
      intake.set(-0.25);
    } else if (PS4.getRawButton(2)) {
      intake.set(0.25);
    } else {
      intake.set(0.0);
    }

    if (PS4.getRawButton(5)) { // left bumper
      // wormDrive.set(0.4); // down
      lworm.set(-0.5);
      // rworm.set(0.15);
    } else if (PS4.getRawButton(6)) { // right bumper
      // wormDrive.set(-0.4); // up
      lworm.set(0.5);
      // rworm.set(-0.15);
    } else {
      // wormDrive.set(0);
      lworm.set(0);
    }

    // Getting the values of these to be used for Mecanum drive stuff.
    double leftXAxisPS4 = PS4.getX();
    double leftYAxisPS4 = PS4.getY();
    double zAxisTriggers = getZAxisTriggers();

    // If the driver presses the Triangle button on the PS4,
    // change the variable to its opposite state, thus either
    // inverting the drive or un-inverting it.
    if (PS4.getRawButton(4)) {
      invertDriveToggle = !invertDriveToggle;
      Timer.delay(0.2);
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
    if (((Math.abs(leftXAxisPS4) > PS4_MEC_DRIVE_DEADBAND) || (Math.abs(leftYAxisPS4) > PS4_MEC_DRIVE_DEADBAND)
        || (Math.abs(zAxisTriggers) > PS4_MEC_DRIVE_DEADBAND))) {

      // If the invert drive toggle is false, drive normally.
      if (invertDriveToggle == false) {
        mecanumDrive.driveCartesian(getZAxisTriggers(), -leftYAxisPS4, leftXAxisPS4);
      }

      // If the toggle is true, the same function but the signs are different.
      else if (invertDriveToggle == true) {
        mecanumDrive.driveCartesian(-getZAxisTriggers(), leftYAxisPS4, -leftXAxisPS4);
      }

    } else {
      // Else, don't run the drive motors.
      mecanumDrive.driveCartesian(0, 0, 0);
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
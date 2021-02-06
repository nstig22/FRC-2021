
/////////////////////////////////////////////////////////////////////
//  File:  Robot.java
/////////////////////////////////////////////////////////////////////
//
//  Purpose:  Simple timed robot example to evaluate and debug a
//            child thread for driving and turning.  Intended
//            application is for the autonomous operations.
//
//  Revision: Initial development 12/30/2019
//
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  // Motor and encoder for tilt mechanism
  public static double tilt_position;
  public static CANSparkMax tilt_motor;
  public static CANEncoder tilt_enc;

  public static Encoder MagEncoder; // Encoder for the Redline motor

  // Motor Controllers and encoders for drive system
  public static CANSparkMax frontLeft, frontRight, backLeft, backRight;

  // public static WPI_TalonFX testFalcon500;

  // Encoders on two of the drive motors.
  public static CANEncoder left_enc, right_enc;

  public static SpeedControllerGroup leftDrive;
  public static SpeedControllerGroup rightDrive;
  public static SpeedControllerGroup allDrive;

  public static DifferentialDrive diff_drive;

  // Gyroscopes for drive and tilt.
  public static ADXRS450_Gyro driveGyro;
  public static ADXRS450_Gyro tiltGyro;

  // Ports for the two gyros.
  // private static final SPI.Port DRIVE_GYRO_PORT = SPI.Port.kOnboardCS0;
  // private static final SPI.Port TILT_GYRO_PORT = SPI.Port.kOnboardCS1;

  // Here is the class that reads all sensors
  public static Sensors sensor_status;

  // Drive thread parameters
  public static DriveThread auto_drive;
  public static boolean drive_thread_active;
  public static ProximitySensor proximitySensor;
  public static double drive_distance;
  boolean auto_once = true;

  // Home grown delay class
  public static Delay delay;

  // Counter to reduce printout of sensor readings.
  private int sensor_output_count = 0;
  private final int SENSOR_INTERVAL = 10;

  // Creating the PS4 Controller.
  Joystick PS4;

  // Joystick axes.
  double PS4LeftXAxis, PS4LeftYAxis, PS4RightXAxis, PS4RightYAxis, PS4LeftAnalogTrigger, PS4RightAnalogTrigger;

  // Magic numbers for PS4 controller axes.
  final int LEFT_X_AXIS_PORT = 0;
  final int LEFT_Y_AXIS_PORT = 1;
  final int RIGHT_X_AXIS_PORT = 2;
  final int RIGHT_Y_AXIS_PORT = 5;

  @Override
  public void robotInit() {

    proximitySensor = new ProximitySensor();

    drive_thread_active = false;

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    sensor_status = new Sensors();

    // MagEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
    // MagEncoder.reset();

    // tilt_motor = new CANSparkMax(5, MotorType.kBrushless);
    // tilt_enc = new CANEncoder(tilt_motor);

    // drive
    frontLeft = new CANSparkMax(3, MotorType.kBrushless);
    backLeft = new CANSparkMax(1, MotorType.kBrushless);
    frontRight = new CANSparkMax(2, MotorType.kBrushless);
    backRight = new CANSparkMax(4, MotorType.kBrushless);
    left_enc = new CANEncoder(frontLeft);
    right_enc = new CANEncoder(frontRight);

    frontLeft.setIdleMode(IdleMode.kBrake);
    backLeft.setIdleMode(IdleMode.kBrake);
    frontRight.setIdleMode(IdleMode.kBrake);
    backRight.setIdleMode(IdleMode.kBrake);

    frontLeft.setMotorType(MotorType.kBrushless);
    backLeft.setMotorType(MotorType.kBrushless);
    frontRight.setMotorType(MotorType.kBrushless);
    backRight.setMotorType(MotorType.kBrushless);

    // Making the motors all turn the right way
    frontLeft.setInverted(false);
    frontRight.setInverted(false);
    backLeft.setInverted(false);
    backRight.setInverted(false);

    leftDrive = new SpeedControllerGroup(frontLeft, backLeft);
    rightDrive = new SpeedControllerGroup(frontRight, backRight);
    allDrive = new SpeedControllerGroup(frontLeft, frontRight, backLeft, backRight);

    diff_drive = new DifferentialDrive(leftDrive, rightDrive);

    // drive system gyro
    driveGyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
    tiltGyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS1);

    // Initialize the gyros, calibrate, and reset to zero degrees.
    driveGyro.calibrate();
    driveGyro.reset();
    tiltGyro.calibrate();
    tiltGyro.reset();

    delay = new Delay();

    diff_drive.setSafetyEnabled(false);

    PS4 = new Joystick(1);

    // This somehow resets the Talon drive encoder...?
    left_enc.setPosition(0);

  }

  @Override
  public void robotPeriodic() {

    // System.out.println("enc: " + Sensors.drive_position);

    // Reading the values of the 4 analog stick positions.
    PS4LeftXAxis = PS4.getRawAxis(LEFT_X_AXIS_PORT);
    PS4LeftYAxis = PS4.getRawAxis(LEFT_Y_AXIS_PORT);
    PS4RightXAxis = PS4.getRawAxis(RIGHT_X_AXIS_PORT);
    PS4RightYAxis = PS4.getRawAxis(RIGHT_Y_AXIS_PORT);

    // read the sensors
    // sensor_status.readSensors();

    sensor_output_count++;

    // Read and print out the sensor values per interval. According
    // to the documentation for the timed robot, this function will
    // be called every 20msec. We can adjust the frequency of the
    // sensor readings via the final variable SENSOR_INTERVAL.

    if (sensor_output_count == SENSOR_INTERVAL) {
      // read the sensors
      // sensor_status.readSensors();

      // System.out.println("drive_angle" + sensor_status.drive_angle);
      // System.out.println("drive_position" + sensor_status.drive_position);
      // System.out.println("inner lift position" +
      // sensor_status.inner_lift_position);
      // System.out.println("tilt_position" + sensor_status.tilt_position);

      // reset counter
      sensor_output_count = 0;
    }

    SmartDashboard.putNumber("left_enc", sensor_status.drive_position);
    sensor_status.drive_position = SmartDashboard.getNumber("left_enc", sensor_status.drive_position);
  }

  @Override
  public void autonomousInit() {

  }

  @Override
  public void autonomousPeriodic() {
    // Need to prevent unintentional duplication of the drive thread
    // as this block during autonomous is called every 20msec. We
    // limit the creation of the thread to one time. It is
    // intended that a single thread handles all of the
    // autonomous operations. That said, we could create additional
    // threads for other functions inside this block.
    if (auto_once == true) {

      auto_drive = new DriveThread("autonomous operations");

      auto_once = false;
    }

    // if (drive_thread_active == false) {
    // System.out.println("ultrasonic = " + proximitySensor.getDistance());

    // }

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    // System.out.println("ultrasonic = " + proximitySensor.getDistance());

    System.out.println("Drive Gyro: \t" + driveGyro.getAngle() + "\t" + "Tilt Gyro: \t" + tiltGyro.getAngle());

    // Allow joystick actions within this block if the drive thread
    // is not active.
    if (drive_thread_active == false) {

      // Normal drive.
      // diff_drive.arcadeDrive(-PS4LeftYAxis, PS4LeftXAxis, true);

      // diff_drive.curvatureDrive(-PS4LeftYAxis, PS4LeftXAxis, true);

      // testFalcon500.set(-PS4RightYAxis);

      // System.out.print("vel: \t" + testFalcon500.getSelectedSensorVelocity() +
      // "\t");

      // System.out.println("Falcon enc: \t" +
      // testFalcon500.getSelectedSensorPosition());
      // tilt_motor.set(-PS4RightYAxis);

    }
  }

  // Use testPeriodic() for resetting encoders and stuff.
  @Override
  public void testPeriodic() {

    // This somehow resets the Talon drive encoder...?
    left_enc.setPosition(0);

  }
}

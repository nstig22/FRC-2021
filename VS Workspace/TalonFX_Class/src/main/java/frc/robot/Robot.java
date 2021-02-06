// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private int init=1;

  static TalFX_Shooter lf_shoot;
  static TalFX_Shooter rf_shoot;
  static ShootThread shoot;
  static double velocity;

  static boolean shoot_thread_active = false;

  //  You need to set the CAN ID's here for the TalonFX motors
  //  I am setting them to 1 and 2 respectively, you will need
  //  to change them for your specific application.
  int talonfx_can_id_1=1;
  int talonfx_can_id_2=2;


   // This function is run when the robot is first started up and should be used for any
   // initialization code.
   
  @Override
  public void robotInit() {

    double velocity;

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    //  Create the two objects with appropriate inversion state
    lf_shoot = new TalFX_Shooter(talonfx_can_id_1,false);  //  non-inverted
    rf_shoot = new TalFX_Shooter(talonfx_can_id_2,true);   //  inverted

    //  Setup shoot parameters
    lf_shoot.setWheelDiameter(4.0);
    rf_shoot.setWheelDiameter(4.0);

    System.out.println("Wheel Diameter = " + lf_shoot.getWheelDiameter() + "inches");

    lf_shoot.setTargetVelocity(60.0); 
    rf_shoot.setTargetVelocity(60.0); 

    velocity=lf_shoot.getTargetVelocity();

    System.out.println("Target Velocity = " + velocity + "ft/sec");

    //  compute motor velocity target for the desired ball velocity
    lf_shoot.target_speed=lf_shoot.computeMotorSpeed(velocity);
    rf_shoot.target_speed=rf_shoot.computeMotorSpeed(velocity);

    System.out.println("target speed = " + lf_shoot.target_speed + "counts/100msec");
    System.out.println("target speed = " + (-1*rf_shoot.target_speed) + "counts/100msec");  //  inverted
  
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }


    //  We call the thread once.  I envision that this thread could be expanded
    //  to fire all four motors.
    if(init==1)  {

      //  Will need to think about this a bit more.  How do we combine all four
      //  motors in a single thread?  My best guess at this point would be to
      //  create a separate class for a multiple motor setup that is created and
      //  initialized in autonomousInit().  What I don't know is what happends when
      //  you create classes that access the same hardware.
        shoot=new ShootThread("shoot thread");
        init=0;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {    

    //  Telop should work OK with multiple motors and these class functions
    lf_shoot.setVelocity();
    rf_shoot.setVelocity();
   

}

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}

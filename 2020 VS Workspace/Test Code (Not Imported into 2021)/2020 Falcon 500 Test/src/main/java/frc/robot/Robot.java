package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {

  WPI_TalonFX testMotor;

  @Override
  public void robotInit() {

    // Dummy ID value.
    testMotor = new WPI_TalonFX(1);

    testMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1);
    testMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

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

    testMotor.set(1);

    System.out.println("testMotor = " + testMotor.getSelectedSensorPosition());

  }

  @Override
  public void testPeriodic() {
  }
}
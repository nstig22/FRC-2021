package frc.robot;

import edu.wpi.first.hal.sim.DriverStationSim;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  private static final String Switch = "Switch";
  private static final String Scale = "Scale";
  private String targetChoice;
  private SendableChooser<String> Target = new SendableChooser<>();

  static String gameData;

  static String InitGameData;

  @Override
  public void robotInit() {

    Target.setDefaultOption("Switch", Switch);
    Target.addOption("Scale", Scale);
    SmartDashboard.putData("Target choice", Target);

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

  @Override
  public void autonomousInit() {

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    InitGameData = getGameData();

  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public String getGameData() {

    gameData = DriverStation.getInstance().getGameSpecificMessage();

    // Possibly trims down String data?
    gameData = gameData.substring(0, 2);

    SmartDashboard.putString("Colors", gameData);

    return (gameData);
  }

}
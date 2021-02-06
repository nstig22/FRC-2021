package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  // SmartDashboard stuff.
  // Strings for where our autonomous starting position is.
  public final String leftPosition = "Left Position";
  public final String middlePosition = "Middle Position";
  public final String rightPosition = "Right Position";

  // String used for Autonomous.java Switch statements.
  // Stores the position the drives chooses.
  // Gets assigned later in autonomousInit().
  public String positionChoice;

  // The SmartDashboard list that allows the driver to pick our starting position.
  // Akin to a drop-down list you'd find in Windows or something.
  public SendableChooser<String> positionSendableChooser = new SendableChooser<>();

  // The various goal choices that the driver can pick
  public final String highGoal = "High Goal";
  public final String lowGoal = "Low Goal";

  // Stores the goal the driver chooses
  // Gets assigned later in autonomousInit()
  public String goalChoice;

  // The SmartDashboard list that allows the driver
  // to pick our goal to shoot balls into (high or low)
  // Akin to a drop-down list you'd find in Windows or something
  public SendableChooser<String> goalSendableChooser = new SendableChooser<>();

  // Choices for if our robot is going before our other 2 alliance partners, after
  // the first and before the third, or if we're going last
  public final String goingFirst = "Going First";
  public final String goingSecond = "Going Second";
  public final String goingLast = "Going Last";

  // Stores the choice for if going first, second, or third
  // Gets assigned later in autonomousInit()
  public String orderChoice;

  // The SmartDashboard list that allows the driver to pick when our robot is
  // going to drive over to the goal and shoot
  // Akin to a drop-down list you'd find in Windows or something
  public SendableChooser<String> orderSendableChooser = new SendableChooser<>();

  // SmartDashboard thing that allows the driver to specify how far back the robot
  // will be from the tower when attempting to fire power cells in Auto in inches.
  // Default value is 120 inches.
  public double minTowerDistance = 120;

  public SendableChooser<String> minDistSendableChooser = new SendableChooser<>();

  // Calling the Thread classes in Robot.java
  // Creating an instance of the BallIntakeThread.
  BallIntakeThread ballIntakeThread;

  // Creating an instance of BallShootThread.
  BallShootThread ballShootThread;

  // Creating an instance of the ClimbThread.
  ClimbThread climbThread;

  // Creating an instance of DriveThread
  DriveThread driveThread;

  // Creating an instance of WormDriveThread.
  WormDriveThread wormDriveThread;

  // Creating an instance of the Autonomous class.
  Autonomous autonomous = new Autonomous();

  // Create instance of the Sensors class.
  Sensors sensors = new Sensors();

  // Creating an instance of the Variables class.
  Variables variables = new Variables();

  // Variables used for ComputeTrajectory.java.
  // Distances expressed in inches; velocity in ft/sec.
  // Velocity and these other values might need to be adjusted.
  static double v = 25.0; // Ball velocity.
  static double x0 = 0; // Initial x value.
  static double y0 = 24; // Launch location (initial y value).
  static double y = 98.25; // Target of y (height of inner goal of the tower (in inches)).

  // Creating an instance of the ComputeTrajectory class,
  // and passing in the required arguments.
  ComputeTrajectory computeTrajectory = new ComputeTrajectory(x0, sensors.proximitySensorDistance, y0, y, v);

  // String for the color for Position Control, used in Teleop.
  String rotationControlColor;

  // vision crap
  NetworkTable table;
  double[] areas;
  double[] defaultValue = new double[0];

  @Override
  public void robotInit() {

    // Vision idk.
    table = NetworkTableInstance.getDefault().getTable("GRIP/mycontoursReport");

    // SmartDashboard stuff.
    // Adding these objects to SmartDashboard; Middle is the default option.
    // Adding choices for our starting position.
    positionSendableChooser.addOption("Left", leftPosition);
    positionSendableChooser.setDefaultOption("Middle", middlePosition);
    positionSendableChooser.addOption("Right", rightPosition);

    // Adding choices for the goal we're going for.
    // High goal is obviously the default.
    goalSendableChooser.setDefaultOption("High", highGoal);
    goalSendableChooser.addOption("Low", lowGoal);

    // Adding choices for if we're going first, second, or last.
    // Going last is the default option.
    orderSendableChooser.addOption("Going First", goingFirst);
    orderSendableChooser.addOption("Going Second", goingSecond);
    orderSendableChooser.setDefaultOption("Going Last", goingLast);

    // Allows the driver to specify how far back the robot will be from the tower
    // when attempting to fire power cells in Auto.
    SmartDashboard.putData("Min Distance from Tower", minDistSendableChooser);

    // Calling these Threads once in robotInit() should help prevent them from
    // being called a gazillion times in autoPeriodic/teleopPeriodic().

    // Calling the BallShootThread, and telling it to get ready to/start running.
    ballIntakeThread = new BallIntakeThread("BallIntakeThread");

    // Calling the BallShootThread, and telling it to get ready to/start running.
    ballShootThread = new BallShootThread("BallShootThread");

    // Calling the ClimbThread, and telling it to get ready to/start running.
    climbThread = new ClimbThread("ClimbThread");

    // Calling the DriveThread, and telling it to get ready to/start running.
    driveThread = new DriveThread("DriveThread");

    // Calling the WormDriveThread, and telling it to get ready to/start running.
    wormDriveThread = new WormDriveThread("WormDriveThread");
  }

  @Override
  public void robotPeriodic() {

    // Calling the readSensors() function.
    // It is constantly being called in robotPeriodic(), and thus all of the sensors
    // in the Sensors class are constantly being read.
    // This helps reduce redundancy, because the same sensors don't need to be read
    // multiple times, the variable they're stored in can just be accessed whenever
    // necessary.
    sensors.readSensors();
  }

  @Override
  public void autonomousInit() {

    // Assigning these variables to whatever the driver picked in SmartDashboard.
    // They're used in autonomousPeriodic() to allow the robot to decide what to do.
    positionChoice = positionSendableChooser.getSelected();
    goalChoice = goalSendableChooser.getSelected();
    orderChoice = orderSendableChooser.getSelected();

    // Getting and sending the SmartDashboard values.
    minTowerDistance = SmartDashboard.getNumber("Min Distance from Tower", minTowerDistance);
    SmartDashboard.putNumber("Min Distance from Tower", minTowerDistance);
  }

  @Override
  public void autonomousPeriodic() {

    // Calling the function in Autonomous.java that runs all of our Auto code.
    // This helps to keep Robot.java smaller, neater, and more organized.
    autonomous.autoFunctions();
  }

  @Override
  public void teleopPeriodic() {

    // Get the color for the Position Control thing.
    // This is in teleopInit() because it should only need to be run once.
    rotationControlColor = DriverStation.getInstance().getGameSpecificMessage();

    // Sends this value to the SmartDashboard to be viewed by the driver.
    SmartDashboard.putString("Rotation Control Color", rotationControlColor);
  }

  @Override
  public void testPeriodic() {

    // Run this in test to try vision stuff. Finally using testPeriodic() for once.
    // An enhanced for loop, designed for iterating through array indexes.
    for (double areaArrayIndex : areas) {
      System.out.print(areaArrayIndex + " ");
    }

    System.out.println();

  }

}
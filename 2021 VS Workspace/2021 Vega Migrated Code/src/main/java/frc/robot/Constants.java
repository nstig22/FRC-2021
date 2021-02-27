/////////////////////////////////////////////////////////////////////
// File: Constants.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Houses constants that don't belong elsewhere.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/25/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

class Constants extends Robot {

    // Magic numbers for RobotDrive Motor IDs.
    final int FRONT_LEFT_DRIVE_MOTOR_ID = 1;
    final int FRONT_RIGHT_DRIVE_MOTOR_ID = 2;
    final int BACK_LEFT_DRIVE_MOTOR_ID = 3;
    final int BACK_RIGHT_DRIVE_MOTOR_ID = 4;

    // Magic numbers for BallShooter Motor IDs.
    final int FRONT_LEFT_SHOOTER_MOTOR_ID = 5;
    final int FRONT_RIGHT_SHOOTER_MOTOR_ID = 6;
    final int BACK_LEFT_SHOOTER_MOTOR_ID = 7;
    final int BACK_RIGHT_SHOOTER_MOTOR_ID = 8;

    // Magic numbers for the ID for the WormDrive motors.
    final int WORM_DRIVE_MOTOR_ID = 10;

    // Magic number for the ID for the ball intake Falcon 500.
    final int BALL_INTAKE_MOTOR_ID = 11;

    // Magic number for the ID for the climb Falcon 500.
    final int CLIMB_MOTOR_ID = 12;

    // Magic numbers for PS4 Controller Axes IDs.
    final int PS4_L_X_AXIS_ID = 0;
    final int PS4_L_Y_AXIS_ID = 1;
    final int PS4_R_X_AXIS_ID = 2;
    final int PS4_R_Y_AXIS_ID = 3;
    final int PS4_L_ANALOG_TRIG_ID = 4;
    final int PS4_R_ANALOG_TRIG_ID = 5;

    // Magic numbers used for the PS4 Controller button ID's.
    final int PS4_SQUARE_BUTTON = 1;
    final int PS4_X_BUTTON = 2;
    final int PS4_CIRCLE_BUTTON = 3;
    final int PS4_TRIANGLE_BUTTON = 4;
    final int PS4_LEFT_BUMPER = 5;
    final int PS4_RIGHT_BUMPER = 6;
    // ID's 7 and 8 are not included because they're
    // "button" ID's for the Analog Triggers, which is
    // what the Driver Station sees for some reason.
    final int PS4_SHARE_BUTTON = 9;
    final int PS4_OPTIONS_BUTTON = 10;
    final int PS4_LEFT_STICK_IN = 11;
    final int PS4_RIGHT_STICK_IN = 12;
    final int PS4_PS4_LOGO_BUTTON = 13;
    final int PS4_TOUCHPAD = 14;
}
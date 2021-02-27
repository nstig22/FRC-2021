/////////////////////////////////////////////////////////////////////
// File: RobotDrive.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Used for housing drive-related stuff for the robot.
// Also houses the autonomous drive functions.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created on 2/25/2020.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.MecanumDrive;

// This class extends the Robot class so it can access stuff in there.
class RobotDrive extends Robot {

    // Creating the drive motors.
    // CANSparkMax frontLeftDriveMotor = new
    // CANSparkMax(constants.FRONT_LEFT_DRIVE_MOTOR_ID, MotorType.kBrushless);
    // CANSparkMax frontRightDriveMotor = new
    // CANSparkMax(constants.FRONT_RIGHT_DRIVE_MOTOR_ID, MotorType.kBrushless);
    // CANSparkMax backLeftDriveMotor = new
    // CANSparkMax(constants.BACK_LEFT_DRIVE_MOTOR_ID, MotorType.kBrushless);
    // CANSparkMax backRightDriveMotor = new
    // CANSparkMax(constants.BACK_RIGHT_DRIVE_MOTOR_ID, MotorType.kBrushless);

    CANSparkMax frontLeftDriveMotor = new CANSparkMax(1, MotorType.kBrushless);
    CANSparkMax frontRightDriveMotor = new CANSparkMax(2, MotorType.kBrushless);
    CANSparkMax backLeftDriveMotor = new CANSparkMax(3, MotorType.kBrushless);
    CANSparkMax backRightDriveMotor = new CANSparkMax(4, MotorType.kBrushless);

    // Creating an object of the MecanumDrive class.
    // This links the 4 drive motors together.
    MecanumDrive mecanumDrive = new MecanumDrive(frontLeftDriveMotor, backLeftDriveMotor, frontRightDriveMotor,
            backRightDriveMotor);

    // Encoder for the front left drive motor.
    // Used for the auto functions.
    CANEncoder frontLeftDriveEnc = frontLeftDriveMotor.getEncoder(EncoderType.kHallSensor, 42);

    // Gyro for auto drive functions.
    ADXRS450_Gyro driveGyro = new ADXRS450_Gyro();

    // Magic numbers for driveFwd() and driveBwd().
    final double DRIVE_FWD_AND_BWD_SPEED = 0.5; // How fast the drive motors spin in driveFwd() and driveBwd().
    final double ENCODER_CALIBRATION_DRIVEFWDBWD = 1.43; // Encoder calibration for the driveFwd/Bwd auto functions.

    // Magic numbers for strafeLeftAuto() and strafeRightAuto().
    final double STRAFE_SPEED = 0.5; // Magic number for the speed for how fast the robot strafes.
    final double ENCODER_CALIBRATION_STRAFE = 1.15; // Encoder calibration for the strafeLeftAuto/Right auto functions.

    // Magic numbers for turnLeft() and turnRight().
    final double ROTATION_SPEED = 0.5; // Speed value for turnLeft() and turnRight().

    // Constructor.
    RobotDrive() {

        // Removes pointless, annoying warnings/errors from the RioLog.
        mecanumDrive.setSafetyEnabled(false);

        // Putting the drive motors in coast mode.
        // frontLeftDriveMotor.setIdleMode(IdleMode.kCoast);
        // frontRightDriveMotor.setIdleMode(IdleMode.kCoast);
        // backLeftDriveMotor.setIdleMode(IdleMode.kCoast);
        // backRightDriveMotor.setIdleMode(IdleMode.kCoast);

        // Putting the drive motors in brake mode.
        frontLeftDriveMotor.setIdleMode(IdleMode.kBrake);
        frontRightDriveMotor.setIdleMode(IdleMode.kBrake);
        backLeftDriveMotor.setIdleMode(IdleMode.kBrake);
        backRightDriveMotor.setIdleMode(IdleMode.kBrake);

        // Resetting the encoder.
        frontLeftDriveEnc.setPosition(0);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: strafeLeft(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for strafing left, without encoder magic.
    //
    // Arguments: double strafeSpeed
    //
    // Returns: void
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void strafeLeft(double strafeSpeed) {

        // Strafe left at the inputted speed.
        frontLeftDriveMotor.set(-strafeSpeed);
        backLeftDriveMotor.set(-strafeSpeed);
        frontRightDriveMotor.set(strafeSpeed);
        backRightDriveMotor.set(strafeSpeed);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: strafeRight(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for strafing right, without encoder magic.
    //
    // Arguments: double strafeSpeed
    //
    // Returns: void
    //
    // Remarks: Created on 3/07/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void strafeRight(double strafeSpeed) {

        // Strafe right at the inputted speed.
        frontLeftDriveMotor.set(strafeSpeed);
        backLeftDriveMotor.set(strafeSpeed);
        frontRightDriveMotor.set(-strafeSpeed);
        backRightDriveMotor.set(-strafeSpeed);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: strafeLeftAuto(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for strafing left in autonomous.
    //
    // Arguments: double feet.
    // This value is later converted to inches, to get inches per count.
    //
    // Returns: void
    //
    // Remarks: Created on 2/22/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void strafeLeftAuto(double feet) {

        // Initialize the encoder to 0 (reset it).
        frontLeftDriveEnc.setPosition(0);

        // Convert feet to inches.
        double inches = feet * 12.0;

        // Our current encoder count reading.
        double currentCounts = 0;

        // This should give us how many counts.
        double maxEncoderCounts = inches / ENCODER_CALIBRATION_STRAFE;

        while (currentCounts < maxEncoderCounts) {

            // Strafe left.
            frontLeftDriveMotor.set(-STRAFE_SPEED);
            backLeftDriveMotor.set(-STRAFE_SPEED);
            frontRightDriveMotor.set(STRAFE_SPEED);
            backRightDriveMotor.set(STRAFE_SPEED);

            // Delay for 20 ms.
            Timer.delay(0.02);

            // Read the encoder, and get our current counts.
            currentCounts = Math.abs(frontLeftDriveEnc.getPosition());
        }

        // Stop the motors.
        frontLeftDriveMotor.set(0);
        backLeftDriveMotor.set(0);
        frontRightDriveMotor.set(0);
        backRightDriveMotor.set(0);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: strafeRightAuto(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for strafing right in autonomous.
    //
    // Arguments: double feet.
    // This value is later converted to inches, to get inches per count.
    //
    // Returns: void
    //
    // Remarks: Created on 2/22/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void strafeRightAuto(double feet) {

        // Initialize the encoder to 0 (reset it).
        frontLeftDriveEnc.setPosition(0);

        // Convert feet to inches.
        double inches = feet * 12.0;

        // Our current encoder count reading.
        double currentCounts = 0;

        // This should give us how many counts.
        double maxEncoderCounts = inches / ENCODER_CALIBRATION_STRAFE;

        while (currentCounts < maxEncoderCounts) {

            // Strafe right.
            frontLeftDriveMotor.set(STRAFE_SPEED);
            backLeftDriveMotor.set(STRAFE_SPEED);
            frontRightDriveMotor.set(-STRAFE_SPEED);
            backRightDriveMotor.set(-STRAFE_SPEED);

            // Delay for 20 ms.
            Timer.delay(0.02);

            // Read the encoder, and get our current counts.
            currentCounts = Math.abs(frontLeftDriveEnc.getPosition());
        }

        // Stop the motors.
        frontLeftDriveMotor.set(0);
        backLeftDriveMotor.set(0);
        frontRightDriveMotor.set(0);
        backRightDriveMotor.set(0);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: driveFwd(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for driving forward in autonomous.
    //
    // Arguments: double feet.
    // This value is later converted to inches, to get inches per count.
    //
    // Returns: void
    //
    // Remarks: Created on 2/22/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void driveFwd(double feet) {

        // Initialize the encoder to 0 (reset it).
        frontLeftDriveEnc.setPosition(0);

        // Convert feet to inches.
        double inches = feet * 12.0;

        // Our current encoder count reading.
        double maxEncoderCounts = 0;

        // This should give us how many counts.
        double encoderCounts = inches / ENCODER_CALIBRATION_DRIVEFWDBWD;

        while (maxEncoderCounts < encoderCounts) {

            // Drive forward.
            frontLeftDriveMotor.set(DRIVE_FWD_AND_BWD_SPEED);
            backLeftDriveMotor.set(-DRIVE_FWD_AND_BWD_SPEED);
            frontRightDriveMotor.set(DRIVE_FWD_AND_BWD_SPEED);
            backRightDriveMotor.set(-DRIVE_FWD_AND_BWD_SPEED);

            // Delay for 20 ms.
            Timer.delay(0.02);

            // Read the encoder, and get our current counts.
            maxEncoderCounts = Math.abs(frontLeftDriveEnc.getPosition());
        }

        // Stop the motors.
        frontLeftDriveMotor.set(0);
        backLeftDriveMotor.set(0);
        frontRightDriveMotor.set(0);
        backRightDriveMotor.set(0);

    }

    /////////////////////////////////////////////////////////////////////
    // Function: driveBwd(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for driving backward in autonomous.
    //
    // Arguments: double feet
    // This value is later converted to inches, to get inches per count.
    //
    // Returns: void
    //
    // Remarks: Created on 2/22/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void driveBwd(double feet) {

        // Initialize the encoder to 0 (reset it).
        frontLeftDriveEnc.setPosition(0);

        // Convert feet to inches.
        double inches = feet * 12.0;

        // Our current encoder count reading.
        double currentCounts = 0;

        // This should give us how many counts.
        double maxEncoderCounts = inches / ENCODER_CALIBRATION_DRIVEFWDBWD;

        while (currentCounts < maxEncoderCounts) {

            // Drive forward.
            frontLeftDriveMotor.set(-DRIVE_FWD_AND_BWD_SPEED);
            backLeftDriveMotor.set(DRIVE_FWD_AND_BWD_SPEED);
            frontRightDriveMotor.set(-DRIVE_FWD_AND_BWD_SPEED);
            backRightDriveMotor.set(DRIVE_FWD_AND_BWD_SPEED);

            // Delay for 20 ms.
            Timer.delay(0.02);

            // Read the encoder, and get our current counts.
            currentCounts = Math.abs(frontLeftDriveEnc.getPosition());
        }
        // Stop the motors.
        frontLeftDriveMotor.set(0);
        backLeftDriveMotor.set(0);
        frontRightDriveMotor.set(0);
        backRightDriveMotor.set(0);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: turnLeft(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for turning left in autonomous.
    //
    // Arguments: double targetAngle: the angle we are trying to achieve.
    //
    // Returns: void
    //
    // Remarks: Created on 2/24/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void turnLeft(double targetAngle) {

        driveGyro.reset(); // Reset the gyro.

        targetAngle *= -1.0; // Invert the target angle.

        double currentAngle = driveGyro.getAngle(); // Initial gyro angle.
        double rotationSpeed = ROTATION_SPEED; // Start our rotation speed at this magic number.

        // While our current angle is less than our target angle, rotate.
        while (currentAngle > targetAngle) {

            // Rotate counterclockwise.
            frontLeftDriveMotor.set(-rotationSpeed);
            backLeftDriveMotor.set(-rotationSpeed);
            frontRightDriveMotor.set(-rotationSpeed);
            backRightDriveMotor.set(-rotationSpeed);

            // Get our current angle, and stop when we get there.
            currentAngle = driveGyro.getAngle();

            // If our current angle minus our target angle is less than 10 degrees,
            // reduce our speed.
            if (Math.abs(currentAngle - targetAngle) < 10.0) {
                rotationSpeed = 0.1;
            }
        }

        // Stop the motors, because we're at our target.
        frontLeftDriveMotor.set(0);
        backLeftDriveMotor.set(0);
        frontRightDriveMotor.set(0);
        backRightDriveMotor.set(0);
    }

    /////////////////////////////////////////////////////////////////////
    // Function: turnRight(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for turning right in autonomous.
    //
    // Arguments: double targetAngle: the angle we are trying to achieve.
    //
    // Returns: void
    //
    // Remarks: Created on 2/24/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void turnRight(double targetAngle) {

        driveGyro.reset(); // Reset the gyro.

        double currentAngle = driveGyro.getAngle(); // Initial gyro angle.
        double rotationSpeed = ROTATION_SPEED; // Start our rotation speed at this magic number.

        // While our current angle is greater than our target angle, rotate.
        while (currentAngle < targetAngle) {

            // Rotate clockwise.
            frontLeftDriveMotor.set(rotationSpeed);
            backLeftDriveMotor.set(rotationSpeed);
            frontRightDriveMotor.set(rotationSpeed);
            backRightDriveMotor.set(rotationSpeed);

            // Get our current angle, and stop when we get there.
            currentAngle = driveGyro.getAngle();

            // If our current angle minus our target angle is less than 10 degrees,
            // reduce our speed.
            if (Math.abs(currentAngle - targetAngle) < 10.0) {
                rotationSpeed = 0.1;
            }
        }

        // Stop the motors, because we're at our target.
        frontLeftDriveMotor.set(0);
        backLeftDriveMotor.set(0);
        frontRightDriveMotor.set(0);
        backRightDriveMotor.set(0);
    }

}
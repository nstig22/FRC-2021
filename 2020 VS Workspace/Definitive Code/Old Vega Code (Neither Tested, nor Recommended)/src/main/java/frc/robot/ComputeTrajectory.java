/////////////////////////////////////////////////////
// File: computeTrajectory.java
/////////////////////////////////////////////////////
// 
// Date created: 1/18/2020
// 
// Purpose: Class file for Trajectory.java that 
// houses all calculations.
// 
// Authors: Noah S, Elliott D, Larry B
// 
// Environment: Microsoft VSCode Java
// 
// Remarks: Added into 2020 Official Vega Code on
// 2/08/2020 at 8:25 PMish.
// 
/////////////////////////////////////////////////////
/////////////////////////////////////////////////////

package frc.robot;

import edu.wpi.first.wpilibj.Timer;

class ComputeTrajectory {

    // Create an instance of the Sensors.java class in here.
    // Used for determining trajectory.
    Sensors sensors = new Sensors();

    // Creating an instance of the WormDriveThread in this file.
    WormDriveThread wormDriveThread = new WormDriveThread("WormDriveThread");

    // Creating an instance of the BallShootThread in this file.
    BallShootThread ballShootThread = new BallShootThread("BallShootThread");

    // Distances expressed in inches; velocity in ft/sec.
    double v; // velocity
    double x0; // initial x value
    double x; // target x value
    double y0; // launch location
    double y; // target y value
    double theta; // angle of firing
    final double GRAVITY_ACCEL = 9.81; // Acceleration due to gravity in m/s/s.

    // Magic number for the height of the inner goal in inches.
    final double INNER_GOAL_HEIGHT = 98.25;

    /////////////////////////////////////////////////////
    // Function: computeTrajectory(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Constructor
    //
    // Arguments: Arguments are named similarly to above,
    // but have suffix _val to keep them separate.
    //
    // Returns: void
    //
    // Remarks:
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    ComputeTrajectory(double x0_val, double x_val, double y0_val, double y_val, double v_val) {
        x0 = x0_val;
        x = x_val;
        y0 = y0_val;
        y = y_val;
        v = v_val;

        // Converting these values from inches to meters for easier math.
        x0 = convert_inches2meters(x0);
        x = convert_inches2meters(x);
        y0 = convert_inches2meters(y0);
        y = convert_inches2meters(y);
        v = convert_feet2meters(v);

    }

    /////////////////////////////////////////////////////
    // Function: compute_tofx(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Calculates the tof for the x axis.
    //
    // Arguments: double x0, double x, double v,
    // double theta
    //
    // Returns: double tofx
    //
    // Remarks: tofx = time of flight x
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    double compute_tofx(double x0, double x, double v, double theta) {

        double tofx;
        double vx; // x velocity
        double theta_rad;

        theta_rad = (Math.PI / 180.0) * theta;
        vx = v * Math.cos(theta_rad);

        tofx = (x - x0) / vx;

        return tofx;

    }

    /////////////////////////////////////////////////////
    // Function: compute_tofy_plus(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Calculates the tof for the y axis.
    //
    // Arguments: double y0, double y, double v,
    // double theta
    //
    // Returns: double tofy
    //
    // Remarks: tofy = time of flight y
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    double compute_tofy_plus(double y0, double y, double v, double theta) {

        double tofy;
        double vy; // y velocity
        double theta_rad;
        double temp;

        theta_rad = (Math.PI / 180.0) * theta; // Convert to radians
        vy = v * Math.sin(theta_rad);

        temp = (vy * vy) - ((2 * GRAVITY_ACCEL) * (y - y0));

        if (temp < 0.0) {

            // System.out.println("No solution available");
            return (-999.999);
        }
        tofy = (vy + Math.sqrt(temp)) / GRAVITY_ACCEL;

        return tofy;
    }

    /////////////////////////////////////////////////////
    // Function: compute_tofy_minus(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Calculates the tof for the y axis.
    //
    // Arguments: double y0, double y, double v, double theta
    //
    // Returns: double tofy
    //
    // Remarks: tofy = time of flight y
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    double compute_tofy_minus(double y0, double y, double v, double theta) {

        double tofy;
        double vy;
        double theta_rad;
        double temp;

        theta_rad = (Math.PI / 180.0) * theta;
        vy = v * Math.sin(theta_rad);

        temp = (vy * vy) - (2 * GRAVITY_ACCEL) * (y - y0);

        if (temp < 0.0) {

            // System.out.println("No solution available");
            return (-999.999);
        }
        tofy = (vy - Math.sqrt(temp)) / GRAVITY_ACCEL;

        return tofy;
    }

    /////////////////////////////////////////////////////
    // Function: compute_theta_plus(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Computes the necessary angle.
    //
    // Arguments: double y0, double y, double x0, double x
    //
    // Returns: double theta_soln
    //
    // Remarks:
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    double compute_theta_plus(double y0, double y, double x0, double x) {

        double theta_min;
        double theta;
        double theta_soln = 0;
        double delta;
        double min_delta = 1000;
        double tofx;
        double tofy;

        theta_min = Math.atan((y - y0) / (x - x0));
        theta_min = (180.0 / Math.PI) * theta_min;

        for (theta = theta_min; theta < 90.0; theta += 0.01) {
            tofx = compute_tofx(x0, x, v, theta);
            tofy = compute_tofy_plus(y0, y, v, theta);

            delta = Math.abs(tofy - tofx);

            if (delta < min_delta) {

                min_delta = delta;
                theta_soln = theta;
            }

        }
        return theta_soln;
    }

    /////////////////////////////////////////////////////
    // Function: compute_theta_minus(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Computes the necessary angle.
    //
    // Arguments: double y0, double y, double x0, double x
    //
    // Returns: double theta_soln
    //
    // Remarks: soln = solution
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    double compute_theta_minus(double y0, double y, double x0, double x) {

        double theta_min;
        double theta;
        double theta_soln = 0;
        double delta;
        double min_delta = 1000;
        double tofx;
        double tofy;

        theta_min = Math.atan((y - y0) / (x - x0));
        theta_min = (180.0 / Math.PI) * theta_min;

        for (theta = theta_min; theta < 90.0; theta += 0.01) {
            tofx = compute_tofx(x0, x, v, theta);
            tofy = compute_tofy_minus(y0, y, v, theta);

            delta = Math.abs(tofy - tofx);

            if (delta < min_delta) {

                min_delta = delta;
                theta_soln = theta;
            }

        }
        return theta_soln;

    }

    /////////////////////////////////////////////////////
    // Function: convert_inches2meters(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Converts the argument in inches to meters.
    //
    // Arguments: double inches.
    //
    // Returns: double meters.
    //
    // Remarks:
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    static double convert_inches2meters(double inches) {

        double meters;

        meters = ((inches * 2.54) / 100);
        return meters;
    }

    /////////////////////////////////////////////////////
    // Function: convert_feet2meters(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Converts feet to meters.
    //
    // Arguments: double feet
    //
    // Returns: double meters
    //
    // Remarks: This function has a variable inches.
    // It first converts feet to inches, then uses the
    // previous function to convert feet to inches.
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    static double convert_feet2meters(double feet) {

        double inches;
        double meters;

        inches = 12 * feet;

        meters = convert_inches2meters(inches);

        return meters;
    }

    /////////////////////////////////////////////////////////////////////
    // Function: adjustAngleOfShooter()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Used for adjusting the angle at which the ball shooter is
    // firing. Uses sensor readings and some fancy physics to adjust it to
    // the right angle to shoot it into the inner hole. After it adjusts,
    // it calls on the BallShootThread to fire the ball.
    //
    // Arguments: none
    //
    // Returns: void
    //
    // Remarks: Created on 2/08/2020 at 7:28 PM.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public void adjustAngleOfShooter() {

        // Initial x and y values.
        double x0 = 0.0;
        double y0 = 0.0;

        // Get the distance from the robot to the tower (the x value).
        double xProxSensorDistance = sensors.proximitySensorDistance;

        // Get the initial angle of the ball shooter.
        double initTheta = sensors.wormDriveGyroAngle; // Initial angle.

        // Compute the theta needed for the right trajectory.
        // Passing in the values so it can do the math.
        // Returns a double representing the angle we need to adjust to; store it in a
        // double "targetFiringTheta".
        double targetFiringTheta = compute_theta_plus(y0, y, x0, xProxSensorDistance);

        // If our initial angle is greater than OR less than our intended angle,
        // move it to there.
        // Else if it's already there, do nothing.
        if (initTheta > targetFiringTheta) {

            while (sensors.wormDriveGyroAngle > targetFiringTheta) {

                wormDriveThread.rightWormDriveMotor.set(-wormDriveThread.WORM_DRIVE_MOTORS_SPEED);

                // Once we get there, stop the motors and break out of the while loop.
                if (sensors.wormDriveGyroAngle == targetFiringTheta) {
                    wormDriveThread.rightWormDriveMotor.set(0);
                    break; // Break out of the while loop.
                }

            }

            // If our initial angle is less than our intended target angle,
            // move it to that angle.
        } else if (initTheta < targetFiringTheta) {

            while (sensors.wormDriveGyroAngle < targetFiringTheta) {

                wormDriveThread.rightWormDriveMotor.set(wormDriveThread.WORM_DRIVE_MOTORS_SPEED);

                // Once we get there, stop the motors and break out of the while loop.
                if (sensors.wormDriveGyroAngle == targetFiringTheta) {
                    wormDriveThread.rightWormDriveMotor.set(0);
                    break; // Break out of the while loop.
                }

            }

            // If we're already at our target angle, do nothing.
        } else if (sensors.wormDriveGyroAngle == targetFiringTheta) {
            wormDriveThread.rightWormDriveMotor.set(0);
            // Do nothing.
        }

        // Delay so the robot has adequate time to adjust the arm.
        Timer.delay(0.75);

        // Fire the balls using the ballShoot function.
        ballShootThread.ballShoot(ballShootThread.FRONT_SHOOTER_MOTORS_SPEED,
                ballShootThread.BACK_SHOOTER_MOTORS_SPEED);

        // Move Ball Shooter back to initial position.
        while (sensors.wormDriveGyroAngle > initTheta) {
            wormDriveThread.rightWormDriveMotor.set(-wormDriveThread.WORM_DRIVE_MOTORS_SPEED);
        }

        // End of adjustAngleOfShooter().

    }
}
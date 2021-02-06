/////////////////////////////////////////////////////
// File: Trajectory.java
/////////////////////////////////////////////////////
// 
// Date created: 2/1/2021
// 
// Purpose: Class file for Trajectory.java that 
// houses all calculations.
// 
// Authors: 
// 
// Environment: Microsoft VSCode Java
// 
// Remarks: 
// 
/////////////////////////////////////////////////////
/////////////////////////////////////////////////////

package frc.robot;

//import edu.wpi.first.wpilibj.Timer;

class Trajectory {

     // Distances expressed in inches; velocity in ft/sec.
    double v; // velocity
    double x0; // initial x value
    double x; // target x value
    double y0; // launch location
    double y; // target y value
    double theta; // angle of firing
    final double GRAVITY_ACCEL = 32.0; // Acceleration due to gravity in ft/s/s.

    // Magic number for the height of the inner goal in inches.
    final double INNER_GOAL_HEIGHT = 98.25;

    /////////////////////////////////////////////////////
    // Function: computeTrajectory(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Constructor
    //
    // Arguments: Arguments are named similarly to above,
    // all values in inches.
    //
    // Returns: void
    //
    // Remarks:
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    Trajectory(double x0_val, double x_val, double y0_val, double y_val, double v_val) {
        x0 = x0_val;
        x = x_val;
        y0 = y0_val;
        y = y_val;
        v = v_val;    
    }

    /////////////////////////////////////////////////////
    // Function: compute_tofx(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Calculates the tof (in seconds) for the x axis.
    //
    // Arguments: double x0, double x, double v,
    // double theta in degrees.
    // Distamces in inches. velocity in ft/sec.
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

        //  convert inches to feet
        x0/=12.0;
        x/=12.0;

        tofx = (x - x0) / vx;

        return (tofx);

    }

    /////////////////////////////////////////////////////
    // Function: compute_tofy_plus(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Calculates the tof for the y axis.
    //
    // Arguments: double x0, double x, double v,
    // double theta in degrees.
    // Distamces in inches. velocity in ft/sec.  
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

        //  convert inches to feet
        y0/=12.0;
        y/=12.0;

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
    // Arguments: double y0, double y, double v, double theta,
    //            y0,y in inches, v in ft/sec, theta in degrees.
    //
    // Returns: double tofy in seconds
    //
    // Remarks: tofy = time of flight y
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    double compute_tofy_minus(double y0, double y, double v, double theta) {

        double tofy;
        double vy; // y velocity
        double theta_rad;
        double temp;

        theta_rad = (Math.PI / 180.0) * theta; // Convert to radians
        vy = v * Math.sin(theta_rad);

        //  convert inches to feet
        y0/=12.0;
        y/=12.0;

        temp = (vy * vy) - ((2 * GRAVITY_ACCEL) * (y - y0));

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
    //           all dimensions in inches.
    //
    // Returns: double theta_soln in degrees.
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

            //  convert inches to feet
            x0/=12.0;
            x/=12.0;
            y0/=12.0;
            y/=12.0;

        //  Compute line of sight angle.  Target angle
        //  will be larger.
        theta_min = Math.atan((y - y0) / (x - x0));
        theta_min = (180.0 / Math.PI) * theta_min;

        //  Compute optimum angle for long side of the 
        //  trajectory.
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
    //           all dimensions in inches.
    //
    // Returns: double theta_soln in degrees
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

            //  convert inches to feet
            x0/=12.0;
            x/=12.0;
            y0/=12.0;
            y/=12.0;

        //  Compute line of sight angle.  Target angle
        //  will be larger.
        theta_min = Math.atan((y - y0) / (x - x0));
        theta_min = (180.0 / Math.PI) * theta_min;

        //  Compute optimum angle for long side of the 
        //  trajectory.
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
    static double convert_inches2feet(double inches) {

        double feet;

        feet = inches/12.0;
        return feet;
    }

    

    /*
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
        double xProxSensorDistance = Robot.sensors.proximitySensorDistance;

        // Get the initial angle of the ball shooter.
        double initTheta = Robot.sensors.wormDriveGyroAngle; // Initial angle.

        // Compute the theta needed for the right trajectory.
        // Passing in the values so it can do the math.
        // Returns a double representing the angle we need to adjust to; store it in a
        // double "targetFiringTheta".
        double targetFiringTheta = compute_theta_plus(y0, y, x0, xProxSensorDistance);

        // If our initial angle is greater than OR less than our intended angle,
        // move it to there.
        // Else if it's already there, do nothing.
        if (initTheta > targetFiringTheta) {

            while (Robot.sensors.wormDriveGyroAngle > targetFiringTheta) {

                Robot.wormdrive.wormDriveMotors.set(-Robot.wormdrive.WORM_DRIVE_MOTORS_SPEED);

                // Once we get there, stop the motors and break out of the while loop.
                if (Robot.sensors.wormDriveGyroAngle == targetFiringTheta) {
                    Robot.wormdrive.wormDriveMotors.set(0);
                    break; // Break out of the while loop.
                }

            }

            // If our initial angle is less than our intended target angle,
            // move it to that angle.
        } else if (initTheta < targetFiringTheta) {

            while (Robot.sensors.wormDriveGyroAngle < targetFiringTheta) {

                Robot.wormdrive.wormDriveMotors.set(Robot.wormdrive.WORM_DRIVE_MOTORS_SPEED);

                // Once we get there, stop the motors and break out of the while loop.
                if (Robot.sensors.wormDriveGyroAngle == targetFiringTheta) {
                    Robot.wormdrive.wormDriveMotors.set(0);
                    break; // Break out of the while loop.
                }

            }

            // If we're already at our target angle, do nothing.
        } else if (Robot.sensors.wormDriveGyroAngle == targetFiringTheta) {
            Robot.wormdrive.wormDriveMotors.set(0);
            // Do nothing.
        }

        // Delay so the robot has adequate time to adjust the arm.
        Timer.delay(0.75);

        // Fire the balls using the ballShoot function.
        Robot.shooter.ballShoot(Robot.shooter.FRONT_SHOOTER_MOTORS_SPEED,
        Robot.shooter.BACK_SHOOTER_MOTORS_SPEED);

        // Move Ball Shooter back to initial position.
        while (Robot.sensors.wormDriveGyroAngle > initTheta) {
            Robot.wormdrive.wormDriveMotors.set(-Robot.wormdrive.WORM_DRIVE_MOTORS_SPEED);
        }

        // End of adjustAngleOfShooter().

    }
    */
}

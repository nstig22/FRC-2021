
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
// Remarks:
/////////////////////////////////////////////////////
/////////////////////////////////////////////////////
import java.io.*;
import java.math.*;

class computeTrajectory {

    double v; // velocity
    double x0; // ititial x value
    double x; // target x value
    double y0; // launch location
    double y; // target y value
    double theta; // angle of firing
    final double g = 9.81; // acceleration due to gravity m/s/s

    /////////////////////////////////////////////////////
    // Function: computeTrajectory(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Constructor
    //
    // Arguments: Arguments are named similarly to above,
    // but have suffix _val to keep them seperate.
    //
    // Returns: void
    //
    // Remarks:
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    computeTrajectory(double x0_val, double x_val, double y0_val, double y_val, double v_val) {
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

        temp = (vy * vy) - ((2 * g) * (y - y0));

        if (temp < 0.0) {

            // System.out.println("No solution available");
            return (-999.999);
        }
        tofy = (vy + Math.sqrt(temp)) / g;

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

        temp = (vy * vy) - (2 * g) * (y - y0);

        if (temp < 0.0) {

            // System.out.println("No solution available");
            return (-999.999);
        }
        tofy = (vy - Math.sqrt(temp)) / g;

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

    /////////////////////////////////////////////////////////////////////
    // Function: computeLeadScrewPosition(...)
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Computes the position of the screw for the ball shooter.
    //
    // Arguments: double lowerPivot, double height, double upperPivot,
    // double angleDeg
    //
    // Returns: screwPosition: the position of the ball shooter screw.
    //
    // Remarks: Created on 2/24/2020.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    public double computeLeadScrewPosition(double lowerPivot, double height, double upperPivot, double angleDeg) {

        // Position of the ball shooter screw.
        double screwPosition;

        // The computed sides of a right angle triangle,
        // where the hypotenuse is the lead screw position.
        double side1, side2;

        // Used for storing the angleDeg argument, converted to Radians.
        double angleRad = Math.toRadians(angleDeg);

        // Doing the math for calculating the screw position.
        side1 = lowerPivot - upperPivot * Math.cos(angleRad);
        side2 = upperPivot * Math.sin(angleRad);

        // Using the Pythagorean Theorem for getting the screw position.
        screwPosition = Math.sqrt((side1 * side1) + (side2 * side2));

        // Return the final screw position value.
        return screwPosition;
    }
}
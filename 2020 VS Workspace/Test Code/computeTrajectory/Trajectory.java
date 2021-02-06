
/////////////////////////////////////////////////////
// File: Trajectory.java
/////////////////////////////////////////////////////
// 
// Date created: 1/18/2020
// 
// Purpose: To automatically calculate the angle of  
// the ball launcher needed to score a ball in the
// top goal.
// 
// Authors: Noah S, Elliott D, Larry B
// 
// Environment: Microsoft VSCode Java
// 
// Remarks:
// 
/////////////////////////////////////////////////////
/////////////////////////////////////////////////////
import java.io.*;
import java.math.*;

class Trajectory {

    static double v; // velocity
    static double x0; // ititial x value
    static double x; // target x value
    static double y0; // launch location
    static double y; // target y value

    public static void main(String args[]) {

        double theta;

        // distances expressed in inches; velocity in ft/sec
        v = 25.0;
        x0 = 0;
        x = 96;
        y0 = 24;
        y = 98.25;

        // Link the 2 files together.
        computeTrajectory traj;

        // Converting these values from inches to meters for easier math.
        x0 = cnvrt_inches2meters(x0);
        x = cnvrt_inches2meters(x);
        y0 = cnvrt_inches2meters(y0);
        y = cnvrt_inches2meters(y);
        v = cnvrt_feet2meters(v);

        traj = new computeTrajectory(x0, x, y0, y, v);

        // Compute positive and negative solutions for theta.
        theta = traj.compute_theta_minus(y0, y, x0, x);
        System.out.println("Theta = " + theta);

        theta = traj.compute_theta_plus(y0, y, x0, x);
        System.out.println("Theta = " + theta);
    }

    /////////////////////////////////////////////////////
    // Function: cnvrt_inches2meters(...)
    /////////////////////////////////////////////////////
    //
    // Purpose: Converts the agrument in inches to meters.
    //
    // Arguments: double inches.
    //
    // Returns: double meters.
    //
    // Remarks:
    //
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    static double cnvrt_inches2meters(double inches) {

        double meters;

        meters = ((inches * 2.54) / 100);
        return meters;
    }

    /////////////////////////////////////////////////////
    // Function: cnvrt_feet2meters(...)
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
    static double cnvrt_feet2meters(double feet) {

        double inches;
        double meters;

        inches = 12 * feet;

        meters = cnvrt_inches2meters(inches);

        return meters;
    }

}
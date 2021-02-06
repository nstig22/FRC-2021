/////////////////////////////////////////////////////////////////////
// File: MetricConversion.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: This file is used for converting between metric and SAE
// units (US units like feet, inches, etc).
//
// Authors: Larry Basegio and Elliott DuCharme
//
// Environment: Microsoft VSCode Java.
//
// Remarks: 
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

class MetricConversion {

    // Magic numbers.
    // Used for converting between units.
    final double INCHES_2_MM = 25.4;
    final double FEET_2_MM = 25.4 * 12.0;

    // Used for storing the feet and inch values.
    // Equivalent to pointers in C.
    double feet[];
    double inches[];

    // Constructor.
    MetricConversion() {

        feet = new double[1];
        inches = new double[1];
    }

    /////////////////////////////////////////////////////////////////////
    // Function: convert_mm_2_SAE()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Converts millimeters (mm) to inches.
    //
    // Arguments: double mm, int feet[], double inches[]
    //
    // Returns: void.
    //
    // Remarks: Feet and inches are used for memory address pointer stuff,
    // because Java can't return multiple things like C can.
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    void convert_mm_2_SAE(double mm, int feet[], double inches[]) {
        double ftmp;

        ftmp = mm / FEET_2_MM;

        System.out.println("ftmp = " + ftmp);

        feet[0] = (int) ftmp;

        System.out.println("feet[0] = " + feet[0]);

        inches[0] = (ftmp - (double) feet[0]) * 12;

        System.out.println("inches[0] = " + inches[0]);

    }

    /////////////////////////////////////////////////////////////////////
    // Function: convert_SAE_2_mm()
    /////////////////////////////////////////////////////////////////////
    //
    // Purpose: Converts SAE to MM.
    //
    // Arguments: int feet, double inches
    //
    // Returns: ftmp.
    //
    // Remarks:
    //
    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    double convert_SAE_2_mm(int feet, double inches) {

        double ftmp;

        ftmp = (double) feet * 12.0 + inches;
        ftmp *= INCHES_2_MM;
        return (ftmp);

    }

}
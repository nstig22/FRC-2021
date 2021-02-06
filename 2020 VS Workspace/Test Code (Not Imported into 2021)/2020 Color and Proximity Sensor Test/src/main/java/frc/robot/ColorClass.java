/////////////////////////////////////////////////////////////////////
// File: ColorClass.java
/////////////////////////////////////////////////////////////////////
//
// Purpose: Uses the Rev Robotics color sensor to measure
// color, infrared, and proximity values.
//
// Authors: Elliott DuCharme and Larry Basegio.
//
// Environment: Microsoft VSCode Java.
//
// Remarks: Created 1/11/2020(ish).
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
package frc.robot;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

class ColorClass {

  // Magic numbers.
  // Minimum values for color measurement.
  // If below these, can't read color values properly.
  private final double MIN_IR_VALUE = 10;
  private final int MIN_PROX_VALUE = 50;

  private final I2C.Port i2cPort = I2C.Port.kOnboard;

  private ColorSensorV3 m_colorSensor;

  /**
   * A Rev Color Match object is used to register and detect known colors. This
   * can be calibrated ahead of time or during operation.
   * 
   * This object uses a simple euclidian distance to estimate the closest match
   * with given confidence range.
   */
  private ColorMatch m_colorMatcher;

  // These will be setup per RGB in the class constructor
  private final Color kBlueTarget;
  private final Color kGreenTarget;
  private final Color kRedTarget;
  private final Color kYellowTarget;

  // Constructor.
  ColorClass() {

    // Creating the color sensor.
    m_colorSensor = new ColorSensorV3(i2cPort);

    // Used for detecting colors.
    m_colorMatcher = new ColorMatch();

    // Defining colors.
    kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    m_colorMatcher.addColorMatch(kYellowTarget);
  }

  /////////////////////////////////////////////////////////////////////
  // Function: getColorString()
  /////////////////////////////////////////////////////////////////////
  //
  // Purpose: Gets the current color that the color sensor sees.
  //
  // Arguments: void.
  //
  // Returns: A string containing the color it sees (colorString).
  //
  // Remarks:
  //
  /////////////////////////////////////////////////////////////////////
  
  String getColorString() {
    Color detectedColor;
    ColorMatchResult match;
    String colorString;

    /**
     * The method GetColor() returns a normalized color value from the sensor and
     * can be useful if outputting the color to an RGB LED or similar. To read the
     * raw color, use GetRawColor().
     * 
     * The color sensor works best when within a few inches from an object in well
     * lit conditions (the built in LED is a big help here!). The farther an object
     * is the more light from the surroundings will bleed into the measurements and
     * make it difficult to accurately determine its color.
     */
    detectedColor = m_colorSensor.getColor();

    /**
     * Run the color match algorithm on our detected color
     */
    match = m_colorMatcher.matchClosestColor(detectedColor);

    if (match.color == kBlueTarget) {
      colorString = "Blue";
    } else if (match.color == kRedTarget) {
      colorString = "Red";
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
    } else {
      colorString = "Unknown";
    }

    if (ColorValidity() == false) {
      colorString = "Invalid";
    }

    return (colorString);

  }

  /////////////////////////////////////////////////////////////////////
  // Function: GetIRValue()
  /////////////////////////////////////////////////////////////////////
  //
  // Purpose: Gets the IR value.
  //
  // Arguments: void.
  //
  // Returns: double representing the IR reading.
  //
  // Remarks: Created on 1/11/2020.
  //
  /////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////
  double GetIRValue() {

    double IR_Reading;

    IR_Reading = m_colorSensor.getIR();

    return (IR_Reading);
  }

  /////////////////////////////////////////////////////////////////////
  // Function: getProximity()
  /////////////////////////////////////////////////////////////////////
  //
  // Purpose: Gets the proximity to the object.
  //
  // Arguments: void.
  //
  // Returns: A value proportional to the proximity
  // (the closer you are, the higher the value).
  //
  // Remarks: Saturates at 255 (max value it can return).
  // Experimentally, the minimum value is 40 or 50.
  //
  /////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////
  int GetProximity() {

    int proximity;

    proximity = m_colorSensor.getProximity();

    return (proximity);

  }

  /////////////////////////////////////////////////////////////////////
  // Function: ColorValidity()
  /////////////////////////////////////////////////////////////////////
  //
  // Purpose: Determines if we are close enough to get an accurate
  // color reading. Makes use of both IR and proximity features of
  // the sensor.
  //
  // Arguments: void.
  //
  // Returns: True or false, depending on if it's close enough to get
  // an accurate reading. True means that we can have a valid reading
  // (we're close enough). False means we're not close enough.
  //
  // Remarks: Initial testing indicated that the values MIN_IR_VALUE and
  // MIN_PROX_VALUE are sufficient.
  //
  /////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////
  boolean ColorValidity() {

    int proximity;
    double IR_Reading;

    IR_Reading = m_colorSensor.getIR();

    proximity = m_colorSensor.getProximity();

    if ((proximity > MIN_PROX_VALUE) && (IR_Reading > MIN_IR_VALUE)) {
      return (true);
    } else {
      return (false);
    }

  }

}
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/////////////////////////////////////////////////////////////////////
//  File: Robot.java
////////////////////////////////////////////////////////////////////
//
//  Purpose:  This relatively simple motor feedback example is 
//            intended to illustrate how one could related motor
//            velocity readings and proportional control to 
//            create a relatively consistent belt speed for the
//            ball shooter.
//
//  Compiler:  Microsoft VS for FIRST Robotics
//
//  Team:  5914 Caledonia Robotic Warriors
//
//  Revision History:
//
//         Initial development 1/29/2021
//
//         To Do:  
//         1.  All of this functionality should be combined
//             in a separate class.
//         2.  Reverse motor operation needs to be considered.
//         3.  Timing functions need to be added to measure
//             time required to converge.  This will aid in
//             determining best feedback parameters.
//
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;;

public class Robot extends TimedRobot {

  final double wheel_diameter=3.0;  //  drive wheel diameter in inches
  final double deadband=5.0;        //  deadband for moter percentage changes

  /* Hardware */
	TalonFX _talon = new TalonFX(1);

  Delay delay=new Delay();

  String str;

  //  Timing parame int  
  long start_time=0;
  long end_time=0;
  double elapsed_time=0.0;
  double previous_time=0.0;
  double loop_time=0.0;
    
  /* String for output */
  StringBuilder _sb = new StringBuilder();
    
    /* Loop tracker for prints */
  int _loops = 0;
  
  double target_velocity;     //  target velocity in meters/second
  double target_speed;        //  target motor speed in counts/100msec
  double motor_output=0.50;        //  percentage motor output (starting point)
  double output_increment;

  int init=1;

	public void robotInit() {

  
    //  set a target velocity for the ball
    target_velocity=10.0;  //  velocity in meters/sec
   
    /* Factory Default all hardware to prevent unexpected behaviour */
		_talon.configFactoryDefault();
		
    /* Config sensor used for Primary PID [Velocity] */
    //  Integrated Sensor (TalonFX has it), primary closed loop, 50 msec timeout
    _talon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,0, 50);
  		

    /* Config the peak and nominal outputs */
    // These functions are inherited from can.BaseMotorController

    //  configNominalOutputForward(double percentOutput,int timeoutMS)
    //  These calls indicate 0 output nominal
		_talon.configNominalOutputForward(0, 50);
    _talon.configNominalOutputReverse(0, 50);
    
    //  configPeakOutputForward(double percentOut,int timeoutMS)
    //  These calls indicate maximum output
    //  note sign reversal for reverse
		_talon.configPeakOutputForward(1,50);
		_talon.configPeakOutputReverse(-1,50);

        
		/*
		 * Talon FX does not need sensor phase set for its integrated sensor
		 * This is because it will always be correct if the selected feedback device is integrated sensor (default value)
		 * and the user calls getSelectedSensor* to get the sensor's position/velocity.
		 * 
		 * https://phoenix-documentation.readthedocs.io/en/latest/ch14_MCSensor.html#sensor-phase
		 */

     //  compute motor velocity target for the desired ball velocity
      target_speed=computeMotorSpeed(target_velocity);
      System.out.println("target speed = " + target_speed);

    
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
      
    //  Temporary variables, calculated within this function
    double speed;
    double raw_error;
    double error;
    double incr;

  
    //  Start the timer
    if(init==1)  {
        start_time=System.nanoTime();
        init=0;
    }

    /* Set Talon  output percentage at the starting point
       Future:  This could be read from a table to more quickly hit
       the target.  Table would relate velocity to starting
       point (percentage) for motor drive.
    */

    //  This starts the motor
    _talon.set(TalonFXControlMode.PercentOutput, motor_output);

    //  Need this delay to allow motor to start before attempting
    //  to read the encoder velocity
    delay.delay_milliseconds(5);

  	/* Prepare line to print */
		_sb.append("\tout:");
		/* Cast to int to remove decimal places */
		_sb.append((int) (motor_output * 100));
		_sb.append("%");	// Percent

    _sb.append("\tspd:");

    //  Read the selected sensor velocity in counts per 100msec
    //  "0" implies primary closed loop, "1" impies auxiliary closed loop
    //  Essentially we are reading the integrated TalonFX sensor.
    speed=_talon.getSelectedSensorVelocity(0);
      
		_sb.append(speed);
    _sb.append("u"); 	// Native units
    
    //  Compute the difference error between target
    //  and actual motor speed
    raw_error=target_speed-speed;
    error=Math.abs(raw_error);
    _sb.append("\traw error:");
    _sb.append(raw_error);
    _sb.append("u"); 	// Native units
    _sb.append("\terror:");
    _sb.append(error);
    _sb.append("u"); 	// Native units
   

    //  Here is our proportional error correction block
    //  These numbers determine many things:
    //  1.  Speed of correction (convergence).
    //  2.  Amount of "ringing"
    //  The goal is to get to our speed as quickly
    //  as possible with a minimum of "overshoot"
    if(error>1000.0)  {
      incr=0.01;
    } else if(error>500.0)  {
      incr=0.005;
    } else if(error>100.0)  {
      incr=0.001;
    }  else  {
      incr=0.0001;
    }

    //  If we are within +/- deadband,
    //  do nothing.  Note that if we are operating
    //  in reverse (negative motor percentages)
    //  This block may need to be changed.  With
    //  multiple motors perhaps the invert function
    //  will simplify this issue.  To do:  widen
    //  the deadband to determine the expected
    //  noise in the reading of the motor speed
    //  with a constant input.
    if(speed<(target_speed+deadband))  {
      motor_output+=incr;
    } 
    else if(speed>(target_speed-deadband))  {
      motor_output-=incr;
    }  else  {
     ;
    }
    
 
    end_time=System.nanoTime();
    elapsed_time=(double)((end_time-start_time)/1e6);  //  This is in nanoseconds
    loop_time=elapsed_time-previous_time;
    previous_time=elapsed_time;
 
    /* Print built string every 10 loops */
		if (++_loops >= 10) {
			_loops = 0;
      System.out.println(_sb.toString());
      System.out.println("Percent Output = " + motor_output);
      System.out.println("Elapsed Time = " + elapsed_time + "msec");
      System.out.println("Loop Time = " + loop_time + "msec");
    }
        /* Reset built string */
		_sb.setLength(0);
  }


  ///////////////////////////////////////////////////////////////////
  //  Function:  double computeMotorSpeed(double velocity)
  //////////////////////////////////////////////////////////////////
  //
  //  Purpose:  Given the desired shooter velocity in meters/second,
  //            based on the drive wheel diameter, it returns the
  //            desired rotational speed of the motor in encoder
  //            counts per 100msec (the direct readout of the
  //            encoder in velocity)
  //
  //  Arguments:Accepts the velocity in meters per second as double
  //
  //  Returns:  Motor speed in counts per 100msec as double
  //
  //  Remarks:  Uses the class member wheel_diameter.
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////
  public double computeMotorSpeed(double velocity)
  {
    double motor_rps;  //  motor revolutions per second
    double diameter;
    double counts_msec;

    diameter=wheel_diameter*2.54/100.0;  //  diameter in meters

    //  motor speed in revolutions per second
    motor_rps=velocity/(Math.PI*diameter);

    //  next, counts per 100 msec.
    counts_msec=2048.0*motor_rps/10.0;
    System.out.println("counts_msec = " + counts_msec);
    return(counts_msec);
  }

  //  Alternate overloaded function that allows changing of the
  //  wheel diameter.

  ///////////////////////////////////////////////////////////////////
  //  Function:  double computeMotorSpeed(double velocity, double diameter)
  //////////////////////////////////////////////////////////////////
  //
  //  Purpose:  Given the desired shooter velocity in meters/second,
  //            based on the drive wheel diameter, it returns the
  //            desired rotational speed of the motor in encoder
  //            counts per 100msec (the direct readout of the
  //            encoder in velocity)
  //
  //  Arguments:Accepts the velocity in meters per second as double
  //            Accepts the wheel diameter in inches.
  //
  //  Returns:  Motor speed in counts per 100msec as double
  //
  //  Remarks:  Uses the class member wheel_diameter.
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////
  public double computeMotorSpeed(double velocity,double diameter)
  {
    double motor_rps;  //  motor revolutions per second
    double counts_msec;

    //  convert wheel diameter to meters.
    diameter=diameter*2.54/100.0;  //  diameter in meters

    //  motor speed in revolutions per second
    //  Note important of operator precedence
    motor_rps=velocity/(Math.PI*diameter);

    //  next, coumpute counts per 100 msec.
    counts_msec=2048.0*motor_rps/10.0;
    System.out.println("counts_msec = " + counts_msec);
    return(counts_msec);
  }

   ///////////////////////////////////////////////////////////////////
  // Function:
  //////////////////////////////////////////////////////////////////
  //
  //  Purpose:
  //
  //  Arguments:
  //
  //  Returns:
  //
  //  Remarks:
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////

   ///////////////////////////////////////////////////////////////////
  //  Function:
  //////////////////////////////////////////////////////////////////
  //
  //  Purpose:
  //
  //  Arguments:
  //
  //  Returns:
  //
  //  Remarks:
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////

  
}

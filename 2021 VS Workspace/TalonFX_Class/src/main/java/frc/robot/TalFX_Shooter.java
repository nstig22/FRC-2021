
/////////////////////////////////////////////////////////////////////
//  File:  TalFX_Shooter.java
/////////////////////////////////////////////////////////////////////
//
//  Purpose:  Provides construction of a TalonTX object with
//            member functions specific to operation of the shooter
//            mechanism of the 2021 robot project.
//
//  Compiler:  Java via Microsoft VS for FRC.  Libraries from Phoenix
//            and WPI are included.
//
//  Remarks:  02/04/2021:  Need to make more of the variables private
//            and add more access functions for those variables.
//
//
///////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;

public class TalFX_Shooter {

    private double wheel_diameter=4.0;    //  wheel diameter in inches
    private double target_velocity;       //  target belt velocity ft/sec.
    private double starting_output=0.28;  //  motor output fraction
    private Boolean invert_state=false;   //  direction of motor rotation

    TalonFX _talon;
    Delay delay;

    
    String str;

    //  Timing parame int  
    long start_time=0;
    long end_time=0;
    double elapsed_time=0.0;
    double previous_time=0.0;
    double loop_time=0.0;
    double duration=3.0;
        
    /* String for output */
    StringBuilder _sb = new StringBuilder();
        
     int _loops = 0;             //  loop counter for printouts
    
    double target_speed;        //  target encoder speed in counts/100msec
    double motor_output;        //  fractional motor output
    double output_increment;    //  output increment for motor output
    double deadband=5.0;        //  deadband in counts per 100msec

    int init=1;


    //  Base constructor
    TalFX_Shooter(int can_addr)  {

            
        /* Hardware */
        _talon = new TalonFX(can_addr);
        delay=new Delay();
            
        /* Factory Default all hardware to prevent unexpected behaviour */
        //_talon.configFactoryDefault();
        System.out.println("Error Code = " + _talon.configFactoryDefault());
            
        /* Config sensor used for Primary PID [Velocity] */
        //  Integrated Sensor (TalonFX has it), primary closed loop, 50 msec timeout
        _talon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,0, 50);
         
        //  configNominalOutputForward(double percentOutput,int timeoutMS)
        //  These calls indicate 0 output nominal
        _talon.configNominalOutputForward(0, 50);
        _talon.configNominalOutputReverse(0, 50);
        
        //  configPeakOutputForward(double percentOut,int timeoutMS)
        //  These calls indicate maximum output
        //  note sign reversal for reverse
        _talon.configPeakOutputForward(1,50);
        _talon.configPeakOutputReverse(-1,50);

        motor_output=starting_output;

        invert_state=false;

    
       
    }

    //  Constructor allowing specification of invert status.  Note that in
    //  the 2021 robot shooter configuration, two of the drive motors are
    //  inverted.
    TalFX_Shooter(int can_addr,Boolean invert)  {
          
        /* Hardware */
        _talon = new TalonFX(can_addr);
        delay=new Delay();
      
        /* Factory Default all hardware to prevent unexpected behaviour */
        //_talon.configFactoryDefault();
        System.out.println("Error Code = " + _talon.configFactoryDefault());
            
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
              
        motor_output=starting_output;

        //  Set invert status
        if(invert==true)  {
          invert_state=true;
        }  else if(invert==false) {
          invert_state=false;
        }
            
    
    }


    //  Private data member access functions
    int setWheelDiameter(double diameter) {

        wheel_diameter=diameter;
        return(0);
    }

    double getWheelDiameter()  {

        return(wheel_diameter);
    }
    
    int setTargetVelocity(double velocity) {

        target_velocity=velocity;
        return(0);

    }

    double getTargetVelocity() {

        return(target_velocity);
    }

    int setStartingOutput(double output)
    {
        starting_output=output;
        return(0);
    }

    double getStartingOutput()
    {
        return(starting_output);
    }

    int setInvertState(Boolean state) 
    {
      invert_state=state;
      return(0);
    }

    Boolean getInvertState() 
    {
        return(invert_state);
    }

  ///////////////////////////////////////////////////////////////////
  //  Function:  double computeMotorSpeed(double velocity)
  //////////////////////////////////////////////////////////////////
  //
  //  Purpose:  Given the desired shooter velocity in feet/second,
  //            based on the drive wheel diameter, it returns the
  //            desired rotational speed of the motor in encoder
  //            counts per 100msec (the direct readout of the
  //            encoder in velocity)
  //
  //  Arguments:Accepts the velocity in feet per second as double
  //
  //  Returns:  Motor speed in counts per 100msec as double
  //
  //  Remarks:  Uses the class member wheel_diameter.
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////
  public double computeMotorSpeed(double velocity)
  {
    int debug=0;
    double motor_rps;  //  motor revolutions per second
    double diameter;
    double counts_msec;

    diameter=wheel_diameter/12.0;  //  diameter in feet

    //  motor speed in revolutions per second
    //  velocity expressed in feet/second, diameter in feet
    motor_rps=velocity/(Math.PI*diameter);

    //  next, counts per 100 msec.
    counts_msec=2048.0*motor_rps/10.0;

    if(debug==1)  {
        System.out.println("counts_msec = " + counts_msec);
    }

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
  //  Arguments:Accepts the velocity in feet per second as double
  //            Accepts the wheel diameter in inches.
  //
  //  Returns:  Motor speed in counts per 100msec as double
  //
  //  Remarks:  Allows specification of wheel_diameter in inches.
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////
  public double computeMotorSpeed(double velocity,double diameter)
  {
    int debug = 0;
    double motor_rps;  //  motor revolutions per second
    double counts_msec;

    //  convert wheel diameter to feet.
    diameter=diameter/12.0;  //  diameter in feet

    //  motor speed in revolutions per second
    //  Note important of operator precedence
    motor_rps=velocity/(Math.PI*diameter);

    //  next, coumpute counts per 100 msec.
    counts_msec=2048.0*motor_rps/10.0;

    if(debug==1) {
        System.out.println("counts_msec = " + counts_msec);
    }

    return(counts_msec);
  }

  ///////////////////////////////////////////////////////////////////
  // Function: int setVelocity()
  //////////////////////////////////////////////////////////////////
  //
  //  Purpose:
  //
  //  Arguments:
  //
  //  Returns:
  //
  //  Remarks:  Within TeleOp, called approximately every 20msec
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////
  public int setVelocity()
  {
    //  Temporary variables, calculated within this function
    double speed;
    double raw_error;
    double error;
    double incr;

  
    //  Start the timer
    if(init==1)  {
        start_time=System.nanoTime();
        if(invert_state==true)  {
          motor_output*=-1.0;
          target_speed*=-1.0;
        }
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
    delay.delay_milliseconds(15);

  	/* Prepare line to print */
		_sb.append("\tout:");
		/* Cast to int to remove decimal places */
		_sb.append((int) (motor_output * 100));
		_sb.append("%");	// Percent

    _sb.append("\tspd:");

    //  Read the selected sensor velocity in counts per 100msec
    //  "0" implies primary closed loop, "1" impies auxiliary closed loop
    //  Essentially we are reading the integrated TalonFX sensor.
    //  If running in inverted state, speed will be negative
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
    
    if(invert_state==false)  {
      if(speed<(target_speed-deadband))  {
        motor_output+=incr;
      } 
      else if(speed>(target_speed+deadband))  {
        motor_output-=incr;
      }  else  {
        ;
      }
    }  else {
      if(speed<(target_speed-deadband))  {
        motor_output+=incr;
      } 
      else if(speed>(target_speed+deadband))  {
        motor_output-=incr;
      }  else  {
        ;
      }

    }
  
    
 
    end_time=System.nanoTime();  //  This is in nanoseconds
    elapsed_time=(double)((end_time-start_time)/1e6);  //  msec
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
    

    return(0);
  }



///////////////////////////////////////////////////////////////////
  // Function: int setVelocity(double velocity)
  //////////////////////////////////////////////////////////////////
  //
  //  Purpose:    Sets and controls the belt velocity in ft/sec.
  //
  //  Arguments:  Accepts the intended belt velocity in feet/sec
  //
  //  Returns:  Zero normally
  //
  //  Remarks:  Used within TeleOp, called approximately every 20msec
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////
  public int setVelocity(double velocity)
  {
    //  Temporary variables, calculated within this function
    double speed;
    double raw_error;
    double error;
    double incr;

  
    //  Start the timer
    if(init==1)  {
        start_time=System.nanoTime();

        //  compute encoder output per 100msec
        target_speed=computeMotorSpeed(velocity);

        //  Implement inversion state
        if(invert_state==true)  {
          motor_output*=-1.0;
          target_speed*=-1.0;
        }
        init=0;
    }

    //  Set Talon  output percentage at the starting point
    //  Future:  This could be read from a table to more quickly hit
    //  the target.  Table would relate velocity to starting
    //  point (percentage) for motor drive.
    

    //  This starts the motor
    _talon.set(TalonFXControlMode.PercentOutput, motor_output);
    

    //  Need this delay to allow motor to start before attempting
    //  to read the encoder velocity
    delay.delay_milliseconds(15);

  	/* Prepare line to print */
		_sb.append("\tout:");
		/* Cast to int to remove decimal places */
		_sb.append((int) (motor_output * 100));
		_sb.append("%");	// Percent

    _sb.append("\tspd:");

    //  Read the selected sensor velocity in counts per 100msec
    //  "0" implies primary closed loop, "1" impies auxiliary closed loop
    //  Essentially we are reading the integrated TalonFX sensor.
    //  If running in inverted state, speed will be negative
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
    
    if(invert_state==false)  {
      if(speed<(target_speed-deadband))  {
        motor_output+=incr;
      } 
      else if(speed>(target_speed+deadband))  {
        motor_output-=incr;
      }  else  {
        ;
      }
    }  else {
      if(speed<(target_speed-deadband))  {
        motor_output+=incr;
      } 
      else if(speed>(target_speed+deadband))  {
        motor_output-=incr;
      }  else  {
        ;
      }

    }
  
    
 
    end_time=System.nanoTime();  //  This is in nanoseconds
    elapsed_time=(double)((end_time-start_time)/1e6);  //  msec
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
    

    return(0);
  }

//////////////////////////////////////////////////////////////////
  // Function: int setVelocity(double velocity,double duration)
  //////////////////////////////////////////////////////////////////
  //
  //  Purpose:    Sets and controls the belt velocity in ft/sec.
  //
  //  Arguments:  Accepts the intended belt velocity in feet/sec
  //              Accepts the duration in seconds.
  //
  //  Returns:  Zero normally
  //
  //  Remarks:  Used within Autonomous, called approximately every 20msec
  //            This function is called within the shooter thread
  //            and has a different mode of operation.  It is called
  //            once and loop structure is entirely internal.
  //            It will timeout if it takes too long to converge.
  //
  ///////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////
  public int setVelocity(double velocity,double duration)
  {
    //  Temporary variables, calculated within this function
    int debug=1;  //  will eliminate printouts, allow faster convergence
    double speed=0.0;
    double raw_error;
    double error=10000.0;
    double incr;

  
    //  Start the timer
    if(init==1)  {
        start_time=System.nanoTime();

        //  compute encoder output per 100msec
        target_speed=computeMotorSpeed(velocity);

        //  Implement inversion state
        if(invert_state==true)  {
          motor_output*=-1.0;
          target_speed*=-1.0;
        }
        init=0;
    }

    while(error>25.0)  {

      //  Set Talon  output percentage at the starting point
      //  Future:  This could be read from a table to more quickly hit
      //  the target.  Table would relate velocity to starting
      //  point (percentage) for motor drive.
      

      //  This starts the motor
      _talon.set(TalonFXControlMode.PercentOutput, motor_output);
      

      //  Need this delay to allow motor to start before attempting
      //  to read the encoder velocity
      delay.delay_milliseconds(15);

      if(debug==1)  {
          /* Prepare line to print */
          _sb.append("\tout:");
          /* Cast to int to remove decimal places */
          _sb.append((int) (motor_output * 100));
          _sb.append("%");	// Percent

          _sb.append("\tspd:");
      }

      //  Read the selected sensor velocity in counts per 100msec
      //  "0" implies primary closed loop, "1" impies auxiliary closed loop
      //  Essentially we are reading the integrated TalonFX sensor.
      //  If running in inverted state, speed will be negative
      speed=_talon.getSelectedSensorVelocity(0);
      //  Compute the difference error between target
      //  and actual motor speed
      raw_error=target_speed-speed;
      error=Math.abs(raw_error); 

      if(debug==1)  {
          _sb.append(speed);
          _sb.append("u"); 	// Native units
          
        
          _sb.append("\traw error:");
          _sb.append(raw_error);
          _sb.append("u"); 	// Native units
          _sb.append("\terror:");
          _sb.append(error);
          _sb.append("u"); 	// Native units
      }

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
      
      if(invert_state==false)  {
        if(speed<(target_speed-deadband))  {
          motor_output+=incr;
        } 
        else if(speed>(target_speed+deadband))  {
          motor_output-=incr;
        }  else  {
          ;
        }
      }  else {
        if(speed<(target_speed-deadband))  {
          motor_output+=incr;
        } 
        else if(speed>(target_speed+deadband))  {
          motor_output-=incr;
        }  else  {
          ;
        }

      }
      
  
      end_time=System.nanoTime();  //  This is in nanoseconds
      elapsed_time=(double)((end_time-start_time)/1e6);  //  msec
      loop_time=elapsed_time-previous_time;
      previous_time=elapsed_time;

      if(debug==1) {
          System.out.println("Elapsed Time = " + elapsed_time + "msec");
          System.out.println("Allowed Time = " + duration*1000.0 + "msec");
      }
      if(elapsed_time>(duration*1e3))  {
        _talon.set(TalonFXControlMode.PercentOutput, 0.0);
        System.out.println("Thread timed out");
        break;
      }
    

      if(debug==1)  {
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
      
  }  //  while(err0r>25.0)

  System.out.println("Elapsed Time = " + elapsed_time +"msec");
  System.out.println("Motor Speed = " + speed + "counts/100msec");
  System.out.println("Exit error = " + error);

  delay.delay_milliseconds(duration*1e3);  //  run for duration seconds
  _talon.set(TalonFXControlMode.PercentOutput, 0.0);
 

  return(0);
  }


///////////////////////////////////////////////////////////////////
  // Function: int setVelocityII()
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
  public int setVelocityII()
  {

    double speed;
  
    delay.delay_milliseconds(15);

    target_speed=computeMotorSpeed(30.0);
    System.out.println("target speed = " + target_speed + "counts/100msec");

    _talon.set(TalonFXControlMode.PercentOutput, -motor_output);

    delay.delay_milliseconds(15);

    speed=_talon.getSelectedSensorVelocity(0);

    System.out.println("speed = " + speed + "counts/100msec");
    
    return(0);
  }







  ///////////////////////////////////////////////////////////////////
  //  Function:  int stop()
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
  int stop() 
  {
    _talon.set(TalonFXControlMode.PercentOutput, 0.0);
    return(0);
  }


  

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

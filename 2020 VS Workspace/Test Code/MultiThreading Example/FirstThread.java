
/////////////////////////////////////////////////////////////////////
//  File:  FirstThread.java
/////////////////////////////////////////////////////////////////////
//
//  Purpose:  Illustrates the creation of a thread that runs
//            outside of main().  Note that delays within the
//            thread do not delay the main thread.  Also note
//            that the main thread must complete after any of
//            the child threads (like this one) complete or
//            a nasty system crash will result.
//
//  Remarks:  1.  We should be able to use the join() function
//                to wait for the child thread to complete.
//                This compiler doesn't like it.  It must be
//                contained within a try/catch block.
//            2.  Adding a small delay in the run() portion allows
//                other threads to intersperse with this one.
//            3.  In terms of using this concept to control the
//                robot, it might allow the lifts and tilts to
//                perform their movements while the operator can
//                drive the robot to the position.
//            4.  Watchdogs will need to be disabled??  Not sure.
//                When we are in a thread outside the robot 
//                application we will be manipulating motors until
//                they acheive the position.  In many ways it is like
//                a while() loop but the main() program doesn't hang.
//                As previously mentioned the thread must complete
//                prior to the exit of the main() thread.
//
//    03/13/2019:  Changed for() loops to while() loops.  Added
//    escape counters - a break statement when the count is exceeded
//    will take us out of the while() loop.
//
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////


import java.util.*;

class Compute1Thread implements Runnable  {
    String name;
    Thread t;
    Runtime r=Runtime.getRuntime();
    private Delay delay;


    //  Constructor
    Compute1Thread(String threadname)  {
        name=threadname;
        t=new Thread(this,name);
        System.out.println("New thread: " + t);
        delay=new Delay();
        t.start();  //  Start the thread        
    }

    //  Entry point for the thread.  This is where any desired
    //  action needs to be implemented.  It will run in "parallel"
    //  or "time share" with other active threads.
    public void run()  {
        double x;
        double y=0;
        int i=0;
        int count=0;
    
        while(y<100.0)  {
            x=MultiThreadTest.x_start + (i*.1);
            y=4*x*x - 7*x + 5.0;
            System.out.println(name + " i = " + i + " x = " + x + " y = " + y);
            //  This small delay allows the second thread to intersperse with
            //  this one.  Without the delay, this thread might run to completion
            //  before the other thread gets to work.
            delay.delay_milliseconds(1.0);
            i++;
            count++;
            if(count>100)break;
        }
        System.out.println(name + "Exiting");

        //  Initiate garbage collection.
        r.gc();
    }
}

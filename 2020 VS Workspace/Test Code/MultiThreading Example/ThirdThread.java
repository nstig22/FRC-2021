import java.util.*;

class Compute3Thread implements Runnable  {
    String name;
    Thread t;
    Runtime r=Runtime.getRuntime();
    private Delay delay;


    //  Constructor
    Compute3Thread(String threadname)  {
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
        double y=0.0;
        int i=0;
        int count=0;
       
        while(y<500.0) {
            x=i*.1;
            y=Math.pow(x,2);
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

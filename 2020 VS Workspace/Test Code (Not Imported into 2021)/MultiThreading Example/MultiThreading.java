
/////////////////////////////////////////////////////////////////////
//  File:  MultThreading.java
/////////////////////////////////////////////////////////////////////
//
//  Purpose:  Illustrates multithreading using two mathematical
//            computations.
//
//  Remarks:  We are able to re-create the thread as long as we
//            complete each instance.  This would be important in
//            a robotics application in that we will be repeating
//            the various positions.
//
//            3/13/2019:  Curious about the effect of using while()
//            loops within the threads.  Changed the two threads 
//            accordingly.  The sleep() for the main thread is now
//            a multiple of the for() loop index.
//
//            8/16/2019:  Got rid of the static reference to a 
//            non-static field by declaring member types T1, T2
//            as static.  This still does not result in the use of
//            the t.join() function. The solution is to call the t.join()
//            functions within a try/catch block.  See the Thread class
//            definition for further details.
//
//            Can we call these threads multiple times?  As long as we
//            insure that the previous instance has completed it appears
//            so.  In the most recent code example extensive of the
//            Thread.t.isAlive() function is made to twice implement the
//            same threads.  No delays are required within the main
//            thread.
//
/////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////
import java.util.*;
import java.io.*;

class MultiThreadTest {

    // This must be declared as static in that it is used within a static
    // thread T1.
    static double x_start = 0.0;

    // These need to be declared static or the following
    // message will result:
    // Cannot make a static reference to the non-static field T1
    // Cannot make a static reference to the non-static field T2
    //
    // main(...) is declared static therefore:
    // 1. It can only directly call other static methods.
    // 2. It can only directly access static data.
    // 3. It cannot relate to "this" in any way.
    // See "The Java Language", Herbert Schildt, Oracle Press, Ninth Edition,pg. 145
    // for a thorough description of the keyword "static".
    static Compute1Thread T1;
    static Compute2Thread T2;
    static Compute3Thread T3;

    static BufferedReader br;
    static char c;

    public static void main(String[] args) {

        T1 = new Compute1Thread("quadratic");
        T2 = new Compute2Thread("cubic");
        T3 = new Compute3Thread("exp");

        try {
            T1.t.join();
            T2.t.join();
            T3.t.join();

        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
        }

        System.out.println("T1 active = " + T1.t.isAlive());
        System.out.println("T2 active = " + T2.t.isAlive());
        System.out.println("T3 active = " + T3.t.isAlive());

        // Here we make sure that the previously created threads
        // are not active. In this case we can re-create the
        // same threads. Note the use of "t.isAlive()". Note
        // the t.join() must be called within a try/catch block.
        // Note that no delays are required.
        if ((T1.t.isAlive() == false) && (T2.t.isAlive() == false) && (T3.t.isAlive() == false)) {
            T1 = new Compute1Thread("quadratic");
            T2 = new Compute2Thread("cubic");
            T3 = new Compute3Thread("exp");

            try {
                T1.t.join();
                T2.t.join();
                T3.t.join();
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted");
            }

            System.out.println("T1 active = " + T1.t.isAlive());
            System.out.println("T2 active = " + T2.t.isAlive());
        }
        System.out.println("Main thread exiting");
    }
}
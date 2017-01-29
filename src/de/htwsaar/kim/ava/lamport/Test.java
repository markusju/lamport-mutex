package de.htwsaar.kim.ava.lamport;

import java.util.ArrayList;

/**
 * Created by markus on 29.01.17.
 */
public class Test {


    private int threads = 100;

    private ArrayList<Integer> ticket = new ArrayList<>(threads); // ticket for threads in line, n - number of threads
    // Java initializes each element of 'ticket' to 0

    private ArrayList<Boolean> entering = new ArrayList<>(threads); // True when thread entering in line
    // Java initializes each element of 'entering' to false

    public void lock(int pid) // thread ID
    {
        entering.set(pid, true);
        int max = 0;
        for (int i = 0; i < threads; i++)
        {
            int current = ticket.get(i);
            if (current > max)
            {
                max = current;
            }
        }
        ticket.set(pid, 1 + max);
        entering.set(pid, false);
        for (int i = 0; i < ticket.size(); ++i)
        {
            if (i != pid)
            {
                while (entering.get(i)) { Thread.yield(); } // wait while other thread picks a ticket
                while (ticket.get(i) != 0 && ( ticket.get(pid) > ticket.get(i)  ||
                        (ticket.get(pid) == ticket.get(i) && pid > i)))
                { Thread.yield(); }
            }
        }
        // The critical section goes here...
    }
    public void unlock(int pid)
    {
        ticket.set(pid, 0);
    }



    public static void main(String... args) {


    }




}



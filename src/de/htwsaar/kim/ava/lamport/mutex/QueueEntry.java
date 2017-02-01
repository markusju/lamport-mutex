package de.htwsaar.kim.ava.lamport.mutex;

import de.htwsaar.kim.ava.lamport.process.LamportProcess;

/**
 * Created by markus on 31.01.17.
 */
public class QueueEntry implements Comparable{
    private Integer stamp;
    private Integer process;


    public QueueEntry(int stamp, int process) {
        this.stamp = stamp;
        this.process = process;
    }

    public Integer getStamp() {
        return stamp;
    }

    public Integer getProcess() {
        return process;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof QueueEntry) {
            int val = stamp.compareTo(((QueueEntry) o).stamp);

            if (val == 0) return process.compareTo(((QueueEntry) o).process);
            return val;
        } else
            throw new IllegalArgumentException("Nope");
    }

    @Override
    public String toString() {
        return "<"+stamp+","+process+">";
    }
}

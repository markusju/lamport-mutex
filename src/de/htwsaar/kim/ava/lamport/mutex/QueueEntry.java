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
            return stamp.compareTo(((QueueEntry) o).stamp);
        } else
            throw new IllegalArgumentException("Nope");
    }
}

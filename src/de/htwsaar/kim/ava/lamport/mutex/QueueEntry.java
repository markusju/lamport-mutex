package de.htwsaar.kim.ava.lamport.mutex;

import de.htwsaar.kim.ava.lamport.process.LamportProcess;

/**
 * Created by markus on 31.01.17.
 */
public class QueueEntry implements Comparable{
    private Integer stamp;
    private LamportProcess lamportProcess;


    public QueueEntry(int stamp, LamportProcess lamportProcess) {
        this.stamp = stamp;
        this.lamportProcess = lamportProcess;
    }

    public Integer getStamp() {
        return stamp;
    }

    public LamportProcess getLamportProcess() {
        return lamportProcess;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof QueueEntry) {
            return stamp.compareTo(((QueueEntry) o).stamp);
        } else
            throw new IllegalArgumentException("Nope");
    }
}

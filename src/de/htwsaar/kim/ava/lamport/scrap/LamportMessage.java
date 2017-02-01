package de.htwsaar.kim.ava.lamport.scrap;

/**
 * Created by markus on 31.01.17.
 */

public class LamportMessage {
    private int stamp;
    private int processId;
    private LamportMessageType type;

    enum LamportMessageType {
        ACQUIRE,
        RELEASE
    }


    public LamportMessage(LamportMessageType type) {
        this.type = type;
    }


    public int getStamp() {
        return stamp;
    }

    public void setStamp(int stamp) {
        this.stamp = stamp;
    }


    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public int getProcessId() {
        return processId;
    }

    public LamportMessageType getType() {
        return type;
    }
}

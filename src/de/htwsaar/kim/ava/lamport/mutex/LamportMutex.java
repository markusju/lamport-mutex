package de.htwsaar.kim.ava.lamport.mutex;

import com.google.common.collect.TreeMultimap;
import de.htwsaar.kim.ava.lamport.process.LamportProcess;
import javafx.util.Pair;

import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

/**
 * Created by markus on 29.01.17.
 */
public class LamportMutex {
    //Queue of Requesting Process ID sorted by their timestamps
    private TreeSet<QueueEntry> queue = new TreeSet<>();
    private int timeStamp = 0;
    //Key-Val Liste aller Prozesse nach ID
    private LamportProcess ownProcess;

    public LamportMutex(LamportProcess ownProcess) {
        this.ownProcess = ownProcess;
    }

    public void sendMessage(LamportProcess destination, LamportMessage message) {
        //TimeStamp
        timeStamp++;
        message.setStamp(timeStamp);
        destination.getLamportMutex().receiveMessage(ownProcess, message);
        message.setProcessId(ownProcess.getId());

    }
    public synchronized void receiveMessage(LamportProcess source, LamportMessage message) {
        //TimeStamp
        int recvStamp = message.getStamp();
        timeStamp = Integer.max(recvStamp, timeStamp);
        ownProcess.getLogger().log(Level.INFO, "Received "+message.getType()+" from "+source.getId()+" t="+timeStamp);

        switch(message.getType()) {
            case ACQUIRE:
                queue.add(new QueueEntry(
                        timeStamp,
                        source
                ));
                break;
            case RELEASE:
                break;
        }


    }

    public void acquire() {
        queue.add(new QueueEntry(timeStamp, ownProcess));
        for (LamportProcess lamportProcess: ownProcess.getProcessManager().getAll()) {
            if (lamportProcess.getId() == ownProcess.getId()) continue;

            sendMessage(
                    lamportProcess,
                    new LamportMessage(LamportMessage.LamportMessageType.ACQUIRE)
            );
        }

    }

    public void release() {

    }

    public void terminate() {

    }






}

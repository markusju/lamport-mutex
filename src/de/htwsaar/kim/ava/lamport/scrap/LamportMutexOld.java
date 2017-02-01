package de.htwsaar.kim.ava.lamport.scrap;

import de.htwsaar.kim.ava.lamport.mutex.QueueEntry;
import de.htwsaar.kim.ava.lamport.process.LamportProcess;

import java.util.TreeSet;
import java.util.logging.Level;

/**
 * Created by markus on 29.01.17.
 */
public class LamportMutexOld {
    //Queue of Requesting Process ID sorted by their timestamps
    private TreeSet<QueueEntry> queue = new TreeSet<>();
    private int timeStamp = 0;
    //Key-Val Liste aller Prozesse nach ID
    private LamportProcess ownProcess;

    public LamportMutexOld(LamportProcess ownProcess) {
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

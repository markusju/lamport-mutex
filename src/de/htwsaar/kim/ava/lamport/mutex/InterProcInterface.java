package de.htwsaar.kim.ava.lamport.mutex;

import de.htwsaar.kim.ava.lamport.process.LamportProcess;

/**
 * Created by markus on 29.01.17.
 */
public interface InterProcInterface {

    void acquireRessource(LamportProcess process) throws InterruptedException;
    void releaseResource(LamportProcess process);

    void notifyTerminatePartner(LamportProcess process);

    void addToMap(LamportProcess lamportProcess);

}

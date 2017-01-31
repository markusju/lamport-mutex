package de.htwsaar.kim.ava.lamport.mutex;

import de.htwsaar.kim.ava.lamport.process.LamportProcess;

/**
 * Created by markus on 29.01.17.
 */
public class Lamport implements InterProcInterface {


    @Override
    public void acquireRessource(LamportProcess process) throws InterruptedException {

    }

    @Override
    public void releaseResource(LamportProcess process) {

    }

    @Override
    public void notifyTerminatePartner(LamportProcess process) {

    }

    @Override
    public void addToMap(LamportProcess lamportProcess) {

    }
}

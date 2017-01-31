package de.htwsaar.kim.ava.lamport.mutex;

import de.htwsaar.kim.ava.lamport.mutex.InterProcInterface;
import de.htwsaar.kim.ava.lamport.process.LamportProcess;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by markus on 29.01.17.
 */
public class DummySemaphore implements InterProcInterface {

    private Semaphore semaphore = new Semaphore(1, true);
    private Map<Integer, LamportProcess> processes = new HashMap<>();

    public DummySemaphore() {
    }

    @Override
    public void acquireRessource(LamportProcess process) throws InterruptedException {
        semaphore.acquire();
    }

    @Override
    public void releaseResource(LamportProcess process) {
        semaphore.release();
    }

    @Override
    public void notifyTerminatePartner(LamportProcess process) {
        if (process.getId() % 2 != 0) {
            processes.get(process.getId()+1).terminate();
        } else {
            processes.get(process.getId()-1).terminate();
        }
    }

    @Override
    public void addToMap(LamportProcess lamportProcess) {
        processes.put(lamportProcess.getId(), lamportProcess);
    }
}

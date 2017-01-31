package de.htwsaar.kim.ava.lamport.process;

import de.htwsaar.kim.ava.lamport.mutex.InterProcInterface;
import de.htwsaar.kim.ava.lamport.file.LamportFile;

import java.io.IOException;

/**
 * Created by markus on 29.01.17.
 */
public class LamportProcess implements Runnable {

    private int id;
    private int counter;
    private LamportFile lamportFile;
    private InterProcInterface interProcInterface;
    private boolean terminate = false;

    public LamportProcess(LamportFile lamportFile, InterProcInterface interProcInterface, int id) {
        this.lamportFile = lamportFile;
        this.interProcInterface = interProcInterface;
        this.id = id;
        interProcInterface.addToMap(this);
    }


    private boolean loopCondition() {
        return !terminate;
    }

    public int getId() {
        return id;
    }

    public void terminate() {
        terminate = true;
    }

    @Override
    public void run() {
        try {

            while (loopCondition()) {
                try {
                    interProcInterface.acquireRessource(this);

                    if (!loopCondition()) break;


                    if (lamportFile.getValue() == 0) {
                        counter++;
                        if (counter >= 3) {
                            terminate();
                            interProcInterface.notifyTerminatePartner(this);
                            break;
                        }
                    }

                    if (id % 2 != 0) { //Ungerade
                        lamportFile.incrementValue();
                    } else { //Gerade
                        lamportFile.decrementValue();
                    }
                    lamportFile.attachId(id);
                } finally {
                    interProcInterface.releaseResource(this);
                }
            }





        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }




}

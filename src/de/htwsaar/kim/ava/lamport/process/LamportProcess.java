package de.htwsaar.kim.ava.lamport.process;

import de.htwsaar.kim.ava.lamport.file.LamportFile;
import de.htwsaar.kim.ava.lamport.mutex.LamportMutex;


import java.io.IOException;


/**
 * Created by markus on 29.01.17.
 */
public class LamportProcess implements Runnable{

    private int id;
    private int counter;
    private LamportFile lamportFile;
    private LamportMutex lamportMutex;
    private boolean terminate = false;

    public LamportProcess(LamportFile lamportFile, int id) {
        this.lamportFile = lamportFile;
        this.id = id;
        lamportMutex = new LamportMutex(id);

    }

    private boolean loopCondition() {
        return !terminate;
    }


    public void terminate() {
        terminate = true;
    }

    @Override
    public void run() {
        try {

            while (loopCondition()) {
                try {
                    lamportMutex.acquire();

                    if (!loopCondition()) break;


                    if (lamportFile.getValue() == 0) {
                        counter++;
                        if (counter >= 3) {
                            terminate();
                            //lamportMutex.terminate();
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
                    lamportMutex.release();
                }
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


}

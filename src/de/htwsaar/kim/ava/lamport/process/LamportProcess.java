package de.htwsaar.kim.ava.lamport.process;

import de.htwsaar.kim.ava.lamport.logging.SingleLineFormatter;
import de.htwsaar.kim.ava.lamport.mutex.InterProcInterface;
import de.htwsaar.kim.ava.lamport.file.LamportFile;
import de.htwsaar.kim.ava.lamport.mutex.LamportMutex;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by markus on 29.01.17.
 */
public class LamportProcess implements Runnable{

    private int id;
    private int counter;
    private LamportFile lamportFile;
    private LamportMutex lamportMutex;
    private ProcessManager processManager;
    private boolean terminate = false;
    private Logger logger;

    public LamportProcess(LamportFile lamportFile, ProcessManager manager, int id) {
        this.lamportFile = lamportFile;
        this.id = id;
        manager.add(this);
        this.processManager = manager;
        lamportMutex = new LamportMutex(this);

        logger = Logger.getLogger(String.valueOf(id));
        Handler handler = new ConsoleHandler();
        handler.setFormatter(new SingleLineFormatter());
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
    }


    private boolean loopCondition() {
        return !terminate;
    }

    public int getId() {
        return id;
    }

    public Logger getLogger() {
        return logger;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public LamportMutex getLamportMutex() {
        return lamportMutex;
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
                            lamportMutex.terminate();
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





        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

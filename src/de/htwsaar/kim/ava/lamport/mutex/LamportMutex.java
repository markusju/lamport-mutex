package de.htwsaar.kim.ava.lamport.mutex;

import de.htwsaar.kim.ava.lamport.logging.SingleLineFormatter;
import de.htwsaar.kim.ava.lamport.mutex.client.TCPClient;
import de.htwsaar.kim.ava.lamport.mutex.protocol.commands.ACQUIRE;
import de.htwsaar.kim.ava.lamport.mutex.protocol.commands.RELEASE;
import de.htwsaar.kim.ava.lamport.mutex.protocol.commands.TERMINATE;
import de.htwsaar.kim.ava.lamport.mutex.server.TCPParallelServer;
import de.htwsaar.kim.ava.lamport.process.LamportProcess;

import java.io.IOException;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Semaphore;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by markus on 01.02.17.
 */
public class LamportMutex {

    public static int[] PROCESSES = {
            1,
            2,
            3,
            4
    };

    private TCPParallelServer tcpParallelServer;
    private TCPClient tcpClient = new TCPClient(this);
    private int port;
    private int id;

    private ConcurrentSkipListSet<QueueEntry> queue = new ConcurrentSkipListSet<>();
    private int currentTimestamp = 0;
    public int counter = 0;

    public Logger logger;

    private Semaphore lock = new Semaphore(0, true);
    private LamportProcess lamportProcess;



    public LamportMutex(int id, LamportProcess lamportProcess) {
        this.id = id;
        this.port = 5000+id;
        this.currentTimestamp = 0;
        this.lamportProcess = lamportProcess;

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new SingleLineFormatter());
        logger = Logger.getLogger(String.valueOf(id));
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);

        this.tcpParallelServer = new TCPParallelServer(this);
        tcpParallelServer.start();
    }


    public TCPParallelServer getTcpParallelServer() {
        return tcpParallelServer;
    }

    public TCPClient getTcpClient() {
        return tcpClient;
    }

    public int getPort() {
        return port;
    }


    public int getId() {
        return id;
    }

    public Semaphore getLock() {
        return lock;
    }

    public LamportProcess getLamportProcess() {
        return lamportProcess;
    }

    public void acquire() throws InterruptedException, IOException {
        logger.log(Level.INFO, "Acquire");
        requestCS();
        lock.acquire();
    }

    public void release() throws IOException, InterruptedException {
        logger.log(Level.INFO, "Release");
        removeProcessFromQueue(id);
        for (int el: PROCESSES) {
            if (el == id) continue;
            RELEASE.sendRELEASE(tcpClient, "127.0.0.1", el+5000);
        }

    }

    public void terminate() throws IOException {
        int partner = id;

        if (id % 2 == 0)
            partner--;
        else
            partner++;

        TERMINATE.startTERMINATE(getTcpClient(), "127.0.0.1", 5000+partner);
    }

    private void requestCS() throws IOException {
        incrementTimeStamp();
        queue.add(new QueueEntry(getCurrentTimestamp(), getId()));



        for (int el: PROCESSES) {
            if (el == id) continue;
            ACQUIRE.sendACQUIRE(tcpClient, "127.0.0.1", 5000+el, getCurrentTimestamp());
        }



    }

    public ConcurrentSkipListSet<QueueEntry> getQueue() {
        return queue;
    }

    public void addToQueue(QueueEntry queueEntry) throws InterruptedException {
        queue.add(queueEntry);
    }

    public void removeProcessFromQueue(int id) throws InterruptedException {
        QueueEntry toBeRemoved = null;
        for (QueueEntry entry: queue) {
            if (entry.getProcess() == id)
                toBeRemoved = entry;
        }

        if (toBeRemoved == null) return;
        queue.remove(toBeRemoved);
    }

    public QueueEntry getOwn() {
        for (QueueEntry entry: queue) {
            if (entry.getProcess() == id)
                return entry;
        }
        return null;
    }





    public void incrementTimeStamp() {
        currentTimestamp++;
    }

    public void setAndIncTimeStamp(int newValue) {
        currentTimestamp = Integer.max(newValue, currentTimestamp)+1;
    }

    public int getCurrentTimestamp() {
        return currentTimestamp;
    }
}

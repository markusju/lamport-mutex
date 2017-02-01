package de.htwsaar.kim.ava.lamport.mutex;

import de.htwsaar.kim.ava.lamport.logging.SingleLineFormatter;
import de.htwsaar.kim.ava.lamport.mutex.client.TCPClient;
import de.htwsaar.kim.ava.lamport.mutex.protocol.commands.ACQUIRE;
import de.htwsaar.kim.ava.lamport.mutex.server.TCPParallelServer;

import java.io.IOException;
import java.util.Queue;
import java.util.TreeSet;
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

    private TreeSet<QueueEntry> queue = new TreeSet<>();
    private int currentTimestamp = 0;
    public int counter = 0;

    public Logger logger;

    private Semaphore lock = new Semaphore(0);



    public LamportMutex(int id) {
        this.id = id;
        this.port = 5000+id;

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




    public void acquire() throws InterruptedException, IOException {
        logger.log(Level.INFO, "Acquire");
        requestCS();
        lock.acquire();
    }

    public void release() {


    }

    private void requestCS() throws IOException {
        queue.add(new QueueEntry(getCurrentTimestamp(), getId()));

        for (int el: PROCESSES) {
            if (el == id) continue;
            ACQUIRE.sendACQUIRE(tcpClient, "127.0.0.1", 5000+el, getCurrentTimestamp());
        }



    }

    public TreeSet<QueueEntry> getQueue() {
        return queue;
    }

    public void removeProcessFromQueue(int id) {
        for (QueueEntry entry: queue) {
            if (entry.getProcess() == id)
                queue.remove(entry);
        }
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

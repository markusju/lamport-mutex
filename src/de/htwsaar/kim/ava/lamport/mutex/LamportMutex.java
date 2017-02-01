package de.htwsaar.kim.ava.lamport.mutex;

import de.htwsaar.kim.ava.lamport.mutex.client.TCPClient;
import de.htwsaar.kim.ava.lamport.mutex.protocol.commands.ACQUIRE;
import de.htwsaar.kim.ava.lamport.mutex.server.TCPParallelServer;

import java.io.IOException;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

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

    private TCPParallelServer tcpParallelServer = new TCPParallelServer(this);
    private TCPClient tcpClient = new TCPClient(this);
    private int port;
    private int id;

    private TreeSet<QueueEntry> queue = new TreeSet<>();
    private int currentTimestamp = id;

    private Semaphore lock = new Semaphore(0);



    public LamportMutex(int id) {
        this.id = id;
        this.port = 5000+id;
        tcpParallelServer.start();
    }


    public TCPParallelServer getTcpParallelServer() {
        return tcpParallelServer;
    }

    public int getPort() {
        return port;
    }


    public int getId() {
        return id;
    }

    public void acquire() throws InterruptedException, IOException {
        requestCS();
        lock.acquire();
    }

    public void release() {


    }


    private void requestCS() throws IOException {
        for (int el: PROCESSES) {
            if (el == id) continue;
            ACQUIRE.sendACQUIRE(tcpClient, "127.0.0.1", 5000+el);
        }

        queue.add(new QueueEntry(getCurrentTimestamp(), getId()));

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

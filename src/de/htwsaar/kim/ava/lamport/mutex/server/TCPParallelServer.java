package de.htwsaar.kim.ava.lamport.mutex.server;

import de.htwsaar.kim.ava.lamport.mutex.LamportMutex;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

/**
 * Created by markus on 05.06.15.
 */
public class TCPParallelServer extends Thread {

    public static final int MAX_NUM_THREADS = 20;
    public static final int DEFAULT_PORT = 4322;
    private static final int DEFAULT_TIMEOUT = 10000;
    private static final String THREAD_NAME = "ServerThread";

    private int port = DEFAULT_PORT;
    private int timeout = DEFAULT_TIMEOUT;

    private ServerSocket socket;
    private ServerStatus serverStatus = ServerStatus.STOPPED;

    public Semaphore mutex = new Semaphore(1, true);

    private LamportMutex lamportMutex;

    protected List<TCPParallelWorker> workerList = new LinkedList<>();


    public TCPParallelServer(LamportMutex lamportMutex) {
        this.port = lamportMutex.getPort();
        this.lamportMutex = lamportMutex;
        setName(THREAD_NAME+"-"+ Integer.toString(port));
    }

    /**
     * Start the parallel Server.
     */
    public void run() {
        try
        {
            // Erzeugen der Socket/binden an Port/Wartestellung
            socket = new ServerSocket(port, 10);
            changeServerStatus(ServerStatus.RUNNING);

            while (!isInterrupted())
            {
                try {
                    Socket client = socket.accept();
                    //Connection Timeout
                    client.setSoTimeout(timeout);
                    TCPParallelWorker worker = new TCPParallelWorker(client, lamportMutex);

                    Thread t = new Thread(worker);
                    t.setName("Worker-of-"+getName());

                    mutex.acquire();
                    t.start();
                } catch (IOException e) {
                        e.printStackTrace();
                }

            }

        }
        catch (Exception e)
        {
            changeServerStatus(ServerStatus.ERROR);
        }

        changeServerStatus(ServerStatus.STOPPED);
    }

    public boolean isRunning() {
        return !isInterrupted();
    }

    public void stopServer() {
        interrupt();
        //workerPool.shutdown();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(TCPParallelWorker worker : workerList) {
            worker.stopWorker();
        }


    }


    /**
     * Gets the port the server is
     * listening on.
     *
     * @return
     */
    public int getPort() {
        return this.port;
    }


    private void changeServerStatus(ServerStatus newStatus) {
        this.serverStatus = newStatus;
    }


    public ServerStatus getServerStatus() {
        return serverStatus;
    }
}

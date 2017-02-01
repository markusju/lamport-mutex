package de.htwsaar.kim.ava.lamport.mutex.server;


import de.htwsaar.kim.ava.lamport.mutex.LamportMutex;
import de.htwsaar.kim.ava.lamport.mutex.protocol.AvaNodeProtocol;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by markus on 07.06.15.
 */
public class TCPParallelWorker implements Runnable {


    private static final String THREAD_NAME = "WorkerThread";
    private Socket socket;

    private LamportMutex lamportMutex;

    public TCPParallelWorker(Socket socket, LamportMutex lamportMutex) {
        //setName(THREAD_NAME);
        this.lamportMutex = lamportMutex;
        this.socket = socket;
    }


    public void stopWorker() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run - method of the Worker
     */
    public void run() {
        try {

            new AvaNodeProtocol(socket, lamportMutex).run();
            socket.close();

        } catch (Exception e) {
            //e.printStackTrace();

        }


    }






}

package de.htwsaar.kim.ava.lamport.mutex.client;

import de.htwsaar.kim.ava.lamport.mutex.LamportMutex;
import de.htwsaar.kim.ava.lamport.mutex.protocol.AvaNodeClientProtocol;
import de.htwsaar.kim.ava.lamport.mutex.protocol.requests.Request;

import javax.xml.soap.Node;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by markus on 27.12.16.
 */
public class TCPClient {


    private AvaNodeClientProtocol avaNodeClientProtocol;
    private LamportMutex lamportMutex;


    public TCPClient(LamportMutex lamportMutex) {
        this.lamportMutex = lamportMutex;

    }

    private void connect(String host, int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5000);
        avaNodeClientProtocol = new AvaNodeClientProtocol(socket);
    }

    private void disconnect() throws IOException {
        avaNodeClientProtocol.close();
    }

    public void sendRequest(String host, int port, Request request) throws IOException {
        connect(host, port);

        lamportMutex.incrementTimeStamp();

        request.addParameter("SRC", String.valueOf(lamportMutex.getId()));
        request.addParameter("TIMESTAMP", String.valueOf(lamportMutex.getCurrentTimestamp()));
        avaNodeClientProtocol.putLine(request.toProtString());
        disconnect();
    }




}

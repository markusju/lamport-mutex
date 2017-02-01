package de.htwsaar.kim.ava.lamport.mutex.protocol;

import com.google.common.base.Splitter;
import de.htwsaar.kim.ava.lamport.exception.ClientErrorException;
import de.htwsaar.kim.ava.lamport.mutex.LamportMutex;
import de.htwsaar.kim.ava.lamport.mutex.protocol.commands.Command;
import de.htwsaar.kim.ava.lamport.mutex.protocol.requests.AvaNodeProtocolRequest;
import de.htwsaar.kim.ava.lamport.mutex.protocol.requests.Request;


import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by markus on 25.12.16.
 */
public class AvaNodeProtocol extends AbstractBaseProtocol implements Runnable {

    private Request request;
    private int source;
    private int timestamp;

    private LamportMutex lamportMutex;

    public AvaNodeProtocol(Socket socket, LamportMutex lamportMutex) throws IOException {
        super(socket);
        this.lamportMutex = lamportMutex;
    }

    public int getSource() {
        return source;
    }
    public int getTimestamp() { return timestamp; }

    public Request getRequest() {
        return request;
    }

    public LamportMutex getLamportMutex() {
        return lamportMutex;
    }

    private void checkRequest() throws ClientErrorException {
        //Contains SRC Param?
        if (!request.getParameters().containsKey("SRC")) {
            throw new ClientErrorException("SRC Paramter was not supplied!");
        }
        if (!request.getParameters().containsKey("TIMESTAMP")) {
            throw new ClientErrorException("TIMESTAMP Paramter was not supplied!");
        }
    }


    @Override
    public void run() {
        try {

            //Syntaktische Analyse
            request = new AvaNodeProtocolRequest(this);

            //Semantische Analyse
            Command command = Command.interpretRequest(request);

            checkRequest();
            source = Integer.valueOf(request.getParameters().get("SRC"));
            timestamp = Integer.valueOf(request.getParameters().get("TIMESTAMP"));

            //Process timestamp
            lamportMutex.setAndIncTimeStamp(timestamp);

            close();


            command.execute(this);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lamportMutex.getTcpParallelServer().mutex.release();
        }



    }
}

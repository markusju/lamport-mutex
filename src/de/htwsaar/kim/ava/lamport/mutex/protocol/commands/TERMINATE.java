package de.htwsaar.kim.ava.lamport.mutex.protocol.commands;

import de.htwsaar.kim.ava.lamport.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.lamport.mutex.client.TCPClient;
import de.htwsaar.kim.ava.lamport.mutex.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.lamport.mutex.protocol.requests.AvaNodeProtocolRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by markus on 02.02.17.
 */
public class TERMINATE implements Command {
    @Override
    public String getMethodName() {
        return "TERMINATE";
    }

    @Override
    public void execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        protocol.getLamportMutex().getLamportProcess().terminate();
    }

    public static void startTERMINATE(TCPClient tcpClient, String host, int port) throws IOException {
        tcpClient.sendRequest(host, port, new AvaNodeProtocolRequest(
                "TERMINATE",
                new LinkedList<String>(),
                new HashMap<String, String>()
        ));
    }
}

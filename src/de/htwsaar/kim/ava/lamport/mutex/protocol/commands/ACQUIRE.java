package de.htwsaar.kim.ava.lamport.mutex.protocol.commands;

import de.htwsaar.kim.ava.lamport.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.lamport.mutex.QueueEntry;
import de.htwsaar.kim.ava.lamport.mutex.client.TCPClient;
import de.htwsaar.kim.ava.lamport.mutex.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.lamport.mutex.protocol.requests.AvaNodeProtocolRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by markus on 01.02.17.
 */
public class ACQUIRE implements Command {
    @Override
    public String getMethodName() {
        return "ACQUIRE";
    }

    @Override
    public void execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        protocol.getLamportMutex().getQueue().add(new QueueEntry(
                protocol.getLamportMutex().getCurrentTimestamp(),
                protocol.getSource()
        ));

        try {
            ACK.sendACK(protocol.getLamportMutex().getTcpClient(),
                    "127.0.0.1",
                    protocol.getSource()+5000
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendACQUIRE(TCPClient tcpClient, String host, int port, int timestamp) throws IOException {
        tcpClient.sendRequest(host, port, new AvaNodeProtocolRequest(
                "ACQUIRE",
                new LinkedList<String>(),
                new HashMap<String, String>() {{
                    put("TIMESTAMP", String.valueOf(timestamp));
                }}
        ));
    }
}

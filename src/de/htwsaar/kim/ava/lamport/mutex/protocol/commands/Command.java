package de.htwsaar.kim.ava.lamport.mutex.protocol.commands;


import de.htwsaar.kim.ava.lamport.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.lamport.mutex.protocol.AvaNodeProtocol;
import de.htwsaar.kim.ava.lamport.mutex.protocol.requests.Request;

public interface Command {


    String getMethodName();
    void execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException;


    static Command interpretRequest(Request request) {
        switch (request.getMethod()) {
            case "ACK":
                return new ACK();
            case "ACQUIRE":
                return new ACQUIRE();
            case "RELEASE":
                return new RELEASE();
            case "TERMINATE":
                return new TERMINATE();
            default:
                return new UNKNOWN();
        }
    }

}
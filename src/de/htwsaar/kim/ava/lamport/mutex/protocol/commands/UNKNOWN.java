package de.htwsaar.kim.ava.lamport.mutex.protocol.commands;

import de.htwsaar.kim.ava.lamport.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.lamport.mutex.protocol.AvaNodeProtocol;

/**
 * Created by markus on 26.12.16.
 */
public class UNKNOWN implements Command {


    @Override
    public String getMethodName() {
        return "UNKNOWN";
    }

    @Override
    public void execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {
        throw new CommandExecutionErrorException("Unknown Method");
    }
}

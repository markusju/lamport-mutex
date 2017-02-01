package de.htwsaar.kim.ava.lamport.mutex.protocol.commands;

import de.htwsaar.kim.ava.lamport.exception.CommandExecutionErrorException;
import de.htwsaar.kim.ava.lamport.mutex.protocol.AvaNodeProtocol;

/**
 * Created by markus on 01.02.17.
 */
public class RELEASE implements Command {
    @Override
    public String getMethodName() {
        return null;
    }

    @Override
    public void execute(AvaNodeProtocol protocol) throws CommandExecutionErrorException {

    }
}

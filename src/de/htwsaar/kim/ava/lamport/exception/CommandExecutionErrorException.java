package de.htwsaar.kim.ava.lamport.exception;

/**
 * Created by markus on 08.01.17.
 */
public class CommandExecutionErrorException extends Exception {
    public CommandExecutionErrorException (String message) {
        super(message);
    }

}

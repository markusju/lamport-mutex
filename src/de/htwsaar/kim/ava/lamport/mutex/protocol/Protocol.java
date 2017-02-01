package de.htwsaar.kim.ava.lamport.mutex.protocol;


import java.io.IOException;

/**
 * Interface which declares the protocol methods.
 *
 * Created by markus on 12.06.15.
 */
public interface Protocol {


    /**
     * Get the last line of text, which has been read from the socket.
     * @return the last line of text, which has been read from the socket.
     */
    String getCurrentLine();

    /**
     * Write to the Socket.
     * @param line to write to the socket.
     */
    void putLine(String line);


    void readLine() throws IOException;

    void close() throws IOException;


}

package de.htwsaar.kim.ava.lamport.mutex.protocol;

import java.io.*;
import java.net.Socket;

public abstract class AbstractBaseProtocol implements Protocol {

    /**
     * {@link BufferedReader} to read from the socket.
     */
    private final BufferedReader bufferedReader;

    /**
     * {@link OutputStream} to write on the socket.
     */
    private OutputStream output;

    /**
     * Current line read from the socket.
     */
    private String currentLine;

    private Socket socket;


    public AbstractBaseProtocol(Socket socket) throws IOException {
        this.bufferedReader =
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        this.output = socket.getOutputStream();
        this.socket = socket;

    }


    /**
     * Initiate AbstractBaseProtocol.
     */

    @Override
    public String getCurrentLine() {
        return this.currentLine;
    }


    @Override
    public void putLine(String line) {
        PrintWriter printWriter =
                new PrintWriter(
                        new OutputStreamWriter(this.output));
        printWriter.println(line);
        //System.out.print(line);
        printWriter.flush();
    }


    public void readLine() throws IOException {
            this.currentLine = bufferedReader.readLine();
            if (currentLine == null) {
                throw new IOException();
            }
    }


    public void close() throws IOException {
        this.socket.close();
    }
}
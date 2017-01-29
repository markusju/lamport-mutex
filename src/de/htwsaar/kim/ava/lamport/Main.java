package de.htwsaar.kim.ava.lamport;

import java.io.IOException;

/**
 * Created by markus on 29.01.17.
 */
public class Main {





    public static void main(String... args) throws IOException {
        LamportFile lmpFile = new LamportFile("file.txt");
        lmpFile.attachId(1);
        lmpFile.attachId(1);
        lmpFile.attachId(2);
        lmpFile.incrementValue();
    }
}

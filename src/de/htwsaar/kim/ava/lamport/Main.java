package de.htwsaar.kim.ava.lamport;

import de.htwsaar.kim.ava.lamport.file.LamportFile;
import de.htwsaar.kim.ava.lamport.process.LamportProcess;
import de.htwsaar.kim.ava.lamport.process.ProcessManager;

import java.io.IOException;

/**
 * Created by markus on 29.01.17.
 */
public class Main {


    public static void main(String... args) throws IOException {
        LamportFile lmpFile = new LamportFile("file.txt");
        ProcessManager manager = new ProcessManager();


        LamportProcess p1 = new LamportProcess(lmpFile, manager, 1);
        LamportProcess p2 = new LamportProcess(lmpFile, manager, 2);
        LamportProcess p3 = new LamportProcess(lmpFile, manager, 3);
        LamportProcess p4 = new LamportProcess(lmpFile, manager, 4);

        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);
        Thread t3 = new Thread(p3);
        Thread t4 = new Thread(p4);

        t1.start();
        t2.start();
        t3.start();
        t4.start();

    }
}

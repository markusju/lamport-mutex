package de.htwsaar.kim.ava.lamport.test;

import de.htwsaar.kim.ava.lamport.mutex.QueueEntry;

import java.util.TreeSet;

/**
 * Created by markus on 01.02.17.
 */
public class TreeSetQueueTest {




    public static void main(String ...args) {
        TreeSet<QueueEntry> queue = new TreeSet<>();

        queue.add(new QueueEntry(1,2));
        queue.add(new QueueEntry(1,3));

        System.out.println(queue);
    }




}

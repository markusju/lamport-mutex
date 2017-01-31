package de.htwsaar.kim.ava.lamport.process;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by markus on 31.01.17.
 */
public class ProcessManager {

    private HashMap<Integer, LamportProcess> map = new HashMap<>();


    public List<LamportProcess> getAll() {
        return new LinkedList<>(map.values());
    }

    public LamportProcess get(int id) {
        return map.get(id);
    }

    public void add(LamportProcess lamportProcess) {
        map.put(lamportProcess.getId(), lamportProcess);
    }
}

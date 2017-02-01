package de.htwsaar.kim.ava.lamport.mutex.protocol.requests;

import java.util.List;
import java.util.Map;

/**
 * Created by markus on 25.12.16.
 */
public interface Request {

    String getMethod();

    List<String> getMethodArguments();
    Map<String, String> getParameters();

    void addParameter(String key, String value);

    String toProtString();

}

package de.htwsaar.kim.ava.lamport.mutex.protocol.requests;


import de.htwsaar.kim.ava.lamport.exception.ClientErrorException;
import de.htwsaar.kim.ava.lamport.mutex.protocol.AvaNodeProtocol;

import java.io.IOException;
import java.util.*;

/**
 * Created by markus on 25.12.16.
 */
public class AvaNodeProtocolRequest implements Request {

    private AvaNodeProtocol protocol;

    private String method;
    private List<String> methodArguments = new LinkedList<>();
    private Map<String, String> parameters = new HashMap<>();

    public AvaNodeProtocolRequest(String method, List<String> methodArguments, Map<String, String> parameters) {
        this.method = method;
        this.methodArguments = methodArguments;
        this.parameters = parameters;
    }

    public AvaNodeProtocolRequest(AvaNodeProtocol avaNodeProtocol) throws IOException, ClientErrorException {
        protocol = avaNodeProtocol;

        protocol.readLine();

        //Method
        String[] methodParts = avaNodeProtocol.getCurrentLine().split(" ");

        if (methodParts.length < 1 || methodParts[0].equals(""))
            throw new ClientErrorException("No Method found");

        method = methodParts[0];

        methodArguments.addAll(Arrays.asList(methodParts));
        methodArguments.remove(0);

        protocol.readLine();

        //Parameters
        while (!terminatingCharsFound()) {
            String[] parameterParts =  protocol.getCurrentLine().split(": ");

            if (parameterParts.length < 2 || parameterParts[0].equals(""))
                throw new IOException("Malformatted Parameters!");

            parameters.put(parameterParts[0], parameterParts[1]);

            protocol.readLine();
        }
        return;

    }

    private boolean terminatingCharsFound() {
        return protocol.getCurrentLine().equals("");
    }


    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public List<String> getMethodArguments() {
        return methodArguments;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    @Override
    public String toProtString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getMethod()).append(" ").append(String.join(" ", getMethodArguments())).append("\n");
        for (Map.Entry<String, String> entry : getParameters().entrySet())
        {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }


        return sb.toString();
    }

}

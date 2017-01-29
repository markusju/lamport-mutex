package de.htwsaar.kim.ava.lamport;

/**
 * Created by markus on 29.01.17.
 */
public interface LamportInterProcCommInterface {

    void acquireRessource();
    void releaseResource();
    int getPartnerId();

}

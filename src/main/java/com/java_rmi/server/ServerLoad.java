package com.java_rmi.server;

import java.io.Serializable;


/**
 * This class represents the load information of a server.
 * It includes the current load and the size of the waiting list.
 */
public class ServerLoad implements Serializable {
    private int load;
    private int waitingList;

    // Constructor to initialize the ServerLoad object with load and waiting list values.
    public ServerLoad(int load, int waitingList) {
        this.load = load;
        this.waitingList = waitingList;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(int waitingList) {
        this.waitingList = waitingList;
    }
}

package com.java_rmi.server;

import java.io.Serializable;

public class ServerLoad implements Serializable {
    private int load;
    private int waitingList;

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


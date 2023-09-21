package com.java_rmi.server;

import java.io.Serializable;

public class ServerLoadInfo implements Serializable {
    private int load;
    private int waitingListSize;

    public ServerLoadInfo(int load, int waitingListSize) {
        this.load = load;
        this.waitingListSize = waitingListSize;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getWaitingListSize() {
        return waitingListSize;
    }

    public void setWaitingListSize(int waitingListSize) {
        this.waitingListSize = waitingListSize;
    }
}


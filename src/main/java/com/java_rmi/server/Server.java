package com.java_rmi.server;

import java.io.Serializable;

public class Server implements Serializable {
    private String serverName;
    private int zone;
    private int serverPort;
    private int load;
    private int waitingList;

    public Server(String serverName, int zone, int serverPort) {
        this.serverName = serverName;
        this.zone = zone;
        this.serverPort = serverPort;
        this.load = 0;
        this.waitingList = 0;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
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

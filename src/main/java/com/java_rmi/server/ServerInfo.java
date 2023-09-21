package com.java_rmi.server;

import java.io.Serializable;

public class ServerInfo implements Serializable {
    private String serverName;
    private int zone;
    private int serverPort;
    private int load;
    private int waitingListSize;

    public ServerInfo(String serverName, int zone, int serverPort) {
        this.serverName = serverName;
        this.zone = zone;
        this.serverPort = serverPort;
        this.load = 0; // Initialize load to 0
        this.waitingListSize = 0; // Initialize waiting list size to 0
    }

    // Getter and Setter methods

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

    public int getWaitingListSize() {
        return waitingListSize;
    }

    public void setWaitingListSize(int waitingListSize) {
        this.waitingListSize = waitingListSize;
    }

    // You can add additional methods and fields as needed
}

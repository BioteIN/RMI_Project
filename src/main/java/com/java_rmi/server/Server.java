package com.java_rmi.server;

import java.io.Serializable;

/**
 * The Server class represents contains information about the server.
 * including its name, zone, port, load, and waiting list status.
 */
public class Server implements Serializable {
    private String serverName;
    private int zone;
    private int serverPort;
    private int load;
    private int waitingList;

    /**
     * Constructs a new Server object with the specified server name, zone, and server port.
     * @param serverName The name of the server.
     * @param zone       The zone in which the server is located.
     * @param serverPort The port on which the server is listening.
     */
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

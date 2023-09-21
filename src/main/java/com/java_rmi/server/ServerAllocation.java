package com.java_rmi.server;

import java.io.Serializable;

/**
 * The ServerAllocation class represents information about a server's allocation, including its name and port.
 * It implements the Serializable interface to allow for easy serialization and deserialization of objects.
 */
public class ServerAllocation implements Serializable {
    private String serverName;
    private int serverPort;

    /**
     * Constructs a new ServerAllocation object with the specified server name and port.
     * @param serverName The name of the server.
     * @param serverPort The port on which the server is allocated.
     */
    public ServerAllocation(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }
    
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}

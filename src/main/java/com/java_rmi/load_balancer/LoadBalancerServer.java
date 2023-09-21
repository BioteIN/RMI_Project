package com.java_rmi.load_balancer;

import com.java_rmi.server.ServerAssignment;
import com.java_rmi.server.ServerInfo;
import com.java_rmi.server.ServerInterface;
import com.java_rmi.server.ServerLoadInfo;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class LoadBalancerServer extends UnicastRemoteObject implements LoadBalancerInterface {
    private List<ServerInfo> servers;
    private Map<String, Integer> serverRequestCounts; // To keep track of server request counts

    public LoadBalancerServer(List<ServerInfo> servers) throws RemoteException {
        super();
        this.servers = servers;
        this.serverRequestCounts = new HashMap<>();
        for (ServerInfo server : servers) {
            serverRequestCounts.put(server.getServerName(), 0);
        }
    }

    @Override
    public ServerAssignment requestServerAssignment(int clientZone) throws RemoteException {
        // Implement the server assignment logic based on the rules you provided
        ServerInfo selectedServer = selectServer(clientZone);

        // Increment the request count for the selected server
        incrementRequestCount(selectedServer.getServerName());

        return new ServerAssignment(selectedServer.getServerName(), selectedServer.getServerPort());
    }

    @Override
    public void updateServerLoad(String serverName, int load, int waitingListSize) throws RemoteException {
        // Update the load information of the specified server
        for (ServerInfo server : servers) {
            if (server.getServerName().equals(serverName)) {
                server.setLoad(load);
                server.setWaitingListSize(waitingListSize);

                // Fetch updated load information from the server
                ServerInterface serverStub = getServerStub(server);
                if (serverStub != null) {
                    ServerLoadInfo serverLoadInfo = serverStub.getServerLoadInfo();
                    server.setLoad(serverLoadInfo.getLoad());
                    server.setWaitingListSize(serverLoadInfo.getWaitingListSize());
                }

                break;
            }
        }
    }

    private ServerInfo selectServer(int clientZone) {
        // Implement your server selection logic based on the provided rules
        List<ServerInfo> availableServersInZone = new ArrayList<>();
        List<ServerInfo> availableServersInNeighborZone = new ArrayList<>();

        // Find available servers in the same zone and neighboring zones
        for (ServerInfo server : servers) {
            if (server.getZone() == clientZone && server.getLoad() < 18) {
                availableServersInZone.add(server);
            } else if (isNeighborZone(server.getZone(), clientZone) && server.getLoad() < 8) {
                availableServersInNeighborZone.add(server);
            }
        }

        // Priority: Same zone servers > Neighbor zone servers > Random server in same zone
        if (!availableServersInZone.isEmpty()) {
            return getRandomServer(availableServersInZone);
        } else if (!availableServersInNeighborZone.isEmpty()) {
            return getRandomServer(availableServersInNeighborZone);
        } else {
            // If no available servers, select a random server in the same zone
            List<ServerInfo> serversInClientZone = getServersInZone(clientZone);
            return getRandomServer(serversInClientZone);
        }
    }

    private boolean isNeighborZone(int serverZone, int clientZone) {
        // Determine if the server's zone is a neighbor of the client's zone
        return Math.abs(serverZone - clientZone) <= 2;
    }

    private List<ServerInfo> getServersInZone(int zone) {
        List<ServerInfo> serversInZone = new ArrayList<>();
        for (ServerInfo server : servers) {
            if (server.getZone() == zone) {
                serversInZone.add(server);
            }
        }
        return serversInZone;
    }

    private ServerInfo getRandomServer(List<ServerInfo> servers) {
        if (!servers.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(servers.size());
            return servers.get(randomIndex);
        }
        return null;
    }

    private void incrementRequestCount(String serverName) {
        int count = serverRequestCounts.get(serverName) + 1;
        serverRequestCounts.put(serverName, count);

        // After every 18 requests, update the server load
        if (count % 18 == 0) {
            for (ServerInfo server : servers) {
                if (server.getServerName().equals(serverName)) {
                    try {
                        // Invoke a remote call to the server to fetch updated load information
                        ServerInterface serverStub = getServerStub(server);
                        if (serverStub != null) {
                            ServerLoadInfo serverLoadInfo = serverStub.getServerLoadInfo();
                            server.setLoad(serverLoadInfo.getLoad());
                            server.setWaitingListSize(serverLoadInfo.getWaitingListSize());
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    private ServerInterface getServerStub(ServerInfo server) {
        try {
            Registry registry = LocateRegistry.getRegistry(server.getServerPort());
            return (ServerInterface) registry.lookup(server.getServerName());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

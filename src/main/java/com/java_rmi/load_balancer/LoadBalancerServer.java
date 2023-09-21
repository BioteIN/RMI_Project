package com.java_rmi.load_balancer;

import com.java_rmi.server.ServerAllocation;
import com.java_rmi.server.Server;
import com.java_rmi.server.ServerInterface;
import com.java_rmi.server.ServerLoad;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class LoadBalancerServer extends UnicastRemoteObject implements LoadBalancerInterface {
    private List<Server> servers;
    private Map<String, Integer> serverRequest; // To keep track of server request counts

    public LoadBalancerServer(List<Server> servers) throws RemoteException {
        super();
        this.servers = servers;
        this.serverRequest = new HashMap<>();
        for (Server server : servers) {
            serverRequest.put(server.getServerName(), 0);
        }
    }

    @Override
    public ServerAllocation requestServerAllocation(int zone) throws RemoteException {
        // Implement the server assignment logic based on the rules you provided
        Server selectedServer = selectServer(zone);
        // Increment the request count for the selected server
        increaseRequest(selectedServer.getServerName());

        return new ServerAllocation(selectedServer.getServerName(), selectedServer.getServerPort());
    }

    @Override
    public void updateServerLoad(String serverName, int load, int waitingList) throws RemoteException {
        // Update the load information of the specified server
        for (Server server : servers) {
            if (server.getServerName().equals(serverName)) {
                server.setLoad(load);
                server.setWaitingList(waitingList);
                // Fetch updated load information from the server
                ServerInterface serverStub = getServerStub(server);
                if (serverStub != null) {
                    ServerLoad serverLoad = serverStub.getServerLoadInfo();
                    server.setLoad(serverLoad.getLoad());
                    server.setWaitingList(serverLoad.getWaitingList());
                }
                break;
            }
        }
    }

    private Server selectServer(int zone) {
        // Implement your server selection logic based on the provided rules
        List<Server> availableZoneServers = new ArrayList<>();
        List<Server> availableNeighborZoneServers = new ArrayList<>();

        // Find available servers in the same zone and neighboring zones
        for (Server server : servers) {
            if (server.getZone() == zone && server.getLoad() < 18) {
                availableZoneServers.add(server);
            } else if (nearbyZone(server.getZone(), zone) && server.getLoad() < 8) {
                availableNeighborZoneServers.add(server);
            }
        }
        // Priority: Same zone servers > Neighbor zone servers > Random server in same zone
        if (!availableZoneServers.isEmpty()) {
            return getRandomServer(availableZoneServers);
        } else if (!availableNeighborZoneServers.isEmpty()) {
            return getRandomServer(availableNeighborZoneServers);
        } else {
            // If no available servers, select a random server in the same zone
            List<Server> serversZone = getServersZone(zone);
            return getRandomServer(serversZone);
        }
    }

    private boolean nearbyZone(int serverZone, int zone) {
        // Determine if the server's zone is a neighbor of the client's zone
        return Math.abs(serverZone - zone) <= 2;
    }

    private List<Server> getServersZone(int zone) {
        List<Server> serversZone = new ArrayList<>();
        for (Server server : servers) {
            if (server.getZone() == zone) {
                serversZone.add(server);
            }
        }
        return serversZone;
    }

    private Server getRandomServer(List<Server> servers) {
        if (!servers.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(servers.size());
            return servers.get(randomIndex);
        }
        return null;
    }

    private void increaseRequest(String serverName) {
        int count = serverRequest.get(serverName) + 1;
        serverRequest.put(serverName, count);
        // After every 18 requests, update the server load
        if (count % 18 == 0) {
            for (Server server : servers) {
                if (server.getServerName().equals(serverName)) {
                    try {
                        // Invoke a remote call to the server to fetch updated load information
                        ServerInterface serverStub = getServerStub(server);
                        if (serverStub != null) {
                            ServerLoad serverLoad = serverStub.getServerLoadInfo();
                            server.setLoad(serverLoad.getLoad());
                            server.setWaitingList(serverLoad.getWaitingList());
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        System.out.println("Request count error!!!");
                    }
                    break;
                }
            }
        }
    }

    private ServerInterface getServerStub(Server server) {
        try {
            Registry registry = LocateRegistry.getRegistry(server.getServerPort());
            return (ServerInterface) registry.lookup(server.getServerName());
        } catch (Exception e) {
            System.out.println("Error when getting Server Stub");
            return null;
        }
    }
}

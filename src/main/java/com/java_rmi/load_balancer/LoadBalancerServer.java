package com.java_rmi.load_balancer;

import com.java_rmi.server.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * The LoadBalancerServer class implements the LoadBalancerInterface and represents the load balancer server
 * responsible for distributing client requests to available servers based on certain rules and maintaining
 * load information.
 */
public class LoadBalancerServer extends UnicastRemoteObject implements LoadBalancerInterface {
    private List<Server> servers;
    private Map<String, Integer> serverRequest;

    /**
     * Constructor for the LoadBalancerServer class.
     * @param servers A list of available servers to be managed by the load balancer.
     */
    public LoadBalancerServer(List<Server> servers) throws RemoteException {
        super();
        this.servers = servers;
        this.serverRequest = new HashMap<>();

        // Initialize request counts for each server
        for (Server server : servers) {
            serverRequest.put(server.getServerName(), 0);
        }
    }
    public LoadBalancerServer() throws RemoteException {


    }

    /**
     * Implements the server allocation logic based on specific rules and selects an appropriate server to
     * handle a client request.
     * @param zone The client's zone for which a server is requested.
     * @return A ServerAllocation object containing the selected server's name and port.
     */
    @Override
    public ServerAllocation requestServerAllocation(int zone) throws RemoteException {
        // Implement the server assignment logic based on the rules
        Server selectedServer = selectServer(zone);
        // Increment the request count for the selected server
        increaseRequest(selectedServer.getServerName());

        return new ServerAllocation(selectedServer.getServerName(), selectedServer.getServerPort());
    }

    /**
     * Selects an appropriate server based on specific rules.
     * @param zone The client's zone for which a server is requested.
     * @return The selected server.
     */
    private Server selectServer(int zone) {
        List<Server> availableZoneServers = new ArrayList<>();
        List<Server> availableNeighborZoneServers = new ArrayList<>();

        // Find available servers in the same zone and nearby zones
        for (Server server : servers) {
            if (server.getZone() == zone && server.getLoad() < 18) {
                availableZoneServers.add(server);
            } else if (nearbyZone(server.getZone(), zone) && server.getLoad() < 8) {
                availableNeighborZoneServers.add(server);
            }
        }

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

    /**
     * Checks if a server's zone is a neighbor of the client's zone.
     * @param serverZone The zone of the server.
     * @param zone       The client's zone.
     * @return True if the server's zone is a neighbor of the client's zone, false otherwise.
     */
    private boolean nearbyZone(int serverZone, int zone) {
        return Math.abs(serverZone - zone) <= 2;
    }

    /**
     * Retrieves a list of servers in the specified zone.
     * @param zone The zone for which servers are to be retrieved.
     * @return A list of servers in the specified zone.
     */
    private List<Server> getServersZone(int zone) {
        List<Server> serversZone = new ArrayList<>();
        for (Server server : servers) {
            if (server.getZone() == zone) {
                serversZone.add(server);
            }
        }
        return serversZone;
    }

    /**
     * Selects a random server from a list of servers.
     * @param servers The list of servers from which to select a random server.
     * @return A randomly selected server, or null if the list is empty.
     */
    private Server getRandomServer(List<Server> servers) {
        if (!servers.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(servers.size());
            return servers.get(randomIndex);
        }
        return null;
    }

    /**
     * Increases the request count for a server and updates the server load if a threshold is reached.
     * After every 18 requests, update the server load
     * @param serverName The name of the server.
     */
    /** If the server in that specific zone is overloaded with client requests
       (equal or more than 18 requests in the waiting list), the load balancing server tries servers in
       the next two zones (if server in zone 1 is overloaded, then load balancer will try servers in zone
       2 and zone 3) to find a server with less than 8 requests in the waiting list.*/
    private void increaseRequest(String serverName) {
        int count = serverRequest.get(serverName) + 1;
        serverRequest.put(serverName, count);

        if (count % 18 == 0) {
            for (Server server : servers) {
                if (server.getServerName().equals(serverName)) {
                    try {
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

    /**
     * Gets the RMI server stub for a given server.
     * @param server The server for which to retrieve the stub.
     * @return The RMI server stub or null if an error occurs.
     */
    private ServerInterface getServerStub(Server server) {
        try {
            Registry registry = LocateRegistry.getRegistry(server.getServerPort());
            return (ServerInterface) registry.lookup(server.getServerName());
        } catch (Exception e) {
            return null;
        }
    }

    public void loadBalancerStarter(){
        try {
            // Create a list of server information
            List<Server> servers = new ArrayList<>();
            servers.add(new Server("Server1", 1, 1098));
            servers.add(new Server("Server2", 2, 1097));
            servers.add(new Server("Server3", 3, 1096));
            servers.add(new Server("Server4", 4, 1095));
            servers.add(new Server("Server5", 5, 1094));

            // Create an instance of the load balancer server with the list of servers
            LoadBalancerServer loadBalancer = new LoadBalancerServer(servers);

            // Start the load balancer server in a new thread
            Thread loadBalancerThread = new Thread(() -> {
                try {
                    // Create the RMI registry for the LoadBalancerServer
                    Registry registry = LocateRegistry.createRegistry(1099);
                    // Bind the LoadBalancerServer to the RMI registry with the name "LoadBalancer"
                    registry.bind("LoadBalancer", loadBalancer);

                    // Bind the Server to the RMI registry with the name "LoadServer"
                    ServerImplementation server = new ServerImplementation();
                    registry.bind("LoadServer", server);

                    for(Server server1: servers){
                        System.out.println("Server name: "+server1.getServerName()+", Zone: "+server1.getZone()+", Port: "+server1.getServerPort());
                    }
                    System.out.println();
                    System.out.println("Successfully created servers using LoadBalancer");
                } catch (Exception e) {
                    System.out.println("Error creating servers, due to servers started!!!");
                }
            });
            loadBalancerThread.start();

        } catch (Exception e) {
            System.out.println("Some error when starting servers!!!");
        }
    }


}

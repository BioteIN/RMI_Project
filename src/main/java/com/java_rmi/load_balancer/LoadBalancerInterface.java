package com.java_rmi.load_balancer;

import com.java_rmi.server.ServerAllocation;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The LoadBalancerInterface represents the remote interface for the load balancer.
 * It defines the methods that can be invoked remotely by clients and servers.
 */
public interface LoadBalancerInterface extends Remote {
    /**
     * Requests the allocation of a server based on the client's zone.
     * @param clientZone The zone of the client requesting a server.
     * @return A ServerAllocation object representing the allocated server's information.
     * @throws RemoteException If there is an issue with RMI communication.
     */
    ServerAllocation requestServerAllocation(int clientZone) throws RemoteException;

}


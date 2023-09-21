package com.java_rmi.load_balancer;

import com.java_rmi.server.ServerAllocation;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancerInterface extends Remote {
    ServerAllocation requestServerAllocation(int clientZone) throws RemoteException;
    void updateServerLoad(String serverName, int load, int waitingList) throws RemoteException;
}


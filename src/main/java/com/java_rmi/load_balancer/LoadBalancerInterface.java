package com.java_rmi.load_balancer;

import com.java_rmi.server.ServerAssignment;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancerInterface extends Remote {
    ServerAssignment requestServerAssignment(int clientZone) throws RemoteException;
    void updateServerLoad(String serverName, int load, int waitingListSize) throws RemoteException;
}


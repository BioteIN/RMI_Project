package com.java_rmi;

import com.java_rmi.client.Client;
import com.java_rmi.load_balancer.LoadBalancerServer;

import javax.swing.*;
import java.rmi.RemoteException;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args ) throws RemoteException {
        LoadBalancerServer loadBalancerServer =new LoadBalancerServer();
        Client clientSide = new Client();

        int confirmLoadBalancerStart = JOptionPane.showConfirmDialog(null, "Start the load balancer.");

        if (confirmLoadBalancerStart==0){
            loadBalancerServer.loadBalancerStarter();

            JOptionPane.showMessageDialog(null, "Successfully created servers using LoadBalancer.");
            int confirmClientStart= JOptionPane.showConfirmDialog(null, "Start the Client-Side.");
            if (confirmClientStart==0){
                clientSide.clientStarter();
            }
            else if (confirmClientStart ==1){
                JOptionPane.showMessageDialog(null,"Client-Side not started.");
            }
        }
        else if (confirmLoadBalancerStart ==1){
            JOptionPane.showMessageDialog(null,"The application will not start due to servers down.");
        }
    }
}

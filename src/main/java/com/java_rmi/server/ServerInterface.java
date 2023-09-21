package com.java_rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    long getPopulationOfCountry(String countryName) throws RemoteException;

    int getNumberOfCities(String countryName, int minPopulation) throws RemoteException;

    int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException;

    int getNumberOfCountries(int cityCount, int minPopulation, int maxPopulation) throws RemoteException;

    ServerLoad getServerLoadInfo() throws RemoteException;
}

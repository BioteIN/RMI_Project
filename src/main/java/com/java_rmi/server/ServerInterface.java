package com.java_rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The ServerInterface interface defines the remote methods that can be invoked on an RMI server.
 * All methods in this interface throw RemoteException to handle remote communication errors.
 */
public interface ServerInterface extends Remote {
    /**
     * Retrieves the population of a country by country name.
     * @param countryName the name of the country.
     * @return the population of the specified country.
     */
    long getPopulationOfCountry(String countryName) throws RemoteException;

    /**
     * Retrieves the number of cities in a country with a minimum population.
     * @param countryName   the name of the country.
     * @param minPopulation the minimum population required for a city to be counted.
     * @return the number of cities in the specified country.
     */
    int getNumberOfCities(String countryName, int minPopulation) throws RemoteException;

    /**
     * Retrieves the number of countries that meet specific criteria: a minimum population and a minimum number of cities.
     * @param cityCount     the minimum number of cities required.
     * @param minPopulation the minimum population required for a country to be counted.
     * @return the number of countries that meet the criteria.
     */
    int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException;

    /**
     * Retrieves the number of countries that meet criteria including a population range and a minimum number of cities.
     * @param cityCount     the minimum number of cities required.
     * @param minPopulation the minimum population required for a country to be counted.
     * @param maxPopulation the maximum population required for a country to be counted.
     * @return the number of countries that meet the criteria.
     */
    int getNumberOfCountries(int cityCount, int minPopulation, int maxPopulation) throws RemoteException;

    /**
     * Retrieves server load information.
     * @return a ServerLoad object containing load and waiting list information.
     */
    ServerLoad getServerLoadInfo() throws RemoteException;
}

package com.java_rmi.server;

import com.java_rmi.data_conector.GeoData;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.java_rmi.data_conector.CSVReader.readDataCSV;

/**
 * This class represents the implementation of the RMI server. It provides methods for
 * retrieving population, city, and country information, along with load balancing.
 */
public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {
    private Map<String, Long> populationCache; // Cache for getPopulationOfCountry results
    private Map<String, Integer> cityCache; // Cache for getNumberOfCities results
    private Map<String, Integer> countryCache; // Cache for getNumberOfCountries results
    private int load;
    private int waitingList;

    /**
     * @throws RemoteException if there is an issue with RMI.
     */
    public ServerImplementation() throws RemoteException {
        super();
        this.populationCache = new LinkedHashMap<>(150, 0.75f, true);
        this.cityCache = new LinkedHashMap<>(150, 0.75f, true);
        this.countryCache = new LinkedHashMap<>(150, 0.75f, true);
        this.load = 0;
        this.waitingList = 0;
    }

    /**
     * Retrieves the population of a country by country name.
     * @param countryName the name of the country.
     * @return the population of the specified country.
     */
    public long getPopulationOfCountry(String countryName) throws RemoteException {
        // Check if the result is in the cache
        if (populationCache.containsKey(countryName)) {
            return populationCache.get(countryName);
        }

        // If not found in cache, perform the operation and add to cache.
        long sum = 0;
        for (GeoData geoData : readDataCSV()) {
            if (geoData.getCountryNameEn().equalsIgnoreCase(countryName)) {
                sum += geoData.getPopulation();
            }
        }

        populationCache.put(countryName, sum);
        return sum;
    }

    /**
     * Retrieves the number of cities in a country with a minimum population.
     * @param countryName   the name of the country.
     * @param minPopulation the minimum population required for a city to be counted.
     * @return the number of cities in the specified country.
     */
    public int getNumberOfCities(String countryName, int minPopulation) throws RemoteException {
        // Create a cache key based on the method parameters
        String cacheKey = "getNumberOfCities:" + countryName + ":" + minPopulation;

        if (cityCache.containsKey(cacheKey)) {
            return cityCache.get(cacheKey);
        }

        // If not found in cache, perform the operation and add to cache
        int cityCount = 0;
        for (GeoData geoData : readDataCSV()) {
            if (geoData.getCountryNameEn().equalsIgnoreCase(countryName) && geoData.getPopulation() >= minPopulation) {
                cityCount++;
            }
        }

        cityCache.put(cacheKey, cityCount);
        return cityCount;
    }

    /**
     * Retrieves the number of countries with a minimum population and a minimum number of cities.
     * @param cityCount     the minimum number of cities required.
     * @param minPopulation the minimum population required for a country to be counted.
     * @return the number of countries that meet the criteria.
     */
    public int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException {
        // Create a cache key based on the method parameters
        String cacheKey = "getNumberOfCountries:" + cityCount + ":" + minPopulation;

        // Check if the result is in the cache
        if (countryCache.containsKey(cacheKey)) {
            return countryCache.get(cacheKey);
        }

        int countryCount = 0;
        for (GeoData geoData : readDataCSV()) {
            if (geoData.getPopulation() >= minPopulation) {
                if (getNumberOfCities(geoData.getCountryNameEn(), minPopulation) >= cityCount) {
                    countryCount++;
                }
            }
        }

        countryCache.put(cacheKey, countryCount);
        return countryCount;
    }

    /**
     * Retrieves the number of countries within a population range and with a minimum number of cities.
     * @param cityCount     the minimum number of cities required.
     * @param minPopulation the minimum population required for a country to be counted.
     * @param maxPopulation the maximum population required for a country to be counted.
     * @return the number of countries that meet the criteria.
     */
    public int getNumberOfCountries(int cityCount, int minPopulation, int maxPopulation) throws RemoteException {
        // Create a cache key based on the method parameters
        String cacheKey = "getNumberOfCountries:" + cityCount + ":" + minPopulation + ":" + maxPopulation;

        // Check if the result is in the cache
        if (countryCache.containsKey(cacheKey)) {
            return countryCache.get(cacheKey);
        }

        // If not found in cache, perform the operation and add to cache
        int countryCount = 0;
        for (GeoData geoData : readDataCSV()) {
            long population = geoData.getPopulation();
            if (population >= minPopulation && population <= maxPopulation) {
                if (getNumberOfCities(geoData.getCountryNameEn(), minPopulation) >= cityCount) {
                    countryCount++;
                }
            }
        }
        countryCache.put(cacheKey, countryCount);
        return countryCount;
    }

    /**
     * Retrieves server load information.
     * @return a ServerLoad object containing load and waiting list information.
     * @throws RemoteException if there is an issue with RMI.
     */
    @Override
    public ServerLoad getServerLoadInfo() throws RemoteException {
        // Return the actual load and waiting list size of the server
        return new ServerLoad(load, waitingList);
    }

}

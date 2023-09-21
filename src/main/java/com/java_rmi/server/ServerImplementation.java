package com.java_rmi.server;

import com.java_rmi.data_conector.GeoData;
import com.java_rmi.load_balancer.LoadBalancerServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
     * Constructor for ServerImplement. Initializes caches and other instance variables.
     *
     * @throws RemoteException if there is an issue with RMI.
     */
    public ServerImplementation() throws RemoteException {
        super();
        this.populationCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for population
        this.cityCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for city counts
        this.countryCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for country counts
        this.load = 0;
        this.waitingList = 0;
    }

    /**
     * Retrieves the population of a country by country name.
     *
     * @param countryName the name of the country.
     * @return the population of the specified country.
     * @throws RemoteException if there is an issue with RMI.
     */
    public long getPopulationOfCountry(String countryName) throws RemoteException {
        // Check if the result is in the cache
        if (populationCache.containsKey(countryName)) {
            return populationCache.get(countryName);
        }

        // If not found in cache, perform the operation and add to cache
        long sum = 0;
        for (GeoData geoData : readDataCSV()) {
            if (geoData.getCountryNameEn().equalsIgnoreCase(countryName)) {
                sum += geoData.getPopulation();
            }
        }

        // Add the result to the cache
        populationCache.put(countryName, sum);
        return sum;
    }

    /**
     * Retrieves the number of cities in a country with a minimum population.
     *
     * @param countryName   the name of the country.
     * @param minPopulation the minimum population required for a city to be counted.
     * @return the number of cities in the specified country.
     * @throws RemoteException if there is an issue with RMI.
     */
    public int getNumberOfCities(String countryName, int minPopulation) throws RemoteException {
        // Create a cache key based on the method parameters
        String cacheKey = "getNumberOfCities:" + countryName + ":" + minPopulation;

        // Check if the result is in the cache
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

        // Add the result to the cache
        cityCache.put(cacheKey, cityCount);
        return cityCount;
    }

    /**
     * Retrieves the number of countries with a minimum population and a minimum number of cities.
     *
     * @param cityCount     the minimum number of cities required.
     * @param minPopulation the minimum population required for a country to be counted.
     * @return the number of countries that meet the criteria.
     * @throws RemoteException if there is an issue with RMI.
     */
    public int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException {
        // Create a cache key based on the method parameters
        String cacheKey = "getNumberOfCountries:" + cityCount + ":" + minPopulation;

        // Check if the result is in the cache
        if (countryCache.containsKey(cacheKey)) {
            return countryCache.get(cacheKey);
        }

        // If not found in cache, perform the operation and add to cache
        int countryCount = 0;
        for (GeoData geoData : readDataCSV()) {
            if (geoData.getPopulation() >= minPopulation) {
                if (getNumberOfCities(geoData.getCountryNameEn(), minPopulation) >= cityCount) {
                    countryCount++;
                }
            }
        }

        // Add the result to the cache
        countryCache.put(cacheKey, countryCount);
        return countryCount;
    }

    /**
     * Retrieves the number of countries within a population range and with a minimum number of cities.
     *
     * @param cityCount     the minimum number of cities required.
     * @param minPopulation the minimum population required for a country to be counted.
     * @param maxPopulation the maximum population required for a country to be counted.
     * @return the number of countries that meet the criteria.
     * @throws RemoteException if there is an issue with RMI.
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
        // Add the result to the cache
        countryCache.put(cacheKey, countryCount);
        return countryCount;
    }

    /**
     * Retrieves server load information.
     *
     * @return a ServerLoad object containing load and waiting list information.
     * @throws RemoteException if there is an issue with RMI.
     */
    @Override
    public ServerLoad getServerLoadInfo() throws RemoteException {
        // Return the actual load and waiting list size of the server
        return new ServerLoad(load, waitingList);
    }

    /**
     * Main method for starting the RMI server and load balancer.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            // Create a list of server information (replace with your own information)
            List<Server> servers = new ArrayList<>();
            servers.add(new Server("Server1", 1, 1098)); // Example server information
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
                    ServerImplementation server = new ServerImplementation();
                    registry.bind("LoadBalancer", server);
                    // Rebind the LoadBalancerServer to the RMI registry with the name "LoadBalancer"
                    registry.rebind("LoadBalancer", loadBalancer);

                    System.out.println("Successfully created servers using LoadBalancer");
                } catch (Exception e) {
                    System.out.println("Error creating servers!!!");
                }
            });
            loadBalancerThread.start();

        } catch (Exception e) {
            System.out.println("Some error when starting servers!!!");
        }
    }
}

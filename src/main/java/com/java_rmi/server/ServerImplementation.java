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

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {
    private Map<String, Long> populationCache; // Cache for getPopulationOfCountry results
    private Map<String, Integer> cityCache; // Cache for getNumberOfCities results
    private Map<String, Integer> countryCache; // Cache for getNumberOfCountries results
    private int load;
    private int waitingList;

    public ServerImplementation() throws RemoteException {
        super();
        this.populationCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for population
        this.cityCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for city counts
        this.countryCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for country counts
        this.load = 0;
        this.waitingList = 0;

    }

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

    @Override
    public ServerLoad getServerLoadInfo() throws RemoteException {
        // Return the actual load and waiting list size of the server
        return new ServerLoad(load, waitingList);
    }

    public static void main(String[] args) {
        try {
            // Crie uma lista de informações de servidor (substitua com suas próprias informações)
            List<Server> servers = new ArrayList<>();
            servers.add(new Server("Server1", 1, 1098)); // Exemplo de informações de servidor
            servers.add(new Server("Server2", 2, 1097));
            servers.add(new Server("Server3", 3, 1096));
            servers.add(new Server("Server4", 4, 1095));
            servers.add(new Server("Server5", 5, 1094));

            // Crie uma instância do servidor de balanceamento de carga com a lista de servidores
            LoadBalancerServer loadBalancer = new LoadBalancerServer(servers);

            // Inicie o servidor de balanceamento de carga em uma nova thread
            Thread loadBalancerThread = new Thread(() -> {
                try {
                    // Crie o registro RMI para o LoadBalancerServer
                    Registry registry = LocateRegistry.createRegistry(1099);
                    ServerImplementation server = new ServerImplementation();
                    registry.bind("LoadBalancer", server);
                    // Registre o LoadBalancerServer no registro RMI com o nome "LoadBalancer"
                    registry.rebind("LoadBalancer", loadBalancer);

                    System.out.println("Successfully created servers using LoadBalancer");
                } catch (Exception e) {
                    System.out.println("Error creating servers!!!");
                }
            });
            loadBalancerThread.start();

        } catch (Exception e) {
            System.out.println("Some error when starting servers!!!");;
        }
    }

}
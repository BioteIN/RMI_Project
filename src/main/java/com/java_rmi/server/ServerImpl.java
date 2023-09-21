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

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
    private Map<String, Long> populationCache; // Cache for getPopulationOfCountry results
    private Map<String, Integer> cityCountCache; // Cache for getNumberOfCities results
    private Map<String, Integer> countryCountCache; // Cache for getNumberOfCountries results
    private int load;
    private int waitingListSize;

    public ServerImpl() throws RemoteException {
        super();
        this.populationCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for population
        this.cityCountCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for city counts
        this.countryCountCache = new LinkedHashMap<>(150, 0.75f, true); // LRU Cache for country counts
        this.load = 0;
        this.waitingListSize = 0;

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
        if (cityCountCache.containsKey(cacheKey)) {
            return cityCountCache.get(cacheKey);
        }

        // If not found in cache, perform the operation and add to cache
        int cityCount = 0;
        for (GeoData geoData : readDataCSV()) {
            if (geoData.getCountryNameEn().equalsIgnoreCase(countryName) && geoData.getPopulation() >= minPopulation) {
                cityCount++;
            }
        }

        // Add the result to the cache
        cityCountCache.put(cacheKey, cityCount);
        return cityCount;
    }

    public int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException {
        // Create a cache key based on the method parameters
        String cacheKey = "getNumberOfCountries:" + cityCount + ":" + minPopulation;

        // Check if the result is in the cache
        if (countryCountCache.containsKey(cacheKey)) {
            return countryCountCache.get(cacheKey);
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
        countryCountCache.put(cacheKey, countryCount);
        return countryCount;
    }

    public int getNumberOfCountries(int cityCount, int minPopulation, int maxPopulation) throws RemoteException {
        // Create a cache key based on the method parameters
        String cacheKey = "getNumberOfCountries:" + cityCount + ":" + minPopulation + ":" + maxPopulation;

        // Check if the result is in the cache
        if (countryCountCache.containsKey(cacheKey)) {
            return countryCountCache.get(cacheKey);
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
        countryCountCache.put(cacheKey, countryCount);
        return countryCount;
    }

    @Override
    public ServerLoadInfo getServerLoadInfo() throws RemoteException {
        // Return the actual load and waiting list size of the server
        return new ServerLoadInfo(load, waitingListSize);
    }

    public static void main(String[] args) {
        try {
            // Crie uma lista de informações de servidor (substitua com suas próprias informações)
            List<ServerInfo> servers = new ArrayList<>();
            servers.add(new ServerInfo("Server1", 1, 1098)); // Exemplo de informações de servidor
            servers.add(new ServerInfo("Server2", 2, 1097));
            servers.add(new ServerInfo("Server3", 3, 1096));
            servers.add(new ServerInfo("Server4", 4, 1095));
            servers.add(new ServerInfo("Server5", 5, 1094));
            // Adicione informações de servidor para outros servidores...

            // Crie uma instância do servidor de balanceamento de carga com a lista de servidores
            LoadBalancerServer loadBalancer = new LoadBalancerServer(servers);

            // Inicie o servidor de balanceamento de carga em uma nova thread
            Thread loadBalancerThread = new Thread(() -> {
                try {
                    // Crie o registro RMI para o LoadBalancerServer
                    Registry registry = LocateRegistry.createRegistry(1099);
                    ServerImpl server = new ServerImpl();
                registry.bind("LoadBalancer", server);

                    // Registre o LoadBalancerServer no registro RMI com o nome "LoadBalancer"
                    registry.rebind("LoadBalancer", loadBalancer);

                    System.out.println("Load Balancer is ready.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            loadBalancerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


//import com.java_rmi.data_conector.GeoData;
//import com.java_rmi.load_balancer.LoadBalancerServer;
//
//import java.rmi.RemoteException;
//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.rmi.server.UnicastRemoteObject;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.java_rmi.data_conector.CSVReader.readDataCSV;
//
//public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
//    private int load;
//    private int waitingListSize;
//    // Constructor
//    public ServerImpl() throws RemoteException {
//        super();
//        // Initialize load and waitingListSize to some default values
//        this.load = 0;
//        this.waitingListSize = 0;
//    }
//
//    // Methods to fetch and manipulate data from the CSV data
//
//    public long getPopulationOfCountry(String countryName) throws RemoteException {
//        long sum = 0;
//        for (GeoData geoData : readDataCSV()) {
//            if (geoData.getCountryNameEn().equalsIgnoreCase(countryName)) {
//                sum = sum + geoData.getPopulation();
//            }
//        }
//        if (sum != 0) {
//            return sum;
//        } else {
//            return -1;
//        }
//    }
//
//    public int getNumberOfCities(String countryName, int minPopulation) throws RemoteException {
//        int cityCount = 0;
//        for (GeoData geoData : readDataCSV()) {
//            if (geoData.getCountryNameEn().equalsIgnoreCase(countryName) && geoData.getPopulation() >= minPopulation) {
//                cityCount++;
//            }
//        }
//        return cityCount;
//    }
//
//    public int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException {
//        int countryCount = 0;
//        for (GeoData geoData : readDataCSV()) {
//
//            if (geoData.getPopulation() >= minPopulation) {
//                if (getNumberOfCities(geoData.getCountryNameEn(), minPopulation) >= cityCount) {
//                    countryCount++;
//                }
//
//            }
//        }
//        return countryCount;
//    }
//
//    public int getNumberOfCountries(int cityCount, int minPopulation, int maxPopulation) throws RemoteException {
//        int countryCount = 0;
//        for (GeoData geoData : readDataCSV()) {
//            if (geoData.getPopulation() >= minPopulation && geoData.getPopulation() <= maxPopulation) {
//                if (getNumberOfCities(geoData.getCountryNameEn(), minPopulation) >= cityCount) {
//                    countryCount++;
//                }
//            }
//        }
//        return countryCount;
//    }
//
//    @Override
//    public ServerLoadInfo getServerLoadInfo() throws RemoteException {
//        // Return the actual load and waiting list size of the server
//        return new ServerLoadInfo(load, waitingListSize);
//    }
//
//    public static void main(String[] args) {
//        try {
//            // Crie uma lista de informações de servidor (substitua com suas próprias informações)
//            List<ServerInfo> servers = new ArrayList<>();
//            servers.add(new ServerInfo("Server1", 1, 1098)); // Exemplo de informações de servidor
//            servers.add(new ServerInfo("Server2", 2, 1097));
//            servers.add(new ServerInfo("Server3", 3, 1096));
//            servers.add(new ServerInfo("Server4", 4, 1095));
//            servers.add(new ServerInfo("Server5", 5, 1094));
//            // Adicione informações de servidor para outros servidores...
//
//            // Crie uma instância do servidor de balanceamento de carga com a lista de servidores
//            LoadBalancerServer loadBalancer = new LoadBalancerServer(servers);
//
//            // Inicie o servidor de balanceamento de carga em uma nova thread
//            Thread loadBalancerThread = new Thread(() -> {
//                try {
//                    // Crie o registro RMI para o LoadBalancerServer
//                    Registry registry = LocateRegistry.createRegistry(1099);
//                    ServerImpl server = new ServerImpl();
//                registry.bind("LoadBalancer", server);
//
//                    // Registre o LoadBalancerServer no registro RMI com o nome "LoadBalancer"
//                    registry.rebind("LoadBalancer", loadBalancer);
//
//                    System.out.println("Load Balancer is ready.");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//            loadBalancerThread.start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}

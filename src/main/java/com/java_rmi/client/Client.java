package com.java_rmi.client;

import com.java_rmi.load_balancer.LoadBalancerInterface;
import com.java_rmi.server.ServerAllocation;
import com.java_rmi.server.ServerImplementation;
import com.java_rmi.server.ServerInterface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The `Client` class represents a client application that interacts with a load balancer
 * and a remote server using Java RMI.
 * It reads input data, performs remote method calls on servers, and manages client-side caching.
 */
public class Client {
    // Client-side cache for method results
    private static Map<String, Long> clientCache;
    // Maximum cache size
    private static final int CLIENT_CACHE_LIMIT = 45;
    private static Map<String, Long> executionTimes;

    /**
     * Calls a remote method on a server and measures execution time.
     * @param server     The remote server interface.
     * @param methodName The name of the method to invoke.
     * @return The result of the remote method call.
     */
    private static long callServerMethod(ServerInterface server, String methodName, String[] args) {
        try {
            long startTime = System.currentTimeMillis();
            long result = 0;

            // Call the appropriate server method based on the methodName and pass the arguments
            if (methodName.equals("getPopulationofCountry")) {
                if (args.length == 1) {
                    result = server.getPopulationOfCountry(args[0]);
                }
            } else if (methodName.equals("getNumberofCities")) {
                if (args.length == 2) {
                    result = server.getNumberOfCities(args[0], Integer.parseInt(args[1]));
                }
            } else if (methodName.equals("getNumberofCountries")) {
                if (args.length == 2) {
                    result = server.getNumberOfCountries(
                            server.getNumberOfCities(args[0], Integer.parseInt(args[1])),
                            Integer.parseInt(args[1])
                    );
                } else if (args.length == 3) {
                    result = server.getNumberOfCountries(
                            server.getNumberOfCities(args[0], Integer.parseInt(args[1])),
                            Integer.parseInt(args[1]),
                            Integer.parseInt(args[2])
                    );
                }
            }

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            long turnAroundTime = executionTime; // Calculate turnaround time and waiting time
            long waitingTime = 0;

            return result;

        } catch (RemoteException e) {
            System.out.println("Error calling server method!!!");
            return -1;
        }
    }

    /**
     * Calculates and prints average execution times for methods.
     * @throws IOException If an error occurs while writing to output files.
     */
    private static void calculateAndPrintAverageTimes() throws IOException {
        long allTurnAroundTime = 0;
        long allExecutionTime = 0;

        // Iterate through the execution times
        for (Map.Entry<String, Long> entry : executionTimes.entrySet()) {
            String methodName = entry.getKey();
            long executionTime = entry.getValue();

            // Calculate total turnaround time and execution time
            allTurnAroundTime += executionTime;
            allExecutionTime += executionTime;

            System.out.println("Method name: " + methodName + ", execution Time: " + executionTime + " ms");
        }

        // Calculate average turnaround time and execution time
        double avgTurnAroundTime = (double) allTurnAroundTime / executionTimes.size();
        double avgExecutionTime = (double) allExecutionTime / executionTimes.size();

        System.out.println("Average turnaround time: " + avgTurnAroundTime + " ms "+"\nAverage execution time: " + avgExecutionTime + " ms");
    }

    public static void main(String[] args) {
        try {
            // Initialize RMI registry connection to the LoadBalancer
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            LoadBalancerInterface loadBalancer = (LoadBalancerInterface) registry.lookup("LoadBalancer");

            // Initialize client-side cache
            clientCache = new LinkedHashMap<>(CLIENT_CACHE_LIMIT, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry eldest) {
                    // Remove the least recently used entry when the cache size exceeds the limit
                    return size() > CLIENT_CACHE_LIMIT;
                }
            };
            // Initialize map to store execution times
            executionTimes = new LinkedHashMap<>();

            // Input and output file paths
            String inputFile = "src\\main\\java\\com\\java_rmi\\client\\exercise_1_input.txt";
            String outputFile = "src\\main\\java\\com\\java_rmi\\client\\client_cache.txt"; //Specify the output file path for the client cache.

            String naiveServerOutputFile = "src\\main\\java\\com\\java_rmi\\server\\naive_server.txt"; // Specify the output file path for the naive server
            String serverCacheOutputFile = "src\\main\\java\\com\\java_rmi\\server\\server_cache.txt"; // Specify the output file path for the server with caching

            // Initialize FileReader and FileWriter
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            FileWriter writer = new FileWriter(outputFile);

            FileWriter naiveServerWriter = new FileWriter(naiveServerOutputFile);
            FileWriter serverCacheWriter = new FileWriter(serverCacheOutputFile);

            String selectedLine;
            long startTime = System.currentTimeMillis();

            while ((selectedLine = reader.readLine()) != null) {
                // Parse each line of the input file
                String[] elementsLine = selectedLine.split(" ");
                if (elementsLine.length >= 2) {
                    String methodName = elementsLine[0];
                    String[] argsArray = new String[elementsLine.length - 2];
                    System.arraycopy(elementsLine, 1, argsArray, 0, elementsLine.length - 2);
                    int zone = Integer.parseInt(elementsLine[elementsLine.length - 1].substring(5));

                    String cacheKey = methodName + String.join(":", argsArray);

                    if (clientCache.containsKey(cacheKey)) {
                        ServerAllocation serverAllocation = loadBalancer.requestServerAllocation(zone);

                        long cachedResult1 = clientCache.get(cacheKey);
                        String resultTime1 = " (cached)";
                        String outputSelectedLine = cachedResult1 + " " + selectedLine + " " + resultTime1 + ", processed by Server " + serverAllocation.getServerName() + ")\n";
                        writer.write(outputSelectedLine);
                    } else {
                        // Make a remote method call to the LoadBalancer to get server assignment
                        ServerAllocation serverAllocation = loadBalancer.requestServerAllocation(zone);
                        ServerImplementation server = new ServerImplementation();

                        long result1 = callServerMethod(server, methodName, argsArray);

                        long endTime = System.currentTimeMillis();
                        long executionTime = endTime - startTime;
                        long turnAroundTime = executionTime;
                        long waitingTime = 0;

                        String resultTime1 = "turnaround time: " + turnAroundTime + " ms, execution time: " + executionTime + " ms, waiting time: " + waitingTime + " ms)";

                        String outputSelectedLine = result1 + " " + selectedLine + " " + resultTime1 + ", processed by Server " + serverAllocation.getServerName() + ")\n";

                        // Write the output selectedLine to the file
                        writer.write(outputSelectedLine);
                        naiveServerWriter.write(outputSelectedLine);
                        serverCacheWriter.write(outputSelectedLine);

                        clientCache.put(cacheKey, result1);
                    }
                }
            }
            // Close the reader and writer
            reader.close();
            writer.close();
            naiveServerWriter.close();
            serverCacheWriter.close();

            calculateAndPrintAverageTimes();

        } catch (Exception e) {
            System.out.println("Client side error!!!");
        }
    }
}
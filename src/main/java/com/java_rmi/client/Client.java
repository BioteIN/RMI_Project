package com.java_rmi.client;

import com.java_rmi.load_balancer.LoadBalancerInterface;
import com.java_rmi.server.ServerAssignment;
import com.java_rmi.server.ServerImpl;
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

public class Client {
    private static Map<String, String> clientCache; // Client-side cache for method results
    private static final int CLIENT_CACHE_LIMIT = 45; // Maximum cache size
    private static Map<String, Long> executionTimes; // Map to store execution times for each method

    public static void main(String[] args) {
        try {
            // Initialize RMI registry connection to the LoadBalancer
            Registry registry = LocateRegistry.getRegistry("localhost", 1099); // Change "localhost" if LoadBalancer is on a different machine
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
            String inputFile = "src\\main\\java\\com\\java_rmi\\client\\exercise_1_input.txt"; // Update with your input file path
            String outputFile = "src\\main\\java\\com\\java_rmi\\client\\client_cache.txt"; // Update with your output file path

            // Initialize FileReader and FileWriter
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            FileWriter writer = new FileWriter(outputFile);

            String naiveServerOutputFile = "src\\main\\java\\com\\java_rmi\\client\\naive_server.txt"; // Specify the output file path for the naive server
            String serverCacheOutputFile = "src\\main\\java\\com\\java_rmi\\client\\server_cache.txt"; // Specify the output file path for the server with caching

            // Initialize FileWriter for naive server output
            FileWriter naiveServerWriter = new FileWriter(naiveServerOutputFile);

            // Initialize FileWriter for server with caching output
            FileWriter serverCacheWriter = new FileWriter(serverCacheOutputFile);

            long startTime = System.currentTimeMillis();
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse each line of the input file
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    String methodName = parts[0];
                    String[] argsArray = new String[parts.length - 2];
                    System.arraycopy(parts, 1, argsArray, 0, parts.length - 2);
                    int zone = Integer.parseInt(parts[parts.length - 1].substring(5));

                    // Create a cache key based on the method name and arguments
                    String cacheKey = methodName + String.join(":", argsArray);

                    // Check if the result is in the client-side cache
                    if (clientCache.containsKey(cacheKey)) {
                        ServerAssignment serverAssignment = loadBalancer.requestServerAssignment(zone);

                        String cachedResult1 = clientCache.get(cacheKey);
                        String resultTime1 = " (cached)";
//                        String outputLine = cachedResult1 + " " + line + resultTime1 + "\n";
                        String outputLine = cachedResult1 + " " + line + " " + resultTime1 + ", processed by Server " + serverAssignment.getServerName() + ")\n";
                        writer.write(outputLine);
                    } else {
                        // Make a remote method call to the LoadBalancer to get server assignment
                        ServerAssignment serverAssignment = loadBalancer.requestServerAssignment(zone);

                        // Initialize RMI registry connection to the selected server
//                        Registry serverRegistry = LocateRegistry.getRegistry("localhost", serverAssignment.getServerPort());
//                        ServerInterface server = (ServerInterface) serverRegistry.lookup(serverAssignment.getServerName());
                        ServerImpl server = new ServerImpl();

                        // Invoke the corresponding server method
                        String result1 = invokeServerMethod(server, methodName, argsArray);
//                        String resultTime1 = time(server, methodName, argsArray);
                        long endTime = System.currentTimeMillis();
                        long executionTime = endTime - startTime;

                        // Calculate turnaround time and waiting time (assuming no waiting time)
                        long turnaroundTime = executionTime;
                        long waitingTime = 0;

                        // Return the result as a formatted string
                        String resultTime1 = " (turnaround time: " + turnaroundTime + " ms, execution time: " + executionTime + " ms, waiting time: " + waitingTime + " ms)";


                        // Generate the output line
                        String outputLine = result1 + " " + line + " " + resultTime1 + ", processed by Server " + serverAssignment.getServerName() + ")\n";

                        // Write the output line to the file
                        writer.write(outputLine);

                        // Write output lines to the naive server output file
                        naiveServerWriter.write(outputLine);

                        // Write output lines to the server with caching output file
                        serverCacheWriter.write(outputLine);


                        // Add the result to the client-side cache
                        clientCache.put(cacheKey, result1);
                    }
                }
            }

            // Close the reader and writer
            reader.close();
            writer.close();

            // Close the naive server output writer
            naiveServerWriter.close();

            // Close the server with caching output writer
            serverCacheWriter.close();


            // Calculate and print average turnaround time, execution time, and waiting time for each method
            calculateAndPrintAverageTimes();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String invokeServerMethod(ServerInterface server, String methodName, String[] args) {
        try {
            long startTime = System.currentTimeMillis();

            // Invoke the appropriate server method based on the methodName and pass the arguments
            String result = "";
            if (methodName.equals("getPopulationofCountry")) {
                if (args.length == 1) {
                    long population = server.getPopulationOfCountry(args[0]);
                    result = String.valueOf(population);
                }
            } else if (methodName.equals("getNumberofCities")) {
                if (args.length == 2) {
                    int numberOfCities = server.getNumberOfCities(args[0], Integer.parseInt(args[1]));
                    result = String.valueOf(numberOfCities);
                }
            } else if (methodName.equals("getNumberofCountries")) {
                if (args.length == 2) {
                    int numberOfCountries = server.getNumberOfCountries(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                    result = String.valueOf(numberOfCountries);
                } else if (args.length == 3) {
                    int numberOfCountries = server.getNumberOfCountries(
                            Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])
                    );
                    result = String.valueOf(numberOfCountries);
                }
            }

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Calculate turnaround time and waiting time (assuming no waiting time)
            long turnaroundTime = executionTime;
            long waitingTime = 0;

            // Return the result as a formatted string
            return result;

        } catch (RemoteException e) {
            e.printStackTrace();
            return "Error invoking server method";
        }
    }


    private static void logExecutionTime(String methodName, long executionTime) {
        // Log the execution time for each method
        executionTimes.put(methodName, executionTime);
    }


    private static void calculateAndPrintAverageTimes() throws IOException {
        // Calculate and print average turnaround time, execution time, and waiting time for each method
        // You can use the logged execution times and timestamps to calculate these values

        // Initialize variables to store total times and counts
        long totalTurnaroundTime = 0;
        long totalExecutionTime = 0;

        // Iterate through the execution times
        for (Map.Entry<String, Long> entry : executionTimes.entrySet()) {
            String methodName = entry.getKey();
            long executionTime = entry.getValue();

            // Calculate total turnaround time and execution time
            totalTurnaroundTime += executionTime;
            totalExecutionTime += executionTime;

            // Print execution time for each method
            System.out.println("Method: " + methodName + ", Execution Time: " + executionTime + " ms");
        }

        // Calculate average turnaround time and execution time
        double avgTurnaroundTime = (double) totalTurnaroundTime / executionTimes.size();
        double avgExecutionTime = (double) totalExecutionTime / executionTimes.size();

        // Print average times to the console
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime + " ms");
        System.out.println("Average Execution Time: " + avgExecutionTime + " ms");
    }
}

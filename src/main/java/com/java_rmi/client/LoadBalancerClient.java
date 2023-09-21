package com.java_rmi.client;

import com.java_rmi.load_balancer.LoadBalancerInterface;
import com.java_rmi.server.ServerAssignment;
import com.java_rmi.server.ServerImpl;
import com.java_rmi.server.ServerLoadInfo;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoadBalancerClient {
    public static void main(String[] args) {
        try {
            // Localize o registro RMI do Load Balancer (certifique-se de que o Load Balancer esteja em execução)
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Obtenha o stub do Load Balancer
            LoadBalancerInterface loadBalancer = (LoadBalancerInterface) registry.lookup("LoadBalancer");

            // Simule 10 solicitações de cliente
            for (int i = 0; i < 10; i++) {
                // Suponha que o cliente esteja em uma zona específica (você pode ajustar isso conforme necessário)
                int clientZone = 5;

                // Faça uma chamada ao Load Balancer para obter um servidor atribuído
                ServerAssignment serverAssignment = loadBalancer.requestServerAssignment(clientZone);

                // Obtenha o nome do servidor atribuído
                String serverName = serverAssignment.getServerName();

                // Obtenha o número da porta do servidor atribuído
                int serverPort = serverAssignment.getServerPort();

                // Obtenha o stub do servidor atribuído
                ServerImpl server = new ServerImpl ();

                // Faça chamadas remotas para o servidor atribuído
                long population = server.getPopulationOfCountry("Norway");
//                int t1 = server.getNumberOfCities("Norway",100000);
                int t2 = server.getNumberOfCountries(2,5000000);
//                int t3 = server.getNumberOfCountries(30,100000, 800000);

                // Obtenha informações de carga do servidor atribuído
                ServerLoadInfo loadInfo = server.getServerLoadInfo();

                // Exiba os resultados das chamadas remotas e informações de carga
                System.out.println("Server: " + serverName);
                System.out.println("Population"+ (i+1)+": " + population);
                System.out.println("T1"+ (i+1)+": " + t2);
//                System.out.println("T2"+ (i+1)+": " + t2);
//                System.out.println("T3"+ (i+1)+": " + t3);
                System.out.println("Load: " + loadInfo.getLoad());
                System.out.println("WaitingListSize: " + loadInfo.getWaitingListSize());

                // Aguarde um pouco antes de fazer outra solicitação (para simular solicitações de cliente intermitentes)
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

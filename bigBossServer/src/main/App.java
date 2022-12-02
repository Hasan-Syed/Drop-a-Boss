package main;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import clients.player;

public class App {
    public int port = 6969; // Tje Port the Server is Listenting on

    public int maxPlayers = 6; // Max Player Limit
    public List<player> clientList = new ArrayList<>(); // Client List
    public ExecutorService pool = Executors.newFixedThreadPool(maxPlayers); // Executoner Thread Pool

    public int playerIndex = 0; // Player Index

    /**
     * * Construct
     * Starts up the Server, Listens on 'Port', Accepts Requests and Creates a new
     * Player Object, and ents it to the Execution Pool List
     */
    App() {
        //
        try (ServerSocket serverListener = new ServerSocket(port)) {
            while (true) {
                Socket client = serverListener.accept(); // Accept on a temporary Socket
                player newPlayer = new player(playerIndex, client, this); // Initialize the Player class, with the
                                                                          // playerID(player index), client Socket, and
                                                                          // this classs
                clientList.add(newPlayer); // And the newPlayer Player class to the CLient List

                pool.execute(newPlayer); // Execute the Player on a thread on the Pool
                playerIndex++; // Add one for a nw Player Index
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new App();
    }
}

package multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import entity.Entity;
import main.enums.responseTypeEnum;

public class multiplayer implements Runnable {
    public int onlineID;

    public final String myIP = InetAddress.getLocalHost().getHostAddress();
    public final ServerSocket serverListener = new ServerSocket(7070);

    public Boolean stillAlive = false;

    public final Socket playerToServer; // Data to Server
    public final PrintWriter toServerReader; // Data to Server
    public final Socket ServerToPlayer; // Data from server
    public final BufferedReader fromServerReader; // Data from Server

    public final Entity player;

    public Object fromServer;
    public Object toServer;

    public multiplayer(String IP, int PORT, Entity player) throws UnknownHostException, IOException {
        this.player = player;
        playerToServer = new Socket(IP, PORT); // Connect to the Server
        this.toServerReader = new PrintWriter(playerToServer.getOutputStream(), true); // Initialize Server Writer
        ServerToPlayer = serverListener.accept(); // Accept 'from Server' connection Offer
        this.fromServerReader = new BufferedReader(new InputStreamReader(ServerToPlayer.getInputStream())); // Initialize
                                                                                                            // Server
                                                                                                            // Response
                                                                                                            // Reader
        init();
    }

    void init() throws NumberFormatException, IOException {
        // Get Player ID \\
        Logger(responseTypeEnum.toServer, "Reqeusting ID");
        toServer = "id"; // Request Type
        writeServer(); // Request
        readServer(); // Read Server Response
        onlineID = Integer.parseInt((String) fromServer); // Server Response
        Logger(responseTypeEnum.fromServer, "your online ID: " + onlineID); // Log Server Interation
        // Sending Server Player Names \\
        Logger(responseTypeEnum.toServer, "Setting Player name on the Server");
        toServer = "playerName"; // Request Type
        writeServer(); // Send Player Name Request
        toServer = player.name;
        writeServer(); // Send Player Name
        Logger(responseTypeEnum.fromServer, "Player Name Set"); // Log Server Interation
        // Send Player's Initial Entity \\
        Logger(responseTypeEnum.toServer, "Sending Player Entity");
        toServer = "playerEntity"; // Request Type
        writeServer(); // Send playerEntity Request to server
        toServer = player.entityJson(); // Request Type
        writeServer(); // Send Player Entity JSON
        // Initialize Finished \\
        toServer = "initComplete";
        writeServer(); // Send End Init to server
        Logger(responseTypeEnum.toServer, "Initialization Finshed Start Game");
        stillAlive = true;
    }

    void Logger(responseTypeEnum responseType, Object log) {
        switch (responseType) {
            case fromServer -> {
                System.out.println("[Multiplayer Connector]: Server Response: " + log);
            }
            case toServer -> {
                System.out.println("[Multiplayer Connector]: To Server: " + log);
            }
        }
    }

    public void readServer() {
        try {
            fromServer = fromServerReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeServer() {
        toServerReader.println(toServer);
    }

    // public void closeConnection() {
    // toServer.println("pleaseLeaveMeAlone");
    // }

    @Override
    public void run() {
        // boolean run = true;
        // while (run) {
        // try {
        // toServer.println(toServerStr);
        // serverReturn = fromServer.readLine();
        // run = false;
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
    }
}

package multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import entity.Entity;
import entity.multiplayerBot;
import main.gamePanel;
import main.enums.responseTypeEnum;

public class multiplayer extends Thread {
    public int onlineID;

    public final String myIP = InetAddress.getLocalHost().getHostAddress();
    public final ServerSocket serverListener = new ServerSocket(7070);

    public Boolean stillAlive = false;

    public final Socket playerToServer; // Data to Server
    public final PrintWriter toServerReader; // Data to Server
    public final Socket ServerToPlayer; // Data from server
    public final BufferedReader fromServerReader; // Data from Server

    public final Entity player;
    public final gamePanel gp;

    public multiplayer(String IP, int PORT, Entity player, gamePanel gp) throws UnknownHostException, IOException {
        this.player = player;
        this.gp = gp;
        this.setName("Multiplayer Thread");
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
        writeServer("id"); // Request
        onlineID = Integer.parseInt((String) readServer()); // Server Assigned ID
        Logger(responseTypeEnum.fromServer, "your online ID: " + onlineID); // Log Server Interation
        // Sending Server Player Names \\
        Logger(responseTypeEnum.toServer, "Setting Player name on the Server");
        writeServer("playerName"); // Send Player Name Request
        writeServer((String) player.name); // Send Player Name
        Logger(responseTypeEnum.fromServer, "Player Name Set"); // Log Server Interation
        // Send Player's Initial Entity \\
        Logger(responseTypeEnum.toServer, "Sending Player Entity");
        writeServer("playerEntity"); // Send playerEntity Request to server
        writeServer((String) player.entityJson().toString()); // Send Player Entity JSON
        // Initialize Finished \\
        writeServer("initComplete"); // Send End Init to server
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

    public Object readServer() {
        try {
            return fromServerReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeServer(Object toServer) {
        toServerReader.println(toServer);
    }

    public boolean isValid(String json) {
        try {
            new JSONObject(json); // Try to parse the String as a JsonObject
        } catch (JSONException e) {
            return false; // The parse failed return false
        }
        return true; // the parse was successful return true
    }

    public void run() {
        while (true) {
            writeServer(player.entityJson());

            Object serverInput = readServer();

            while (serverInput != "finished") {
                serverInput = (String) readServer();
                if (isValid((String) serverInput)) {
                    JSONObject playerUpdate = new JSONObject((String) serverInput);
                    int remoteID = (int) playerUpdate.get("ID");
                    if (playerUpdate.has("playerUpdate")) {
                        playerUpdate = playerUpdate.getJSONObject("playerUpdate");
                        if (remoteID != onlineID) {
                            boolean playerFound = false;
                            for (multiplayerBot playerBot : gp.multiplayerAIArray) {
                                if (playerBot.ID == remoteID) {
                                    playerFound = true;
                                    playerBot.update(playerUpdate);
                                }
                            }
                            if (!playerFound) {
                                multiplayerBot playerBot = new multiplayerBot(gp, remoteID);
                                playerBot.update(playerUpdate);
                                gp.multiplayerAIArray.add(playerBot);
                            }
                        }
                    }
                }
                if (serverInput.equals("finished")) {
                    break;
                }
                gp.repaint();
            }
        }
    }
}

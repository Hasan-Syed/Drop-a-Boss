package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class player extends Thread {
    /*
     * These are Debug Logger Enums
     */
    enum debugging {
        toClient,
        fromClient,
        initiatingClient,
        internalAlternatives
    }

    public JSONObject currentMovementObject;

    public List<JSONObject> playerEvents;
    public List<JSONObject> playerPositioningEvents;
    public List<JSONObject> playerAbilityEvents;
    JSONArray playerCurrentEvent;

    public final App mainClass;
    public final int ID;
    public String playerName;

    public final Socket playerToServer; // To Server Client
    public final BufferedReader fromPlayerReader; // To Server Client
    public final Socket ServerToPlayer; // From Server Client
    public final PrintWriter toPlayerWriter; // From Server Client

    public player(int ID, Socket player, List<JSONObject> playerEvents, App mainClass) throws IOException {
        this.ID = ID; // Assign Player instance an ID
        this.mainClass = mainClass; // Forwording Main Class
        this.playerEvents = playerEvents; // All Player Casted Events
        this.playerPositioningEvents = new ArrayList<>(); // Player Movement Events
        this.playerAbilityEvents = new ArrayList<>(); // Player Casted Ability Events

        this.playerToServer = player; // Player to Server
        logger(debugging.fromClient,
                "Connected to IP: " + playerToServer.getInetAddress() + " The Entity has been given the ID: " + ID); // Log
                                                                                                                     // Player
                                                                                                                     // Connetion
        this.fromPlayerReader = new BufferedReader(new InputStreamReader(playerToServer.getInputStream())); // Connect a
                                                                                                            // Reader to
                                                                                                            // Player to
                                                                                                            // server'
        logger(debugging.toClient, "[ID: " + ID + "]: Connected back to IP: " + playerToServer.getInetAddress()); // Log
                                                                                                                  // Player's
                                                                                                                  // Connection
                                                                                                                  // IP
        this.ServerToPlayer = new Socket(playerToServer.getInetAddress(), 7070); // Attempt to Connect to Player
        logger(debugging.initiatingClient, "to Player Connection Status: " + ServerToPlayer.isConnected()); // to Player
                                                                                                            // Connection
                                                                                                            // Status
        this.toPlayerWriter = new PrintWriter(ServerToPlayer.getOutputStream(), true); // Connect a writer to Server to
                                                                                       // Player

        init(); // Initialize Player
        this.setName(playerName + "'s Online Thread");
        this.start(); // Start Player Connection
        //////////////////////////////////// Thread
    }

    /*
     * the Method void readPlayer() is used to read Player Events to the Server
     */
    private Object readPlayer() {
        try {
            return fromPlayerReader.readLine(); // Try to Read From Player Events
        } catch (IOException e) {
            e.printStackTrace();
            this.stop();
            return null;
        }
    }

    /*
     * The Method void writePlayer() is used to Write Events to the Player
     */
    private void writePlayer(Object toPlayer) {
        toPlayerWriter.println(toPlayer);
    }

    /*
     * The Method void init() is used to Initialize Player to the Server
     */
    void init() {
        boolean initializing = true; // While this is true Keep Looping
        while (initializing) {
            /*
             * The Following Switch block is used to Initialize the Players Client, and the
             * Server
             */
            switch (new String((String) readPlayer())) {
                case "id" -> {
                    writePlayer(ID); // Send the ID's
                }
                case "playerName" -> {
                    // Wait for Player Name to be sent
                    playerName = (String) readPlayer(); // Get Playername
                }
                case "playerEntity" -> {
                    Object Entity = (String) readPlayer(); // Wait for Player Name to be sent
                    System.out.println(Entity);
                    if (isValid((String) Entity)) {
                        JSONObject player = new JSONObject((String) Entity);
                        player.put("ID", ID);
                        playerPositioningEvents.add(player); // Add Player's Event
                        // System.out.println(fromPlayer);
                    }
                }
                // case "initComplete" means that the player is done sending Init Information
                case "initComplete" -> {
                    initializing = false; // Ser initializing to false, and quit the init loop
                    logger(debugging.fromClient, "Initialization Finished");
                }
            }
        }
    }

    /*
     * the Method isValid is used to Varify if the given Sting can be a JSON
     * the method returns true if it can be a JSON
     */
    public boolean isValid(String json) {
        try {
            new JSONObject(json); // Try to parse the String as a JsonObject
        } catch (JSONException e) {
            return false; // The parse failed return false
        }
        return true; // the parse was successful return true
    }

    // This is a Logger so yea...
    void logger(debugging logType, String log) {
        switch (logType) {
            case fromClient -> {
                System.out.println("[Player Class]: [From Client]: " + log);
            }
            case toClient -> {
                System.out.println("[Player Class]: [To Client]: " + log);
            }
            case initiatingClient -> {
                System.out.println("[Initiating Client, ID " + ID + "]: " + log);
            }
            case internalAlternatives -> {
                System.out.println("[Internal Error Corrections]: " + log);
            }
        }
    }

    /*
     * The "sendMe" variable is has a heavy involvement in the sending of player
     * commands
     * 
     * The Server Recives all kinds of commands from the client such as
     * 
     * "playerUpdate", "abilityEvent", "noMovesMade"
     * 
     * when the Command is "noMovesMade", the sever doesnt send the Player Postion
     * to other clients since they are redundent
     */
    public boolean sendMe = true;
    boolean abilityEventMade = false;

    /*
     * The Method Run keeps contacting Players, getting their locaion and updaing
     * locations on other Clients
     * 
     * this method also updates and applies Abilities
     */
    public void run() {
        Object fromPlayer;
        while (true) {
            fromPlayer = readPlayer(); // Read Player Command
            currentMovementObject = new JSONObject((String) fromPlayer).put("ID", ID);
            // Sending Player Info
            for (player client : mainClass.clientList) {
                writePlayer(client.currentMovementObject);
            }
            writePlayer("finished");
        }
    }
}//

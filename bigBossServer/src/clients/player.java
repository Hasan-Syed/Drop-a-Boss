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

import main.App;

public class player extends Thread {
    /**
     * debugging enums for messages
     */
    enum debugging {
        toClient,
        fromClient,
        initiatingClient,
        internalAlternatives
    }

    public List<JSONObject> playerEvents;
    public List<JSONObject> playerPositioningEvents;
    public List<JSONObject> playerAbilityEvents;

    public final App mainClass;
    public final int ID;
    public String playerName;

    public JSONArray serverCastedAbilities = new JSONArray();

    public final Socket playerToServer; // To Server Client
    public final BufferedReader fromPlayerReader; // To Server Client
    public final Socket ServerToPlayer; // From Server Client
    public final PrintWriter toPlayerWriter; // From Server Client

    public JSONObject updateRecived;
    public JSONObject updateToBeSent = new JSONObject();
    public JSONArray castedAbilities = new JSONArray();

    /**
     * 
     * @param ID           ID of the Player
     * @param player       The player incoming socket
     * @param playerEvents The playerEvents array, Stores all Player Events
     * @param mainClass    The Main Class
     * @throws IOException This Exeption will never be thrown, but incase; There was
     *                     a problem Reading input from {@code playerToServer}
     */
    public player(int ID, Socket player, App mainClass) throws IOException {
        this.ID = ID; // Assign Player instance an ID
        this.mainClass = mainClass; // Forwording Main Class
        this.playerPositioningEvents = new ArrayList<>(); // Player Movement Events
        this.playerAbilityEvents = new ArrayList<>(); // Player Casted Ability Events

        this.playerToServer = player; // Player to Server
        // Log Player Connetion
        logger(debugging.fromClient,
                "Connected to IP: " + playerToServer.getInetAddress() + " The Entity has been given the ID: " + ID);
        // Connect a Reader to Player to server
        this.fromPlayerReader = new BufferedReader(new InputStreamReader(playerToServer.getInputStream()));
        // Log Player's Connection IP
        logger(debugging.toClient, "[ID: " + ID + "]: Connected back to IP: " + playerToServer.getInetAddress());
        this.ServerToPlayer = new Socket(playerToServer.getInetAddress(), 6868); // Attempt to Connect to Player
        // to Player Connection Status
        logger(debugging.initiatingClient, "to Player Connection Status: " + ServerToPlayer.isConnected());
        // Connect a writer to Server to Player
        this.toPlayerWriter = new PrintWriter(ServerToPlayer.getOutputStream(), true);
        init(); // Initialize Player
        this.setName(playerName + "'s Online Thread");
        this.start(); // Start Player Connection
    }

    /**
     * * the Method void readPlayer() is used to read Player Events to the Server
     * 
     * @return Object from 'fromPlayerReader' (Player Client's Input)
     */
    private Object readPlayer() {
        try {
            return fromPlayerReader.readLine(); // Try to Read From Player Events
        } catch (IOException e) {
            // Log Error
            logger(debugging.fromClient,
                    "[Read Client]: There was a Problem Reading from the command from Player, the thread is now Interupted, the Player Client Closing.");
            this.interrupt(); // Interrupt the Runnable Thread
            return null; // Return Null, but it doesn't matter, the client will be Disconnected anyways
        }
    }

    /*
     * The Method void writePlayer() is used to Write Events to the Player
     */
    public void writePlayer(Object toPlayer) {
        toPlayerWriter.println(toPlayer); // Write to Player
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
                    if (isValid((String) Entity)) {
                        JSONObject player = new JSONObject((String) Entity);
                        player.put("ID", ID);
                        playerPositioningEvents.add(player); // Add Player's Event
                    }
                }
                case "abilities" -> {
                    writePlayer(mainClass.abilityList); // Add Player's Event
                }
                // case "initComplete" means that the player is done sending Init Information
                case "initComplete" -> {
                    initializing = false; // Ser initializing to false, and quit the init loop
                    logger(debugging.fromClient, "Initialization Finished"); // Log Initializaion Finished
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
    void logger(debugging logType, Object log) {
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
     * The Method Run keeps contacting Players, getting their locaion and updating
     * locations on other Clients
     * 
     * this method also updates and applied Abilities
     */
    public synchronized void run() {
        Object fromPlayer;
        while (true) {
            fromPlayer = readPlayer(); // Read Player Command
            updateRecived = new JSONObject((String) fromPlayer);
            updateToBeSent.clear();

            for (player player : mainClass.clientList) {
                JSONObject playerUpdateMulti = player.updateRecived;
                if (playerUpdateMulti != null) {
                    playerUpdateMulti = playerUpdateMulti.getJSONObject("gameUpdate");
                    // Get player Update and put it on the List
                    if (updateToBeSent.has("playerUpdate") && playerUpdateMulti.has("playerUpdate")) {
                        updateToBeSent.getJSONArray("playerUpdate")
                                .put(playerUpdateMulti.getJSONObject("playerUpdate"));
                    } else if (playerUpdateMulti.has("playerUpdate")) {
                        updateToBeSent.put("playerUpdate",
                                new JSONArray().put(playerUpdateMulti.getJSONObject("playerUpdate")));
                    }
                    // Get casted Ability and put it on the List
                    if (playerUpdateMulti.has("castedAbility")
                            && !playerUpdateMulti.getJSONArray("castedAbility").isEmpty()) {
                        System.out.println(playerUpdateMulti);
                        JSONArray remoteCasted = playerUpdateMulti.getJSONArray("castedAbility");
                        for (Object castedAbility : remoteCasted) {
                            JSONObject ability = (JSONObject) castedAbility;
                            JSONObject impact = ability.getJSONObject("impact");
                            int targetID = impact.getInt("causedTo");
                            player target = mainClass.clientList.get(targetID);
                            target.castedAbilities.put(ability);
                        }
                    }
                }
            }
            if (!castedAbilities.isEmpty()) {
                for (var index = 0; index < castedAbilities.length(); index++) {
                    if (updateToBeSent.has("castedAbility")) {
                        updateToBeSent.getJSONArray("castedAbility").put(castedAbilities.get(index));
                    } else {
                        updateToBeSent.put("castedAbility", new JSONArray().put(castedAbilities.get(index)));
                    }
                }
                castedAbilities.clear();
                ;
            }
            writePlayer(updateToBeSent);
        }
    }
}

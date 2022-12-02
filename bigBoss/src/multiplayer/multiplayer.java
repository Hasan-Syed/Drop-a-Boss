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

import appliars.healAppliars.healObject;
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

    /**
     * 
     * @param IP     The IP Address the Server is listening on
     * @param PORT   The Port the Server is listening on
     * @param player The Player Entity
     * @param gp     The Game Panel
     * @throws UnknownHostException The UnknownHostException can be thrown when:
     *                              The server not Running,
     *                              Incorrect IP,
     *                              Incorrect Port.
     * @throws IOException          The IOException on normal operation will never
     *                              be thrown unless The server fails to send a
     *                              response
     * 
     *                              This Construct Initializes the Multiplayer of
     *                              the Game
     */
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

    /**
     * init() is used to Initialize the Player to the server and the server to the
     * player
     * 
     * @throws NumberFormatException This Exception will not be thrown as this part
     *                               is hard coded on the server side, (ID)
     */
    void init() throws NumberFormatException {
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

    /**
     * Logger() method is used to log Items in the console/terminal
     * 
     * @param responseType In the Console the print out says either Server Response,
     *                     or to Server, Depending on the selected Enum
     * @param log          This is the Object the Logger Prints
     */
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

    /**
     * readServer() Method reads the Server Input and returns it
     * 
     * @return whatever the server Sends it is returned as a Object, it can later be
     *         casted as needed
     */
    public Object readServer() {
        try {
            return fromServerReader.readLine(); // Read from Server
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * writeServer() Method Writes and Sends an Object to the Server
     * 
     * @param toServer the Object is send to the server
     */
    public void writeServer(Object toServer) {
        toServerReader.println(toServer); // Write to Server
    }

    /**
     * isValid() Method is Used to check if the provided String is a validJson and
     * return true and false respectively
     * 
     * @param json The String is Checked if its a JsonObject or not
     * @return Boolean; true if the String can be parsed as a JsonObject, and false
     *         otherwise
     */
    public boolean isValid(String json) {
        try {
            new JSONObject(json); // Try to parse the String as a JsonObject
        } catch (JSONException e) {
            return false; // The parse failed return false
        }
        return true; // the parse was successful return true
    }

    /**
     * run Method is tied to the Thread (Class Extended) the thread is Started, this
     * method keeps while the game is still connected to 'playerToServer' socket,
     * this sends the players current locations using 'writeServer', it also reads
     * the server sent items that contain :
     * *- other Player's main Entity,
     * *- Other player Casted Abilities,
     * *- etc
     * and them updates the game accordingly.
     */
    public void run() {
        while (true) {
            /*
             * Send the Current 'Players main Entity' the Server
             */
            writeServer(player.entityJson());
            /*
             * The Following Code Reads the Server Send Items and applies them Acordingly
             */
            Object serverInput = readServer(); // Read Server send Item
            /*
             * Check if the Server Input is "finished", it is so the client doesn't get
             * stuck attempting to get other players data
             */
            while (serverInput != "finished") {
                serverInput = (String) readServer();
                if (isValid((String) serverInput)) {
                    // *Parse the serverInput as a JsonObject
                    JSONObject command = new JSONObject((String) serverInput);
                    /*
                     * * From here each if Statment is Checks for will check if the JSONObject
                     * contains their respected Keys
                     */
                    // * Checks if the read Command has a "playerUpdate" JSON object in it
                    if (command.has("playerUpdate")) {
                        int remoteID = (int) command.get("ID"); // *Get and Parse Player ID from the server
                        // * Get the Player Object from the Command 'playerUpdate'
                        JSONObject playerUpdate = command.getJSONObject("playerUpdate");
                        /**
                         * * Check if the JsonObject ID is the same as the client ID
                         */
                        if (remoteID != onlineID) {
                            boolean playerFound = false; // * Verifies the the Player is found */
                            /*
                             * Loops through all multiplayer Enties Checking if the Player with the remote
                             * ID can be found
                             */
                            for (multiplayerBot remoteSpawnedPlayer : gp.multiplayerAIArray) {
                                // * If the ID is found the Update Player Entity */
                                if (remoteSpawnedPlayer.ID == remoteID) {
                                    playerFound = true; // The Player is Found so the new entity is not Spawned
                                    // Update the 'remoteSpawnedPlayer' entity
                                    remoteSpawnedPlayer.update(playerUpdate);
                                }
                            }
                            /*
                             * If the Player is not found Spawn a new entity in to the world
                             */
                            if (!playerFound) {
                                // Make a new multiplayerBot, it will be sent the gamePanel, the the remoteID
                                multiplayerBot playerBot = new multiplayerBot(gp, remoteID);
                                playerBot.update(playerUpdate); // Send the new Player the JSON Object
                                gp.multiplayerAIArray.add(playerBot); // Add the Player to the multiplayerList for
                                                                      // future Updates
                                Logger(responseTypeEnum.fromServer, "New Player Spawned " + playerBot.name);
                            }
                        }
                    }

                    // * Checks if the read Command has a "healObject" JSON object in it
                    if (command.has("healObject")) {
                        new healObject(gp, player, gp.multiplayerAIArray, command); // create a new healObject on Client
                    }
                }
                // ! incase the server input is "finished" quit out of the loop, there is no new
                if (serverInput.equals("finished")) {
                    break; // ! Break out of the Loop
                }
            }
        }
    }
}

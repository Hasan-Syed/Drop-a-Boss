package multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Entity;
import entity.multiplayerBot;
import main.gamePanel;
import main.enums.responseTypeEnum;

/**
 * The Multiplayer Class Connects to the Server and Updates, Casts Abilities of
 * Targets on the Entities on remote Casters Behalfs.
 * 
 * @author Hasan Syed
 * @version 1.2
 */
public class multiplayer extends Thread {
    public int onlineID;

    public final String myIP = InetAddress.getLocalHost().getHostAddress();
    public final ServerSocket serverListener = new ServerSocket(6868);

    public Boolean stillAlive = false;

    public final Socket playerToServer; // Data to Server
    public final PrintWriter toServerReader; // Data to Server
    public final Socket ServerToPlayer; // Data from server
    public final BufferedReader fromServerReader; // Data from Server

    public JSONObject dataToBeSent;

    public final Entity player;
    public final gamePanel gp;

    /**
     * 
     * @param IP   The IP Address the Server is listening on
     * @param PORT The Port the Server is listening on
     * @param gp   The Game Panel
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
        System.out.print(ServerToPlayer);
        this.fromServerReader = new BufferedReader(new InputStreamReader(ServerToPlayer.getInputStream())); // Initialize
                                                                                                            // Server
                                                                                                            // Response
                                                                                                            // Reader
        dataToBeSent = new JSONObject();
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
        writeServer((String) player.getEntityJSON().toString()); // Send Player Entity JSON
        // Get Abilities \\
        Logger(responseTypeEnum.toServer, "Asking for Abilities");
        writeServer("abilities"); // Send playerEntity Request to server
        String json = (String) readServer();
        gp.abilities = new JSONArray(json);
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
    public synchronized Object readServer() {
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
    public synchronized void writeServer(Object toServer) {
        toServerReader.println(toServer); // Write to Server
        dataToBeSent.clear();
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
            dataToBeSent.put("playerUpdate", player.getEntityJSON());
            dataToBeSent.put("castedAbility", player.entityCastedAbilities());
            /*
             * Send the Current 'Players main Entity' the Server
             */
            writeServer(new JSONObject().put("gameUpdate", dataToBeSent));
            /*
             * Read Server Input
             */
            JSONObject gameUpdate = new JSONObject((String) readServer());
            {
                /**
                 * Reading (remote) player updates, updating the (remote) Players;
                 * 1. Location
                 * 2. (min/max) Health Value
                 * 3. (min/max) Mana/Power Values (not implemented yet)
                 * 4. (facing) Direction
                 * 5. entity type
                 */
                if (gameUpdate.has("playerUpdate")) {
                    JSONArray playerUpdates = gameUpdate.getJSONArray("playerUpdate");
                    for (Object playerObject : playerUpdates) {
                        JSONObject playerUpdate = (JSONObject) playerObject;
                        if (playerUpdate.has("ID") && (playerUpdate.getInt("ID") != onlineID)) {
                            int ID = playerUpdate.getInt("ID");
                            boolean found = false;
                            for (multiplayerBot bot : gp.multiplayerAIArray) {
                                if (bot.ID == ID) {
                                    bot.setEntityUpdate(playerUpdate);
                                    found = true;
                                }
                            }
                            if (!found) {
                                multiplayerBot mb = new multiplayerBot(gp, ID);
                                mb.setEntityUpdate(playerUpdate);
                                gp.multiplayerAIArray.add(mb);
                            }
                        }
                    }
                }
                /*
                 * Abilities
                 */
                // Check if the gameUpdate has castedAbility
                if (gameUpdate.has("castedAbility")) {
                    JSONArray castedAbility = gameUpdate.getJSONArray("castedAbility");
                    for (var index = 0; index < castedAbility.length(); index++) {
                        System.out.println(gp.player.healthManipulate.addToQueue(castedAbility.getJSONObject(index)));
                        castedAbility.remove(index);
                    }
                }
            }
        }
    }
}
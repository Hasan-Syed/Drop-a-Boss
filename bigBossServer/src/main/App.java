package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import clients.player;
import discord.discordConnect;

public class App extends Thread {
    DateTimeFormatter dtf;
    public int port = 6969; // Tje Port the Server is Listenting on

    public int maxPlayers = 6; // Max Player Limit
    public List<player> clientList = new ArrayList<>(); // Client List
    public List<JSONObject> abilityList = new ArrayList<>(); // All Abilities Jsons will be loaded here and sent to the
                                                             // clients
    public ExecutorService pool = Executors.newFixedThreadPool(maxPlayers); // Executoner Thread Pool

    public List<JSONObject> diliveredJsonObjects = new ArrayList<>();
    public JSONObject deliveryObject = new JSONObject();

    public int playerIndex = 0; // Player Index

    /**
     * * Construct
     * Starts up the Server, Listens on 'Port', Accepts Requests and Creates a new
     * Player Object, and ents it to the Execution Pool List
     */
    App() {
        init();
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

    /**
     * Loads the Ability JSON to a LIST
     */
    void loadAbilityJSON() {
        logger("[Load Ability JSON]: Loading Ability Json File"); // Log
        // Try to Read the Ability JSON file
        try (InputStreamReader is = new InputStreamReader(
                new FileInputStream("resources/abilityResources/abilities.json"))) {
            // Prepare JSON Object
            JSONTokener tokener = new JSONTokener(is);
            JSONObject object = new JSONObject(tokener);
            JSONArray abilities = object.getJSONArray("abilities"); // Get Abilities off of the JSON Object
            // Loop through the abilities
            for (Object abilityObject : abilities) {
                // Add to Ability List
                abilityList.add((JSONObject) abilityObject);
            }
        } catch (JSONException | IOException e) {
            logger("[Load Ability JSON]: " + e.getMessage()); // Log
        }
    }

    /**
     * Load the Discord JSON, and Using information off of the JSON to Connect to
     * Discord and start the Discord Service
     */
    void loadDiscordJSON() {
        logger("[Load Discord JSON]: Loading Discord Json File"); // Log
        // Try to Read the Discord JSON file
        try (InputStreamReader is = new InputStreamReader(
                new FileInputStream("resources/serverOnlineResources/discordAPI.json"))) {
            // Prepare Json Object
            JSONTokener tokener = new JSONTokener(is);
            JSONObject object = new JSONObject(tokener);
            // Load Discord Connect
            new discordConnect(object, this);
        } catch (JSONException | IOException e) {
            logger("[Load Discord JSON]: " + e.getMessage()); // Log
        }
    }

    /**
     * Init() is used to Initialize the Class
     */
    void init() {
        dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        logger("[Init]: Initializing the App Class, and Resources"); // Log
        logger("[Init]: Checking for missing Files"); // Log
        initFileChecker(); // Check for missing Files
        logger("[Init]: Loading player Abilities Files"); // Log
        loadAbilityJSON(); // Load Player Abilities File
        logger("[Init]: Loading Discord JSON, and then Discord"); // Log
        loadDiscordJSON(); // Load Discord JSON, and then Discord
    }

    /**
     * The Required File List is Created, it is then used to check if all the
     * required files {@code requiredFilePaths} all exist.
     * <p>
     * If any are missing
     * {@code createNewFile()} is called to create the required File with all
     * required context.
     */
    void initFileChecker() {
        // Create the Required File List
        List<String> requiredFilePaths = new ArrayList<>();
        requiredFilePaths.add("resources/abilityResources/abilities.json"); // Add to Required File List
        requiredFilePaths.add("resources/serverOnlineResources/discordAPI.json"); // Add to Required File List

        // Check for Files
        logger("[Init File Checker]: Checking for required files..."); // Log
        for (String requiredFilePath : requiredFilePaths) {
            if (!new File(requiredFilePath).exists()) {
                String[] missingFileData = requiredFilePath.split("/"); // Split the File Path to get the File Name
                createNewFile(missingFileData, requiredFilePath); // Create a new File
                // log
                logger("[Init File Checker]: The Missing File Has been Created, the Program will now exit, please fill out the required fields (They are all required)");
                JOptionPane.showMessageDialog(null,
                        "The Missing File Has been Created, the Program will now exit, please fill out the required fields (They are all required) \n "
                                + requiredFilePath,
                        "[Error] Files Missing", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        logger("[Init File Checker]: File check complete"); // Log
    }

    /**
     * Creates a Fresh Layout JSONObject for Files, Creates the new File, and Prints
     * the JSON on it
     * 
     * @param requiredFilePath The missing files file path
     * @param fileName         The missing files name
     */
    void createNewFile(String[] missingFileData, String missingFilePath) {
        String fileName = missingFileData[missingFileData.length - 1];
        String fullPath = missingFilePath;
        logger("[File Creator]: Creating new file; fn: " + fileName + ", fp: " + fullPath); // Log
        try {
            logger("[File Creator] [" + fileName + "]: Attempting to Create File"); // Log
            File missingFile = new File(missingFilePath); // Create the missingFile File Object
            missingFile.getParentFile().mkdirs(); // Create Missing Directories
            missingFile.createNewFile(); // Create the missingFile File
            logger("[File Creator] [" + fileName + "]: File Created"); // Log
            switch (missingFileData[missingFileData.length - 1]) {
                case "discordAPI.json" -> {
                    JSONObject discordAPIFile = new JSONObject(); // Create the missingFile Content JSON Object
                    discordAPIFile.put("token", ""); // File Content
                    PrintWriter writer = new PrintWriter(missingFilePath); // Open the recently created File
                    writer.println(discordAPIFile.toString()); // Print Data on the File
                    writer.flush(); // Flush the writer
                    writer.close(); // close the writer
                }
            }
        } catch (Exception e) {
            logger("[Error] There was a problem creating " + fileName + " on the path: " + fullPath); // Log
            logger("[Error] " + e.getMessage()); // Log
        }
    }

    /**
     * Pretty Self Explanatory
     * 
     * @param log The Log
     */
    void logger(String log) {
        System.out.println(dtf.format(LocalDateTime.now()) + ": [Multiplayer][App Class]: " + log);
    }

    public static void main(String[] args) throws Exception {
        new App();
    }
}

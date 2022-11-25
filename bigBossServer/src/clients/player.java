package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class player extends Thread {
    JSONObject jo = new JSONObject(
            "{\"playerUpdate\":{\"direction\":\"facingUp\",\"entityType\":\"ai\",\"name\":\"serverEntity\",\"x\":-10,\"y\":-10},\"ID\":99}");
    public final int ID;
    public final Socket playerClient;
    public final BufferedReader fromPlayer;
    public final PrintWriter toPlayer;
    public List<JSONObject> allPlayerEvents;
    public final App mainClass;

    public String playerName;

    public player(int ID, Socket player, List<JSONObject> playerEvents, App mainClass) throws IOException {
        this.ID = ID;
        this.playerClient = player;
        this.fromPlayer = new BufferedReader(new InputStreamReader(playerClient.getInputStream()));
        this.toPlayer = new PrintWriter(playerClient.getOutputStream(), true);
        this.allPlayerEvents = playerEvents;
        this.mainClass = mainClass;
        System.out.println("[Server Join]: " + player + " has joined as ID: '" + ID + "'");
        try {
            initializePlayer();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public void initializePlayer() throws IOException {
        System.out.println("[Server Initialize ID: '" + ID + "']: Iniitializing Player");
        toPlayer.println("Connection Established"); // Send Player Connection Established
        {
            // Now the player is going to Request its ID on the server \\
            String tempRead = fromPlayer.readLine().trim();
            if (tempRead.equalsIgnoreCase("getID")) { // When the Player Requests it's ID
                toPlayer.println(ID); // Send Player ID
            }
            // The Server will now Request the Initial Entity JSON from the Player
            toPlayer.println("Initial Entity"); // Send Player Connection Established
            tempRead = fromPlayer.readLine();
            System.out.println("Initial Read: " + tempRead);
            if (isValid(tempRead)) {
                if (new JSONObject(tempRead).has("playerUpdate")) {
                    mainClass.allPlayerEventsNew.add(new JSONObject(tempRead));
                    mainClass.allPlayerNewEventsRecorded.add(true);
                    toPlayer.println("Initial Entity Recived");
                    allPlayerEvents.add(new JSONObject(tempRead));
                    JSONObject initialEntity = new JSONObject(tempRead).getJSONObject("playerUpdate");
                    playerName = initialEntity.getString("name");
                    System.out.println(
                            "[Server Initialize ID: '" + ID + "' or name: '" + playerName
                                    + "']: Initial Setup Finished");
                }
                this.start();
            }
        }
    }

    public boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public void run() {
        while (playerClient.isConnected()) {
            try {
                String check = fromPlayer.readLine();
                System.out.println(check);
                if (check != null) {
                    mainClass.allPlayerEventsNew.add(new JSONObject(check));
                    mainClass.allPlayerNewEventsRecorded.add(true);
                    JSONObject newlyRecived = new JSONObject(fromPlayer.readLine()); // Recieve Player Command
                    newlyRecived.put("ID", ID);
                    allPlayerEvents.add(newlyRecived); // Add to All Player Events
                    for (player multiList : mainClass.clientList) {
                        multiList.toPlayer.println(newlyRecived);
                    }
                    if (mainClass.clientList.size() >= 1) {
                        toPlayer.println(jo);
                    }
                }
                mainClass.run();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

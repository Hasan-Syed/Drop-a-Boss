package online;

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

public class clients implements Runnable {

    List<JSONObject> playerEvents;
    private Socket client;
    private int ID;
    private BufferedReader clientInput;
    private PrintWriter serverResponse;
    main.App App;
    JSONObject jo = new JSONObject(
            "{\"playerUpdate\":{\"direction\":\"facingUp\",\"entityType\":\"ai\",\"name\":\"serverEntity\",\"x\":-10,\"y\":-10},\"ID\":0}");
    public Boolean playerActive;
    public JSONObject newCommand;

    public clients(Socket clientSocket, int ID, List<JSONObject> playerEvents, main.App App) {
        try {
            this.client = clientSocket;
            this.ID = ID;
            this.App = App;
            this.playerEvents = playerEvents;
            clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
            serverResponse = new PrintWriter(client.getOutputStream(), true);
            serverResponse.println("Connection Established");
            // Initial ID Request Reply
            String idCheck = clientInput.readLine();
            if (idCheck.equals("request ID")) {
                serverResponse.println(ID);
            }
            // Request initial Player Entity
            {
                serverResponse.println("Initial Player Entity");
                JSONObject initialPlayerObject = new JSONObject(clientInput.readLine());
                JSONObject initialPlayerObjectInnerds = (JSONObject) initialPlayerObject.getJSONObject("playerUpdate");
                System.out.println("[Server Joined]: " + (String) initialPlayerObjectInnerds.getString("name")
                        + ", ID: " + ID + ", has Joined the game");
                App.playerEventsStorageList.get(ID).add(initialPlayerObject);
            }
            // Start Sending Data
            for (clients players : App.clientList) {
                System.out.println("[Server on ID: " + ID + "'s behalf]: Spawning a new Entity");
                players.serverResponse.println("{\"spawnNewEntity\":\"true\", \"ID\": \"" + ID + "\"}");
            }
            if (App.playerIndex >= 1) {
                for (List<JSONObject> otherPlayers : App.playerEventsStorageList) {
                    if (otherPlayers.size() > 10) {
                        JSONObject plrSpawn = otherPlayers.get(1);
                        serverResponse.println("{\"spawnNewEntity\":\"true\", \"ID\": \"" +
                                plrSpawn.getInt("ID") + "\"}");
                    }
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
            try {
                client.close();
                App.pool.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.getStackTrace();
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

    @Override
    public void run() {
        double drawInterval = 1000000000 / 60;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (client.isConnected()) {
            App.allsent.add(false);
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                try {
                    JSONObject read = new JSONObject(clientInput.readLine()); // get Player Event
                    if (!read.has("movement")) {
                        read.put("ID", ID);
                        playerEvents.add(read); // Add Player event to the Plyer Events List;
                        App.playerEvents.put(read);
                        App.allsent.set(ID, true);
                        newCommand = read;
                        serverResponse.println(App.playerEvents);
                    } else {
                        System.out.println(read);
                    }
                    // for (clients latestPlayerEvents : App.clientList) {
                    // JSONObject latestEvent = latestPlayerEvents.newCommand;
                    //
                    // int eventOwnerID = latestEvent.getInt("ID");
                    // if (eventOwnerID != ID) {
                    // serverResponse.println(App.playerEvents);
                    // App.allsent.set(ID, true);
                    // }
                    // }
                    if (App.playerIndex <= 1) {
                        serverResponse.println("{\"ID\":99}");
                    }
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
            App.run();
        }

    }

}

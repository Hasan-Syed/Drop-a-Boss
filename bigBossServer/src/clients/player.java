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
    JSONObject jo = new JSONObject(
            "{\"playerUpdate\":{\"direction\":\"facingUp\",\"entityType\":\"ai\",\"name\":\"serverEntity\",\"x\":-10,\"y\":-10},\"ID\":99}");
    public List<JSONObject> playerEvents;
    public List<JSONObject> playerPositioningEvents;
    public List<JSONObject> playerAbilityEvents;

    public final App mainClass;
    public final int ID;
    public String playerName;

    public final Socket playerToServer; // To Server Client
    public final BufferedReader fromPlayerReader; // To Server Client
    public final Socket ServerToPlayer; // From Server Client
    public final PrintWriter toPlayerWriter; // From Server Client

    Object fromPlayer;
    Object toPlayer;

    public player(int ID, Socket player, List<JSONObject> playerEvents, App mainClass) throws IOException {
        this.ID = ID;
        this.mainClass = mainClass;
        this.playerEvents = playerEvents;
        this.playerPositioningEvents = new ArrayList<>();
        this.playerAbilityEvents = new ArrayList<>();

        this.playerToServer = player;
        logger(debugging.fromClient,
                "Connected to IP: " + playerToServer.getInetAddress() + " The Entity has been given the ID: " + ID);
        this.fromPlayerReader = new BufferedReader(new InputStreamReader(playerToServer.getInputStream()));
        this.ServerToPlayer = new Socket(playerToServer.getInetAddress(), 7070);
        logger(debugging.toClient, "[ID: " + ID + "]: Connected back to IP: " + playerToServer.getInetAddress());
        this.toPlayerWriter = new PrintWriter(ServerToPlayer.getOutputStream(), true);

        init();
        // this.allPlayerEvents = playerEvents;
        // this.mainClass = mainClass;
        // logger(debugging.initiatingClient, "[Server Join]: " + player + " has joined
        // as ID: '" + ID + "'");
    }

    void readPlayer() {
        try {
            fromPlayer = fromPlayerReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            this.stop();
        }
    }

    void writePlayer() {
        toPlayerWriter.println(toPlayer);
    }

    void init() {
        boolean initializing = true;
        readPlayer();
        while (initializing) {
            switch (new String((String) fromPlayer)) {
                case "id" -> {
                    toPlayer = ID; // Set Player ID to Sender
                    writePlayer(); // Send the ID's
                }
                case "playerName" -> {
                    readPlayer(); // Wait for Player Name to be sent
                    playerName = (String) fromPlayer; // Get Playername
                }
                case "playerEntity" -> {
                    readPlayer(); // Wait for Player Name to be sent
                    if (isValid((String) fromPlayer)) {
                        JSONObject player = new JSONObject((String) fromPlayer);
                        player.put("ID", ID);
                        playerPositioningEvents.add(player); // Add Player's Event
                        // System.out.println(fromPlayer);
                    }
                }
                case "initComplete" -> {
                    initializing = false;
                }
            }
            if (initializing) {
                readPlayer();
            }
        }
        this.start();
    }

    public boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    enum debugging {
        toClient,
        fromClient,
        initiatingClient,
        internalAlternatives
    }

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

    boolean sendMe = true;

    public void run() {
        while (true) {
            readPlayer();
            if (fromPlayer != null) {
                JSONObject Event = new JSONObject((String) fromPlayer);
                if (Event.has("playerUpdate")) {
                    Event.put("ID", ID);
                    playerPositioningEvents.add(Event);
                    playerEvents.add(Event);
                    sendMe = true;
                }
                if (Event.has("abilityEvent")) {
                    playerAbilityEvents.add(Event);
                    playerEvents.add(Event);
                }
                if (Event.has("noMovesMade")) {
                    sendMe = false;
                }
            }
            if (mainClass.playerIndex >= 1) {
                JSONArray playerSpawns = new JSONArray();
                for (player players : mainClass.clientList) {
                    if (players.ID != ID) {
                        playerSpawns
                                .put(players.playerPositioningEvents.get(players.playerPositioningEvents.size() - 1));
                    }
                    if (players.ID == ID && sendMe) {
                        playerSpawns
                                .put(players.playerPositioningEvents.get(players.playerPositioningEvents.size() - 1));
                    }
                }
                toPlayer = (JSONArray) playerSpawns;
                System.out.println(toPlayer);
                writePlayer();
            }
        }
    }
}//

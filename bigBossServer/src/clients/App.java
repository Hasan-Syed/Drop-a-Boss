package clients;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

public class App extends Thread {
    public int maxPlayers = 6;
    public List<player> clientList = new ArrayList<>();
    public ExecutorService pool = Executors.newFixedThreadPool(maxPlayers);

    public List<JSONObject> allPlayerEventsNew = new ArrayList<JSONObject>();
    public List<Boolean> allPlayerNewEventsRecorded = new ArrayList<Boolean>();

    public List<List<JSONObject>> playerEventsStorageList = new ArrayList<List<JSONObject>>();

    public int playerIndex = -1;
    public boolean newPlayer;

    App() {
        try (ServerSocket serverListener = new ServerSocket(6969)) {
            while (true) {
                playerIndex++;
                Socket client = serverListener.accept();
                List<JSONObject> playerEvents = new ArrayList<>();
                player newPlayer = new player(playerIndex, client, playerEvents, this);
                clientList.add(newPlayer);

                pool.execute(newPlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new App().start();
        // app.start();
    }
}

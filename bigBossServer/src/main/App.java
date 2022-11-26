// package main;
//
// import java.net.ServerSocket;
// import java.net.Socket;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
//
// import org.json.JSONArray;
// import org.json.JSONObject;
//
// import online.clients;
//
// public class App extends Thread {
//
// public List<clients> clientList = new ArrayList<>();
// public ExecutorService pool = Executors.newFixedThreadPool(6);
//
// public List<List<JSONObject>> playerEventsStorageList = new
// ArrayList<List<JSONObject>>();
//
// public JSONArray playerEvents = new JSONArray();
// public List<Boolean> allsent = new ArrayList<>();
//
// public int playerIndex = -1;
// public boolean newPlayer;
//
// App() {
// try (ServerSocket serverListener = new ServerSocket(6969)) {
// while (true) {
// playerIndex++;
// Socket client = serverListener.accept();
// List<JSONObject> playerEvents = new ArrayList<>();
// newPlayer = true;
// playerEventsStorageList.add(playerEvents);
// clients newClient = new clients(client, playerIndex, playerEvents, this);
// clientList.add(newClient);
// System.out.print("");
// pool.execute(newClient);
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
//
// public boolean verifyAllEqualUsingALoop(List<Boolean> list) {
// System.out.println(list.size());
// for (Boolean s : list) {
// if (!s.equals(list.get(0)))
// return false;
// }
// return true;
// }
//
// public void run() {
// if (verifyAllEqualUsingALoop(allsent)) {
// System.out.println("Reset: " + playerEvents);
// allsent = new ArrayList<Boolean>();
// playerEvents = new JSONArray();
// }
// }
//
// public static void main(String[] args) throws Exception {
// App app = new App();
// // app.start();
// }
// }
//
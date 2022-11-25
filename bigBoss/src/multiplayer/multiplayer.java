package multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import entity.Entity;

public class multiplayer implements Runnable {
    public Socket playerConnect;
    public BufferedReader fromServer;
    public PrintWriter toServer;
    public String toServerStr;
    public String serverReturn;
    public int onlineID;

    public multiplayer(String IP, int PORT, Entity player) throws UnknownHostException, IOException {
        playerConnect = new Socket(IP, PORT);
        fromServer = new BufferedReader(new InputStreamReader(playerConnect.getInputStream()));
        toServer = new PrintWriter(playerConnect.getOutputStream(), true);
        String initialConversation = fromServer.readLine();
        System.out.println(initialConversation);
        // Request for ID
        toServer.println("getID");
        onlineID = Integer.parseInt(fromServer.readLine());
        System.out.println(onlineID);
        // Send First Request
        toServer.println(player.entityJson());
        System.out.println(fromServer.readLine());
    }

    public void closeConnection() {
        toServer.println("pleaseLeaveMeAlone");
    }

    @Override
    public void run() {
        boolean run = true;
        while (run) {
            try {
                toServer.println(toServerStr);
                serverReturn = fromServer.readLine();
                run = false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

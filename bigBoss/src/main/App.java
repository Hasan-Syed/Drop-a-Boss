package main;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.json.JSONArray;

public class App extends JFrame {

    public gamePanel gamePanel;

    public App(String playerName, String serverIP, int serverPort) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("BIG BOSS");
        setResizable(true);

        gamePanel = new gamePanel(playerName, serverIP, serverPort);
        add(gamePanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
        gamePanel.startGameThread();
    }

    static JSONArray getConnectionInfo() {
        JSONArray returnInfo = new JSONArray();
        {
            // GET Port
            String userName = JOptionPane.showInputDialog(null, "Enter Username");
            if (userName != null) {
                returnInfo.put(0, userName);
            } else {
                returnInfo.put(0, "unknown");
            }
            // GET IP Address
            String IP = JOptionPane.showInputDialog(null, "Enter an IP address");
            if (IP != null) {
                returnInfo.put(1, IP);
            } else {
                returnInfo.put(1, "127.0.0.1");
            }
            // GET Port
            String PORT = JOptionPane.showInputDialog(null, "Enter connection port");
            if (PORT != null) {
                returnInfo.put(2, PORT);
            } else {
                returnInfo.put(2, "6969");
            }
        }
        return returnInfo;
    }

    public static void main(String[] args) {
        String playerName = "unknown";
        String serverIP = "127.0.0.1";
        int serverPort = 6969;
        try {
            if (args.length > 0) {
                playerName = args[0];
                serverIP = args[1];
                serverPort = Integer.parseInt(args[2]);
            } else {
                JSONArray array = getConnectionInfo();
                playerName = array.getString(0);
                serverIP = array.getString(1);
                serverPort = array.getInt(2);
            }
        } catch (Exception e) {

        }
        new App(playerName, serverIP, serverPort);
    }
}

package main;

import javax.swing.JFrame;

public class App extends JFrame {

    public gamePanel gamePanel;

    public App(String playerName, String serverIP, int serverPort, int threadSleepTime) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("BIG BOSS");
        setResizable(true);

        gamePanel = new gamePanel(playerName, serverIP, serverPort, threadSleepTime);
        add(gamePanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
        gamePanel.startGameThread();
    }

    public static void main(String[] args) {
        String playerName = "unknown";
        String serverIP = "25.14.18.191";
        int serverPort = 6969;
        int threadSleepTime = 15;
        try {
            if (args.length > 0) {
                playerName = args[0];
                serverIP = args[1];
                serverPort = Integer.parseInt(args[2]);
                threadSleepTime = Integer.parseInt(args[3]);
            } else {
                playerName = "unknown";
                serverIP = "127.0.0.1";
                serverPort = 6969;
                threadSleepTime = 15;
            }
        } catch (Exception e) {

        }
        new App(playerName, serverIP, serverPort, threadSleepTime);
    }
}

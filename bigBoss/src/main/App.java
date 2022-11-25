package main;

import javax.swing.JFrame;

public class App extends JFrame {

    public gamePanel gamePanel;

    public App(String playerName) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("BIG BOSS");
        setResizable(true);

        gamePanel = new gamePanel(playerName);
        add(gamePanel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);
        gamePanel.startGameThread();
    }

    public static void main(String[] args) {
        String playerName = "";
        try {
            if (args.length > 0) {
                playerName = args[0];
            } else {
                playerName = "unknown";
            }
        } catch (Exception e) {

        }
        new App(playerName);
    }
}

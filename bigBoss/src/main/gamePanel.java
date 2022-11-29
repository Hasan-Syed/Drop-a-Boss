package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import HUD.HUD;
import entity.Player;
import entity.multiplayerBot;
import inputHandleing.KeyHandler;
import inputHandleing.mouseHandler;
import inputHandleing.mouseMotionHandler;
import multiplayer.multiplayer;
import tile.tileManager;

public class gamePanel extends JPanel implements Runnable {
    // Multiplayer
    public multiplayer multiplayer;
    // Screen Settings
    final int originalTileSize = 16; // the Tile Size
    final int scale = 3; // Tile Scale Settings

    public final int tileSize = originalTileSize * scale; // Tile's scaled resolution
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // Screen Width
    public final int screenHeight = tileSize * maxScreenRow; // Screen Height
    // World Settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    final int threadSleepTime;

    public boolean moveMade = true;

    public Thread gameThread; // Game Loop
    public CollisionChecker cChecker = new CollisionChecker(this);
    public mouseHandler mouseH = new mouseHandler();
    public KeyHandler keyH = new KeyHandler(); // Keyboard Handler
    public mouseMotionHandler mouseMotionH = new mouseMotionHandler(); // Motion Mouse Listener
    // Load World Items
    public tileManager tileM = new tileManager(this, mouseMotionH); // World Loader
    public Player player;
    public HUD hud;

    public List<multiplayerBot> multiplayerAIArray = new ArrayList<>();

    public gamePanel(String playerName, String serverIP, int serverPort, int threadSleepTime) {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set Screen Size
        this.setBackground(Color.BLACK); // Background Color;
        this.setDoubleBuffered(true); // Paint Setting
        this.addKeyListener(keyH); // Add Key Listener
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseMotionH);
        this.threadSleepTime = threadSleepTime;
        setFocusable(true);
        player = new Player(this, playerName); // Player
        hud = new HUD(this, player);

        try {
            multiplayer = new multiplayer(serverIP, serverPort, player, this);
            multiplayer.start();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public synchronized void run() {
        // Thread Sleep Method \\
        while (gameThread != null) {
            try {
                Thread.sleep(threadSleepTime);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            update(); // update gameInformation
            repaint(); // draw Updates
        }
    }

    private synchronized void update() {
        hud.update();
        tileM.update(); // World Updates
        player.update(); // Update Player Information
        for (multiplayerBot playerAI : multiplayerAIArray) {
            playerAI.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Draw World;
        tileM.draw(g2d);
        // Draw Player
        player.draw(g2d);
        // Draw Multiplayer Players
        for (multiplayerBot playerAI : multiplayerAIArray) {
            if (playerAI.ID != multiplayer.onlineID) {
                playerAI.draw(g2d);
            }
        }
        g2d.drawString("Multiplayer Enttities: " + multiplayerAIArray.size(), maxScreenCol, ABORT);
        g2d.dispose();
    }
}

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.RenderingHints;

import HUD.HUD;
import abilities.Abilities.flash;
import abilities.Abilities.heal;
import entity.Player;
import entity.multiplayerBot;
import inputHandleing.KeyHandler;
import inputHandleing.mouseHandler;
import inputHandleing.mouseMotionHandler;
import multiplayer.multiplayer;
import tile.tileManager;

/**
 * The Base Game Base
 * 
 * @author Hasan Syed
 * @version 2.0
 */
public class gamePanel extends JPanel implements Runnable {
    // Multiplayer
    public multiplayer multiplayer;
    // Screen Settings
    final int originalTileSize = 16; // the Tile Size
    final int scale = 3; // Tile Scale Settings
    public JSONArray abilities;
    public final int tileSize = originalTileSize * scale; // Tile's scaled resolution
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // Screen Width
    public final int screenHeight = tileSize * maxScreenRow; // Screen Height
    public Dimension screenSize;
    // World Settings
    public final int maxWorldCol = 87;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;
    public int cameraViewX;
    public int cameraViewY;

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

    public gamePanel(String playerName, String serverIP, int serverPort) {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set Screen Size
        this.setBackground(Color.BLACK); // Background Color;
        this.setDoubleBuffered(true); // Paint Setting
        this.addKeyListener(keyH); // Add Key Listener
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseMotionH);
        setFocusable(true);
        screenSize = this.getSize();
        player = new Player(this, playerName); // Player
        hud = new HUD(this, player);
        try {
            multiplayer = new multiplayer(serverIP, serverPort, player, this);
            player.loadAbility(new heal(this, player, (JSONObject) abilities.get(1), mouseMotionH, mouseH, keyH));
            player.loadAbility(new flash(this, player, (JSONObject) abilities.get(0), mouseMotionH, mouseH, keyH));

            player.ID = multiplayer.onlineID;
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
        gameThread.setName("Base Game Thread");
        gameThread.start();
    }

    @Override
    public synchronized void run() {
        // Thread Sleep Method \\
        while (gameThread != null) {
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            update(); // update gameInformation
            repaint(); // draw Updates
        }
    }

    private synchronized void update() {
        screenSize = this.getSize();
        player.update(); // Update Player Information
        hud.update();
        tileM.update(); // World Updates
        for (multiplayerBot playerAI : multiplayerAIArray) {
            playerAI.update();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHints(hints);

        // Draw World;
        tileM.draw(g2d);
        // Draw Player
        player.draw(g2d);
        // Draw Multiplayer Players
        for (multiplayerBot playerAI : multiplayerAIArray) {
            playerAI.draw(g2d);
        }
        hud.draw(g2d);

        g2d.dispose();

    }
}

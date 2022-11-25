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

import org.json.JSONException;
import org.json.JSONObject;

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

    final int FPS_CAP = 60;
    int FPS = 0;

    Thread gameThread; // Game Loop
    public CollisionChecker cChecker = new CollisionChecker(this);
    public mouseHandler mouseH = new mouseHandler();
    public KeyHandler keyH = new KeyHandler(); // Keyboard Handler
    public mouseMotionHandler mouseMotionH = new mouseMotionHandler(); // Motion Mouse Listener
    // Load World Items
    public tileManager tileM = new tileManager(this, mouseMotionH); // World Loader
    public Player player;
    public HUD hud;

    public List<multiplayerBot> multiplayerAIArray = new ArrayList<>();

    public gamePanel(String playerName) {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Set Screen Size
        this.setBackground(Color.BLACK); // Background Color;
        this.setDoubleBuffered(true); // Paint Setting
        this.addKeyListener(keyH); // Add Key Listener
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseMotionH);
        setFocusable(true);
        player = new Player(this, playerName); // Player
        hud = new HUD(this, player);
        try {
            multiplayer = new multiplayer("25.10.205.149", 6969, player);
            multiplayer.run();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public boolean test = true;

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS_CAP;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if (delta >= 1) {
                System.out.println(player.entityJson().toString());
                multiplayer.toServerStr = player.entityJson().toString();
                multiplayer.run();
                System.out.print(multiplayer.serverReturn);
                JSONObject serverInput = new JSONObject(multiplayer.serverReturn);
                if (serverInput.has("playerUpdate")) {
                    boolean playerFound = false;
                    int ID = serverInput.getInt("ID");
                    for (multiplayerBot remoteEntity : multiplayerAIArray) {
                        if (remoteEntity.ID == ID) {
                            remoteEntity.update(serverInput);
                            playerFound = true;
                        }
                    }
                    if (playerFound == false && ID != multiplayer.onlineID) {
                        multiplayerBot remoteEntity = new multiplayerBot(this, ID);
                        remoteEntity.update(serverInput);
                        multiplayerAIArray.add(remoteEntity);
                    }
                }
                // this.multiplayer.toServerStr = null;
                // if (multiplayer.playerConnect.isConnected()) {
                // this.multiplayer.toServerStr = this.player.entityJson().toString();
                // this.multiplayer.run();
                // }
                //// Server Response
                // if (isValid(multiplayer.serverReturn)) {
                // JSONObject newEvent = new JSONObject(multiplayer.serverReturn);
                // System.out.println(newEvent);
                // } else {
                // JSONArray newEvent = new JSONArray(multiplayer.serverReturn);
                // System.out.println(newEvent);
                // }
                // int ID = newEvent.getInt("ID");
                // if (newEvent.has("spawnNewEntity")) {
                // System.out.println("new Player Spawned");
                // multiplayerAIArray.add(new multiplayerBot(this, ID));
                // }
                // if (newEvent.has("playerUpdate")) {
                // for (multiplayerBot remoteEntity : multiplayerAIArray) {
                // if (remoteEntity.ID == ID) {
                // remoteEntity.update(newEvent);
                // }
                // }
                // }

                update(); // update gameInformation
                repaint(); // draw Updates
                delta--;
                drawCount++;
            }
        }
        if (timer >= 1000000000) {
            System.out.println("FPS: " + drawCount);
            drawCount = 0;
            timer = 0;
        }
    }

    public void update() {
        hud.update();
        tileM.update(); // World Updates
        player.update(); // Update Player Information
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

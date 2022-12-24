package tile;

import java.util.ArrayList;
import java.util.List;

import inputHandleing.mouseMotionHandler;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileReader;
import java.io.BufferedReader;

import main.gamePanel;
import main.enums.collisionPenalty;

public class tileManager {
    public boolean cameraUp = false, cameraDown = false, cameraLeft = false, cameraRight = false;
    gamePanel gp;
    mouseMotionHandler mouseMotionH;
    // List of Tile Types
    public List<tile> Tiles;
    public int[][] mapTileNum;

    List<Rectangle2D> sides;

    public tileManager(gamePanel gp, mouseMotionHandler mouseMotionH) {
        this.gp = gp;
        this.mouseMotionH = mouseMotionH;
        Tiles = new ArrayList<>();
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];
        sides = new ArrayList<>();
        loadTiles();
        loadMap("resources\\dataResources\\maps\\map01.txt");
    }

    public void loadTiles() {
        tile tempTile = null;
        /* ^ This temp Tile is reused and renewed to load Tiles ^ */
        // Loading Grass Block
        tempTile = new tile();
        tempTile.loadTile("resources\\worldResources\\grass.png");
        tempTile.tileName = "Grass";
        tempTile.collidable = false;
        tempTile.penalty = collisionPenalty.none;
        Tiles.add(tempTile); // Add the Grass Block to the Tiles List
        // Walk-way Block
        tempTile = new tile();
        tempTile.loadTile("resources\\worldResources\\walkway.png");
        tempTile.tileName = "walkway";
        tempTile.collidable = false;
        tempTile.penalty = collisionPenalty.none;
        Tiles.add(tempTile); // Add the Walk-way Block Block to the Tiles List
        // Load Wall Block
        tempTile = new tile();
        tempTile.loadTile("resources\\worldResources\\wall.png");
        tempTile.tileName = "wall";
        tempTile.collidable = true;
        tempTile.penalty = collisionPenalty.none;
        Tiles.add(tempTile); // Add the Wall Block to the Tiles List
    }

    // Level Loader
    public void loadMap(String mapLoc) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(mapLoc)); // Reader for Reading Map File
            int col = 0;
            int row = 0;

            while (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                String line = br.readLine();
                // Fill out X- axis
                while (col < gp.maxWorldCol) {
                    String numbers[] = line.split(" "); // Split the line om ' '(Space)
                    int tileID = Integer.parseInt(numbers[col]); // X- axis data
                    mapTileNum[col][row] = tileID; // Place on X- Axis
                    col++;
                }
                // Add to Y- Axis
                if (col == gp.maxWorldCol) {
                    col = 0;
                    row++;
                }
            }
            br.close(); // Close Reader
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // RESET CAMERA Movement
        cameraUp = false;
        cameraDown = false;
        cameraLeft = false;
        cameraRight = false;
        // Camera
        Rectangle2D tempRect = new Rectangle2D.Double();
        // Top Bar
        tempRect = new Rectangle2D.Double();
        tempRect.setFrame(0, 0, (gp.maxWorldCol * gp.maxWorldCol), gp.maxWorldRow);
        sides.add(tempRect);
        if (sides.get(0).intersects(mouseMotionH.mouseX, mouseMotionH.mouseY, 1, 1)) {
            cameraUp = true;
        }
        //
        // Right Bar
        tempRect = new Rectangle2D.Double();
        tempRect.setFrame(0, 0, gp.maxWorldCol, (gp.maxWorldRow * gp.maxWorldRow));
        sides.add(tempRect);
        if (sides.get(1).intersects(mouseMotionH.mouseX, mouseMotionH.mouseY, 1, 1)) {
            cameraRight = true;
        }
        // Bottom Bar
        tempRect = new Rectangle2D.Double();
        tempRect.setFrame(0, (gp.screenHeight - gp.tileSize), (gp.maxWorldCol * gp.maxWorldCol),
                gp.maxWorldRow);
        sides.add(tempRect);
        if (sides.get(2).intersects(mouseMotionH.mouseX, mouseMotionH.mouseY, 1, 1)) {
            cameraDown = true;
        }
        // Bottom Bar
        tempRect = new Rectangle2D.Double();
        tempRect.setFrame((gp.screenWidth - gp.tileSize), 0, gp.maxWorldCol,
                (gp.maxWorldRow * gp.maxWorldRow));
        sides.add(tempRect);
        if (sides.get(3).intersects(mouseMotionH.mouseX, mouseMotionH.mouseY, 1, 1)) {
            cameraLeft = true;
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.create();
        // World Building
        // int worldX = 0;
        // int worldY = 0;
        // gp.cameraViewX = (int) (gp.screenSize.getWidth() / 2) - gp.player.x;
        // gp.cameraViewY = (int) (gp.screenSize.getHeight() / 2) - gp.player.y;
        ////// Camera
        // switch (gp.player.camera) {
        // case locked -> {
        // g2d.translate(gp.cameraViewX, gp.cameraViewY);
        // }
        // case unlocked -> {
        //
        // }
        // }
        // MAP
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tile = mapTileNum[worldCol][worldRow];

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.position.x + gp.player.screenX;
            int screenY = worldY - gp.player.position.y + gp.player.screenY;

            g2d.drawImage(Tiles.get(tile).tile, screenX, screenY, gp.tileSize,
                    gp.tileSize, null);
            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
        // for (int windowCol = 0; windowCol != gp.maxWorldCol; windowCol++) {
        // worldY = 0;
        // for (int windowRow = 0; windowRow != gp.maxWorldRow; windowRow++) {
        // int tileID = mapTileNum[windowCol][windowRow];
        // g2d.drawImage(Tiles.get(tileID).tile, worldX, worldY, gp.tileSize,
        // gp.tileSize, gp);
        // worldY += gp.tileSize;
        // }
        // worldX += gp.tileSize;
        // }
    }
}

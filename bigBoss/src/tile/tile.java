package tile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.enums.collisionPenalty;

public class tile {
    public String tileName;
    public BufferedImage tile;
    public int x, y;
    public boolean collidable = false;
    public collisionPenalty penalty = collisionPenalty.none;
    public int negHealth, negSpeed = 0;

    /**
     * loadTile() is used to load a tile
     * 
     * @param tileAddress is the tile address
     */
    public void loadTile(String tileAddress) {
        try {
            tile = ImageIO.read(new File(tileAddress)); // Load the Tile Image
        } catch (IOException e) {
            e.printStackTrace(); // Print Error
        }
    }
}

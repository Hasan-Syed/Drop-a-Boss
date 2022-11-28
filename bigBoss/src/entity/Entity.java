package entity;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONObject;

import main.enums.spriteAdrs;
import main.gamePanel;
import main.enums.camera;
import main.enums.characterDirection;
import main.enums.entityTypeEnum;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

public class Entity {
    public gamePanel gp;
    public entityTypeEnum entityType;
    public int ID;
    public String name;
    public double maxHealth, health;
    public int x, y; // Player's X and Y Coords
    public int worldX, worldY;
    public int speed; // Player's Movement Speed
    public int negHealth = 0, negSpeed = 0;
    public characterDirection myDirection;
    public camera camera = main.enums.camera.locked;
    public boolean collision = false;;
    public Rectangle hitbox;
    // Sprite Related Variables
    public int currentSprite;
    public int spriteSpeed;
    public List<String> upSpriteAdrs, downSpriteAdrs, leftSpriteAdrs, rightSpriteAdrs;
    public List<BufferedImage> upSprite, downSprite, leftSprite, rightSprite;

    public Entity(gamePanel gp) {
        this.gp = gp;
    }

    // Initialize the Entity Class
    public void init() {
        // Initliaze the Sprite List Variables
        upSprite = new ArrayList<>();
        downSprite = new ArrayList<>();
        leftSprite = new ArrayList<>();
        rightSprite = new ArrayList<>();
        // Initialize the Sprite Address List Variables
        upSpriteAdrs = new ArrayList<>();
        downSpriteAdrs = new ArrayList<>();
        leftSpriteAdrs = new ArrayList<>();
        rightSpriteAdrs = new ArrayList<>();
        // Default hitbox
        hitbox = new Rectangle(x, y, gp.tileSize, gp.tileSize);
    }

    // Load Entity Sprites
    public void addSprite(spriteAdrs spriteType, List<String> spriteArds) {
        try {
            switch (spriteType) {
                case upSpriteAdrs -> {
                    for (String address : spriteArds) {
                        upSprite.add(ImageIO.read(new File(address)));
                    }
                }
                case downSpriteAdrs -> {
                    for (String address : spriteArds) {
                        downSprite.add(ImageIO.read(new File(address)));
                    }
                }
                case leftSpriteAdrs -> {
                    for (String address : spriteArds) {
                        leftSprite.add(ImageIO.read(new File(address)));
                    }
                }
                case rightSpriteAdrs -> {
                    for (String address : spriteArds) {
                        rightSprite.add(ImageIO.read(new File(address)));
                    }
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + spriteType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // JSON of the Enitiy
    public JSONObject entityJson() {
        JSONObject entityUpdateObject = new JSONObject();
        {
            JSONObject entityObject = new JSONObject();
            entityObject.put("name", name);
            entityObject.put("x", x);
            entityObject.put("y", y);
            entityObject.put("direction", myDirection.toString());
            entityObject.put("entityType", entityType.toString());
            entityObject.put("cH", health);
            entityObject.put("mH", maxHealth);
            entityUpdateObject.put("playerUpdate", entityObject);
        }
        return entityUpdateObject;
    }
}

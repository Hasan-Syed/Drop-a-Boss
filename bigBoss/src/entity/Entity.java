package entity;

// External Libraries
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.Point;
import org.json.JSONArray;
import org.json.JSONObject;
// Internal Libraries
import abilities.Ability.ability;
import appliars.gameBuffsAndDebuffs.healthManipulate;
import main.enums.spriteAdrs;
import main.enums;
import main.gamePanel;
import main.enums.camera;
import main.enums.characterDirection;
import main.enums.entityLogger;
import main.enums.entityTypeEnum;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

/**
 * Entity Base Class
 * 
 * @author Hasan Syed
 * @version 1.2
 */
public class Entity {
    public gamePanel gp;
    public entityTypeEnum entityType;
    public boolean active = false;
    public int ID;
    public String name;
    public double maxHealth, health, maxMana, mana, maxPower, power, overHeal;
    public boolean keepAlivePassiveHeal = true;
    public Point position = new Point();
    public int screenX, screenY;
    public int worldX, worldY;
    public int speed; // Player's Movement Speed
    public double changeHealth = 0, changeSpeed = 0;
    public JSONArray castedQueuedAbilities = new JSONArray();
    public characterDirection myDirection;
    public camera camera = main.enums.camera.locked;
    public boolean collision = false;;
    public Rectangle hitbox;
    // Sprite Related Variables
    public int currentSprite;
    public int spriteSpeed;
    public List<String> upSpriteAdrs, downSpriteAdrs, leftSpriteAdrs, rightSpriteAdrs;
    public List<BufferedImage> upSprite, downSprite, leftSprite, rightSprite;
    public List<ability> abilities;
    // Used Objects
    public healthManipulate healthManipulate = new healthManipulate(gp, this);

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
        hitbox = new Rectangle(position.x, position.y, gp.tileSize, gp.tileSize);
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

    /**
     * getEntityJSON, returns the Entities current JSON
     */
    public synchronized JSONObject getEntityJSON() {
        // New JSON Entity Object
        JSONObject entityObject = new JSONObject();
        {
            // Set Entity Name
            entityObject.put("name", name);
            // Set Entity Position
            {
                // Location Object
                JSONObject locationObject = new JSONObject();
                locationObject.put("x", position.getX()); // GET X
                locationObject.put("y", position.getY()); // GET Y
                // Add location Object to JSON
                entityObject.put("position", locationObject);
            }
            // Set Entity Direction
            entityObject.put("direction", myDirection.toString());
            // Set Entity Type
            entityObject.put("entityType", entityType.toString());
            // Set Entity Health
            entityObject.put("health", new JSONObject().put("max", maxHealth).put("current", health));
            // Set Entity ID
            entityObject.put("ID", ID);
        }
        return entityObject;
    }

    /**
     * setEntityUpdate, is used to (force) update an entity update
     * 
     * @param entityUpdate The entity Update JSONObject
     * @return True: If the playerUpdate changed the Values
     *         <p>
     *         False: If the playerUpdate didn't change any Values
     */
    public synchronized boolean setEntityUpdate(JSONObject entityUpdate) {
        // Check if name is provided
        if (entityUpdate.has("name")) {
            name = (String) entityUpdate.getString("name");
        } else {
            logger(entityLogger.setEntity,
                    "There was an error; The Entity Name was not given. The Entity will not be Spawned");
            logger(entityLogger.setEntity, entityUpdate);
            return false; // Entitiy name not found cancel spawn
        }
        // Check if position is provided
        if (entityUpdate.has("position")) {
            // Get Entity Position
            {
                // Get Position Object
                JSONObject locatiObject = entityUpdate.getJSONObject("position");
                double x = locatiObject.getDouble("x"); // Get X
                double y = locatiObject.getDouble("y"); // Get Y
                position.setLocation(x, y); // Set Location
            }
        } else {
            logger(entityLogger.setEntity,
                    "There was an error; The Entity Position was not given. The Entity will not be Spawned");
            logger(entityLogger.setEntity, entityUpdate);
            return false; // Entitiy Position not found cancel spawn
        }
        // Check if entityDirection is provided
        if (entityUpdate.has("direction")) {
            // Get the Direction
            myDirection = (characterDirection) entityUpdate.getEnum(enums.characterDirection.class, "direction");
        }
        // Check if entityType is provided
        if (entityUpdate.has("entityType")) {
            // Get the entity Type
            entityType = (entityTypeEnum) entityUpdate.getEnum(enums.entityTypeEnum.class, "entityType");
        }
        // Check if health is provided
        if (entityUpdate.has("health")) {
            // Get health Object
            JSONObject healthObj = entityUpdate.getJSONObject("health");
            health = healthObj.getDouble("current"); // Get Entities Current Health
            maxHealth = healthObj.getDouble("max"); // Get Entities Max Health
        }
        // Updating the new Hitbox to new Location
        hitbox = new Rectangle(position.x, position.y, gp.tileSize, gp.tileSize);
        // Everything Went Smooth Return true
        return true;
    }

    public synchronized JSONArray entityCastedAbilities() {
        JSONArray castedArrayTemp = new JSONArray(castedQueuedAbilities);
        this.castedQueuedAbilities.clear();
        return castedArrayTemp;
    }

    public void logger(entityLogger logType, Object log) {
        switch (logType) {
            case getEntity -> {
                System.out.println("[Entity Class][getEntity]: " + log);
            }
            case loadSprites -> {
                System.out.println("[Entity Class][loadSprites]: " + log);
            }
            case setEntity -> {
                System.out.println("[Entity Class][setEntity]: " + log);
            }
        }
    }
}

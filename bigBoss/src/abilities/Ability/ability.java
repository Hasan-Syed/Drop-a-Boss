package abilities.Ability;

// Other Libraries
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics2D;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.Point;
// Internal Classes
import entity.Entity;
import inputHandleing.KeyHandler;
import inputHandleing.mouseHandler;
import inputHandleing.mouseMotionHandler;
import main.gamePanel;
import main.enums.abilityCostType;
import main.enums.loadType;
import abilities.abilityTimer;

/**
 * The Ability Abstract class, is a super class that is used to make all
 * abilities in the game
 * 
 * @author Hasan Syed
 * @version 1.0
 */
public abstract class ability {
    // Non Ability Vatiables
    public Entity initiatorEntity;
    public gamePanel gp;
    public JSONObject abilityJsonObject;
    public mouseMotionHandler mouseMotionH;
    public mouseHandler mouseHandler;
    public KeyHandler keyboardHandler;
    // Ability Vatiables
    public String abilityName;
    public String abilityDescription;
    public BufferedImage abilityBorder;
    public BufferedImage abilityIcon;
    public JSONObject abilityCooldowns;
    public JSONObject abilityRanges;
    public JSONObject abilityCosts;
    public JSONObject abilitySprites;
    public JSONObject miscellaneous;
    public abilityTimer cooldownTimer;
    public JPanel abilityHUDIcon;
    public boolean abilityArmed = false;
    public boolean onCooldown = false;
    public int abilityCooldown = 0;
    public int cooldownLeft = 0;
    public abilityCostType abilityCostType;
    public int abilityCost;

    /**
     * Ability Super Class Constructer Construct
     * 
     * @param gp                Gamepanel
     * @param initiatorEntity   Initiator Entity
     * @param abilityJsonObject Ability JSONObject
     * @param mouseMotionH      Mouse Motion Handler
     */
    public ability(gamePanel gp, Entity initiatorEntity, JSONObject abilityJsonObject,
            mouseMotionHandler mouseMotionH, mouseHandler mouseHandler, KeyHandler keyboardHandler) {
        this.gp = gp;
        this.initiatorEntity = initiatorEntity;
        this.abilityJsonObject = abilityJsonObject;
        this.mouseMotionH = mouseMotionH;
        this.mouseHandler = mouseHandler;
        this.keyboardHandler = keyboardHandler;
    }

    /**
     * Initialize
     * <p>
     * Super Class
     * 
     * @param abilityInfo Ability JSON
     */
    protected void init() {
        setDefaultValues();
        cooldownTimer = new abilityTimer(this);
    }

    /**
     * This method is used to Load All Sprits needed for the Ability Item itself
     * 
     * @param loadType   sets if the loaded item is a Array of times or a single
     * @param spriteList The sprite address(s), to be loaded
     * @return Returns a List of Sprits loaded
     */
    public List<BufferedImage> loadSprites(loadType loadType, Object spriteList) {
        try {
            List<BufferedImage> loadedSprites = new ArrayList<BufferedImage>();

            switch (loadType) {
                case list -> {
                    JSONArray spriteListArray = (JSONArray) spriteList;

                    BufferedImage sprite;
                    for (Object address : spriteListArray) {
                        sprite = ImageIO.read(new File((String) address));
                        loadedSprites.add(sprite);
                    }
                }
                case single -> {
                    String spritetoBeLoaded = (String) spriteList;
                    BufferedImage sprite;
                    sprite = ImageIO.read(new File((String) spritetoBeLoaded));
                    loadedSprites.add(sprite);
                }
            }
            return loadedSprites;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Apply Cost applies the cost to the entity
     * 
     * @param abilityCostType Cost type to apply
     * @param amount          cost to apply
     */
    public void applyCost(abilityCostType abilityCostType, double amount) {
        switch (abilityCostType) {
            case health -> {
                initiatorEntity.health -= abilityCost;
            }
            case mana -> {
                initiatorEntity.mana -= abilityCost;
            }
            case power -> {
                initiatorEntity.power -= abilityCost;
            }

        }
    }

    // Abstract Method
    /**
     * This method is used to Initialize the Ability with JSON data
     * 
     * @param entity
     */
    public abstract void loadJSONValues(JSONObject values);

    /**
     * Load all ability Costs
     */
    public abstract void loadAbiliyCosts();

    /**
     * Load All Ability Cooldowns
     */
    public abstract void loadCooldowns();

    /**
     * Load All ability Ranges
     */
    public abstract void loadRange();

    /**
     * Load Sprite Assets
     * <p>
     * This Method is used to Organize and load all Sprite Assets, once you have a
     * JSONArray of sprite Assets ready, pass them through to
     * {@code loadSprites([spriteList])}, the return will be a
     * {@code List<BufferedImage>} object with all the sprites loaded
     */
    public abstract void loadSpriteAssets();

    /**
     * Load All Miscellaneous Stuff
     */
    public abstract void loadMiscellaneous();

    /**
     * Set Default Values
     * <p>
     * This method is used to set default values for all properties of the ability,
     * this is hard coded into each ability as a fallback incase the required JSON
     * file is missing or invalid.
     * 
     * Each ability can have extra properties declared in their classes, and
     * initailzed in here.
     */
    public abstract void setDefaultValues();

    /**
     * Ability Arm
     * <p>
     * This method is used to arm the ability.
     * <p>
     * This checks if the ability is ready to be used, while implementation this
     * method can chekck anthing about the ability itself, like it the player can
     * afford it, is it on cooldown or not and more.
     */
    public abstract void abilityArm();

    /**
     * Ability Use
     * <p>
     * This method uses the Ability.
     * <p>
     * This method piggy-backs of the {@code abilityArm()} method, if the ability is
     * armed the ability will get used
     */
    public abstract void abilityUse();

    /**
     * Ability Disarm
     * <p>
     * This method is used to Disarm the ability.
     * <p>
     * This checks if the ability is ready to be used, while implementation this
     * method can chekck anthing about the ability itself, like it the player can
     * afford it, is it on cooldown or not and more.
     */
    public abstract void abilityDisArm();

    public abstract void update();

    public abstract void draw(Graphics2D g2d);

    public abstract void HUD(Graphics2D g2d, Point position);
}

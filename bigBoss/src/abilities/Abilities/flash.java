package abilities.Abilities;

// Other Libraries
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import org.json.JSONObject;
import java.awt.AlphaComposite;
import java.awt.Point;
// Internal Classes
import inputHandleing.KeyHandler;
import inputHandleing.mouseHandler;
import inputHandleing.mouseMotionHandler;
import abilities.Ability.ability;
import entity.Entity;
import main.enums;
import main.gamePanel;
import main.enums.loadType;

/**
 * The Flash Ability
 * 
 * @author Hasan Syed
 * @version 1.0
 */
public class flash extends ability {
    // Ability Spacific Vatiables
    int flashDistance; // Flash Distance
    int flashCooldown; // Flash Cooldown

    /**
     * The Flash ability
     * 
     * @param gp                 Game Panel
     * @param initiatorEntity    Initiator/Target Entity
     * @param abilityIJsonObject The Ability Object
     * @param mouseMotionH       Mouse
     */
    public flash(gamePanel gp, Entity initiatorEntity, JSONObject abilityJsonObject,
            mouseMotionHandler mouseMotionH, mouseHandler mouseHandler, KeyHandler keyboardHandler) {
        super(gp, initiatorEntity, abilityJsonObject, mouseMotionH, mouseHandler, keyboardHandler);
        // Load Json Values
        init();
        loadJSONValues(abilityJsonObject);
    }

    @Override
    public void setDefaultValues() {
        // TODO Auto-generated method stub
    }

    @Override
    public void abilityArm() {
        // Check if the Ability can be armed
        if (!cooldownTimer.cooldownActive) {
            switch (abilityCostType) {
                case health -> {
                    if (initiatorEntity.health >= abilityCost) {
                        abilityArmed = true;
                    }
                }
                case mana -> {
                    if (initiatorEntity.mana >= abilityCost) {
                        abilityArmed = true;
                    }
                }
                case power -> {
                    if (initiatorEntity.power >= abilityCost) {
                        abilityArmed = true;
                    }
                }
            }
        }
    }

    @Override
    public void abilityDisArm() {
        abilityArmed = false;
    }

    void calculateNewPosition() {
        int playerPrespectiveMouseX = (mouseMotionH.mouseX - (int) gp.screenSize.getWidth()) + mouseMotionH.mouseX - 42;
        int playerPrespectiveMouseY = (mouseMotionH.mouseY - (int) gp.screenSize.getHeight()) + mouseMotionH.mouseY
                - 52;

        if (playerPrespectiveMouseY > -20) {
            initiatorEntity.position.y += flashDistance;
        }
        if (playerPrespectiveMouseY < 20) {
            initiatorEntity.position.y -= flashDistance;
        }
        if (playerPrespectiveMouseX < -10) {
            initiatorEntity.position.x -= flashDistance;
        }
        if (playerPrespectiveMouseX > 10) {
            initiatorEntity.position.x += flashDistance;
        }
    }

    @Override
    public void abilityUse() {
        if (abilityArmed) {
            applyCost(abilityCostType, flashCooldown);
            abilityDisArm();
            calculateNewPosition();
            cooldownTimer.startCooldown();
        }
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void draw(Graphics2D g2d) {
        // TODO Auto-generated method stub
    }

    public void HUD(Graphics2D graphics, Point position) {
        int iconSize = 48; // The Size of the Icon
        // Creates a Graphics Object for the Ability Icon
        Graphics2D abilityIconGraphics = (Graphics2D) graphics.create(position.x, position.y, iconSize, iconSize);
        Font abilityTextFont = new Font("Dialog", Font.BOLD, 12); // Text Font
        abilityIconGraphics.setColor(Color.WHITE);
        abilityIconGraphics.setFont(abilityTextFont);
        // Draw the Ability Icon, Text, Status
        if (!cooldownTimer.cooldownActive) {
            // Draw the ICON
            abilityIconGraphics.drawImage(abilityIcon, 0, 0, iconSize, iconSize, null);
            // draw Cooldown, and Ability Cost
            abilityIconGraphics.setColor(Color.WHITE);
            abilityIconGraphics.drawString(flashCooldown + " | " + abilityCost, 3, 10 + iconSize);
        } else {
            abilityIconGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
            // Draw the ICON
            abilityIconGraphics.drawImage(abilityIcon, 0, 0, iconSize, iconSize,
                    Color.black, null);

            abilityIconGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            abilityIconGraphics.drawString("" + cooldownLeft, (0 + 3), (0 - 24) +
                    iconSize);

            abilityIconGraphics.drawString(flashCooldown + " | " + abilityCost, (0 + 3),
                    (0 + 10) + iconSize);
        }
        abilityIconGraphics.drawImage(abilityBorder, 0, 0, iconSize, iconSize, null);
        abilityIconGraphics.dispose();
    }

    @Override
    public void loadJSONValues(JSONObject values) {
        this.abilityName = values.getString("Name");
        this.abilityDescription = values.getString("Description");
        this.abilityCooldowns = values.getJSONObject("cooldowns");
        this.abilityRanges = values.getJSONObject("abilityRanges");
        this.abilityCosts = values.getJSONObject("abilityCosts");
        this.abilitySprites = values.getJSONObject("abilitySprites");
        this.miscellaneous = values.getJSONObject("miscellaneous");
        // Continue Loading
        loadCooldowns(); // Load JSONs
        loadRange(); // Load JSONs
        loadAbiliyCosts(); // Load JSONs
        loadSpriteAssets(); // Load Sprites
    }

    @Override
    public void loadAbiliyCosts() {
        abilityCostType = abilityCosts.getEnum(enums.abilityCostType.class, "abilityCostType");
        abilityCost = abilityCosts.getInt("abilityCost");
    }

    @Override
    public void loadCooldowns() {
        flashCooldown = abilityCooldowns.getInt("cooldown");
        abilityCooldown = flashCooldown;
    }

    @Override
    public void loadRange() {
        flashDistance = abilityRanges.getInt("jumpRange");
    }

    @Override
    public void loadSpriteAssets() {
        // Load Icon
        String Icon = abilitySprites.getString("icon");
        abilityIcon = loadSprites(loadType.single, Icon).get(0);
        // Load Border
        String border = abilitySprites.getString("hudBorder");
        abilityBorder = loadSprites(loadType.single, border).get(0);
    }

    @Override
    public void loadMiscellaneous() {
        // TODO Auto-generated method stub

    }
}

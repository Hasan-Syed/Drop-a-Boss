package abilities.Abilities;

import java.awt.Graphics2D;
import java.awt.Point;

import org.json.JSONObject;
import java.awt.Font;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import abilities.Ability.ability;
import entity.Entity;
import entity.multiplayerBot;
import inputHandleing.KeyHandler;
import inputHandleing.mouseMotionHandler;
import main.enums;
import main.gamePanel;
import main.enums.entityTypeEnum;
import main.enums.loadType;
import main.enums.abilityAppliar;
import multiplayer.multiplayer;

/**
 * The Heal Ability
 * 
 * @author Hasan Syed
 * @version 1.2
 */
public class heal extends ability {
    // Game Variables
    multiplayer multiplayer;
    public List<multiplayerBot> healRadiousList;
    // Ability Spacific Vatiables
    int healRadius; // heal Radius
    int healCooldown; // Flash Cooldown
    // Heal Target Variables
    int instantHeal;
    int slowHeal;
    int healDuration;
    // Initiator Variables
    int initiatorHealHold;
    int initiatorHealDuration;
    boolean otherEntitiesHealed;
    // Ability Shapes
    Ellipse2D range;
    Ellipse2D rangeShape;

    public heal(gamePanel gp, Entity initiatorEntity, JSONObject abilityJsonObject, mouseMotionHandler mouseMotionH,
            inputHandleing.mouseHandler mouseHandler, KeyHandler keyboardHandler) {
        super(gp, initiatorEntity, abilityJsonObject, mouseMotionH, mouseHandler, keyboardHandler);
        multiplayer = gp.multiplayer;
        // Load Json Values
        init();
        loadJSONValues(abilityJsonObject);
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
        loadMiscellaneous(); // Load Miscellaneous
    }

    @Override
    public void loadAbiliyCosts() {
        abilityCostType = abilityCosts.getEnum(enums.abilityCostType.class, "abilityCostType");
        abilityCost = abilityCosts.getInt("abilityCost");
    }

    @Override
    public void loadCooldowns() {
        healCooldown = abilityCooldowns.getInt("cooldown");
        abilityCooldown = healCooldown;
    }

    @Override
    public void loadRange() {
        healRadius = abilityRanges.getInt("healRangeCircle");
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
        // Load heal Target Variables
        JSONObject healTarget = miscellaneous.getJSONObject("heal");
        instantHeal = healTarget.getInt("instant");
        slowHeal = healTarget.getInt("slowHeal");
        healDuration = healTarget.getInt("duration");
        // Initiator Variables
        JSONObject initiator = miscellaneous.getJSONObject("initiatorHealthTaxReturn");
        initiatorHealHold = initiator.getInt("durationHold");
        initiatorHealDuration = initiator.getInt("durationHeal");
    }

    @Override
    public void abilityArm() {
        // Check if the Ability can be armed
        if (!cooldownTimer.cooldownActive) {
            checkHealSite(); // Check Heal Site
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

    void initiatorSettings() {
        // Creating a Heal Object, This heal object will Last for a set amount of time
        // Holding the Health
        JSONObject initiatorHealthHold = new JSONObject();
        JSONObject initiatorHealthHoldInternal = new JSONObject();
        initiatorHealthHoldInternal.put("causedBy", multiplayer.onlineID);
        initiatorHealthHoldInternal.put("instantHeal", 0);
        initiatorHealthHoldInternal.put("maxHeal", 0);
        initiatorHealthHoldInternal.put("healDuration", initiatorHealHold);
        initiatorHealthHoldInternal.put("causedTo", multiplayer.onlineID);
        initiatorHealthHoldInternal.put("stackable", false);
        initiatorHealthHold.put("abilityType", abilityAppliar.healthObject);
        initiatorHealthHold.put("impact", initiatorHealthHoldInternal);
        // Start a health Object for Holding the Health
        initiatorEntity.healthManipulate.addToQueue(initiatorHealthHold);
        // Heal back the Cost
        if (otherEntitiesHealed) {
            // Only if Other entities were healed
            JSONObject initiatorHealthBack = new JSONObject();
            JSONObject initiatorHealthBackInternal = new JSONObject();
            initiatorHealthBackInternal.put("causedBy", multiplayer.onlineID);
            initiatorHealthBackInternal.put("instantHeal", 0);
            initiatorHealthBackInternal.put("maxHeal", abilityCost);
            initiatorHealthBackInternal.put("healDuration", initiatorHealDuration);
            initiatorHealthBackInternal.put("causedTo", multiplayer.onlineID);
            initiatorHealthBackInternal.put("stackable", false);
            initiatorHealthBack.put("abilityType", abilityAppliar.healthObject);
            initiatorHealthBack.put("impact", initiatorHealthBackInternal);
            // Start a health Object for Holding the Health
            initiatorEntity.healthManipulate.addToQueue(initiatorHealthBack);
        }
    }

    void checkHealSite() {
        healRadiousList = new ArrayList<>();
        for (multiplayerBot multiPlayerEntities : gp.multiplayerAIArray) {
            if (multiPlayerEntities.entityType.equals(entityTypeEnum.player)
                    && range.intersects(multiPlayerEntities.hitbox)) {
                healRadiousList.add(multiPlayerEntities);
                otherEntitiesHealed = true;
            }
        }
    }

    void makeNsendHealJson() {
        for (multiplayerBot multiPlayerEntities : healRadiousList) {
            JSONObject entityHealShell = new JSONObject();
            JSONObject entityHealInternal = new JSONObject();
            entityHealInternal.put("causedBy", gp.multiplayer.onlineID);
            entityHealInternal.put("instantHeal", instantHeal);
            entityHealInternal.put("maxHeal", slowHeal);
            entityHealInternal.put("healDuration", healDuration);
            entityHealInternal.put("causedTo", multiPlayerEntities.ID);
            entityHealShell.put("abilityType", abilityAppliar.healthObject);
            entityHealShell.put("impact", entityHealInternal);
            initiatorEntity.castedQueuedAbilities.put(entityHealShell);
        }
    }

    @Override
    public void abilityUse() {
        if (abilityArmed) {
            abilityDisArm();
            applyCost(abilityCostType, abilityCost);
            makeNsendHealJson(); // makes and send JSON
            initiatorSettings(); // Apply Initiator Settings
            cooldownTimer.startCooldown();
        }
    }

    @Override
    public void update() {
        rangeShape = new Ellipse2D.Double(initiatorEntity.screenX - ((healRadius - gp.tileSize) / 2),
                initiatorEntity.screenY - ((healRadius - gp.tileSize) / 2), healRadius, healRadius);
        range = new Ellipse2D.Double(initiatorEntity.position.x - ((healRadius - gp.tileSize) / 2),
                initiatorEntity.position.y - ((healRadius - gp.tileSize) / 2), healRadius, healRadius);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (abilityArmed) {
            g2d.setColor(Color.YELLOW);
            g2d.draw(rangeShape);
        }
    }

    @Override
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
            abilityIconGraphics.drawString(healCooldown + " | " + abilityCost, 3, 10 + iconSize);
        } else {
            abilityIconGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
            // Draw the ICON
            abilityIconGraphics.drawImage(abilityIcon, 0, 0, iconSize, iconSize,
                    Color.black, null);

            abilityIconGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            abilityIconGraphics.drawString("" + cooldownLeft, (0 + 3), (0 - 24) +
                    iconSize);

            abilityIconGraphics.drawString(healCooldown + " | " + abilityCost, (0 + 3),
                    (0 + 10) + iconSize);
        }
        abilityIconGraphics.drawImage(abilityBorder, 0, 0, iconSize, iconSize, null);
        abilityIconGraphics.dispose();
    }

    @Override
    public void setDefaultValues() {
        rangeShape = new Ellipse2D.Double();
        rangeShape.setFrameFromDiagonal(initiatorEntity.screenX - ((healRadius - gp.tileSize) / 2),
                initiatorEntity.screenY - ((healRadius - gp.tileSize) / 2), healRadius, healRadius);
    }
}

package abilities.Abilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import abilities.abilityBase;
import abilities.abilityCarcus;
import entity.Entity;
import entity.multiplayerBot;
import main.enums.entityTypeEnum;

public class abilityHeal extends abilityBase implements abilityCarcus {

    public int instantHeal;
    public double maxHeal;
    public double healDuration;
    public Ellipse2D rangeShape;
    public List<multiplayerBot> healRadiousList;

    public abilityHeal(Entity entity) {
        super(entity);
        init();
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        abilityName = "Heal";
        abilityArmed = false;
        instantHeal = 5;
        maxHeal = 25;
        healDuration = 20;
        abilityRange = 500;
        abilityCooldown = 10;
        abilityDescription = "This ability Heals all players within the " + abilityRange
                + " px range from your character.";
        rangeShape = new Ellipse2D.Double();
        rangeShape.setFrameFromDiagonal(entity.x - ((abilityRange - gp.tileSize) / 2),
                entity.y - ((abilityRange - gp.tileSize) / 2), abilityRange, abilityRange);
    }

    @Override
    public void loadSpriteAssets() {
        // TODO Auto-generated method stub

    }

    public void checkHealSite() {
        healRadiousList = new ArrayList<>();
        for (multiplayerBot multiPlayerEntities : gp.multiplayerAIArray) {
            if (multiPlayerEntities.entityType.equals(entityTypeEnum.player)
                    && rangeShape.intersects(multiPlayerEntities.hitbox)) {
                healRadiousList.add(multiPlayerEntities);
            }
        }
    }

    @Override
    public void abilityArm() {
        if (!abilityTimer.cooldownActive) {
            abilityArmed = true;
            checkHealSite();
        }
    }

    public void makeHealJson() {
        JSONObject healJson = new JSONObject();
        for (multiplayerBot multiPlayerEntities : healRadiousList) {
            JSONObject entityHeal = new JSONObject();
            entityHeal.put("causedBy", gp.multiplayer.onlineID);
            entityHeal.put("instantHeal", instantHeal);
            entityHeal.put("maxHeal", maxHeal);
            entityHeal.put("healDuration", healDuration);
            entityHeal.put("causedTo", multiPlayerEntities.ID);
            healJson.put("healObject", entityHeal);
        }
        // gp.multiplayer.toServer = healJson.toString();
        // gp.multiplayer.writeServer();
    }

    @Override
    public void abilityUse() {
        if (abilityArmed) {
            makeHealJson();
            abilityArmed = false;
            abilityTimer.startCooldown();
        }
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub
        rangeShape = new Ellipse2D.Double(entity.x - ((abilityRange - gp.tileSize) / 2),
                entity.y - ((abilityRange - gp.tileSize) / 2), abilityRange, abilityRange);
    }

    @Override
    public void draw(Graphics2D g2d) {
        // TODO Auto-generated method stub
        if (abilityArmed) {
            g2d.setColor(Color.YELLOW);
            g2d.draw(rangeShape);
        }
    }

}

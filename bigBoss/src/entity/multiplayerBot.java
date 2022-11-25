package entity;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.TexturePaint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONObject;

import main.enums;
import main.gamePanel;
import main.enums.characterDirection;
import main.enums.spriteAdrs;
import main.enums.entityTypeEnum;;

public class multiplayerBot extends Entity {

    BufferedImage test;
    public final int ID;

    public multiplayerBot(gamePanel gp, int ID) {
        super(gp);
        this.ID = ID;
        try {
            test = ImageIO.read(new File("resources\\hudResources\\playerHealth.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        init(); // Initialize Entity Back End
        setDefaultValues(); // Set Defaults
        loadSpriteAssets();
    }

    // Player Default Values
    public void setDefaultValues() {
        maxHealth = 10;
        health = 10;
        x = 100;
        y = 100;
        speed = 4;
        myDirection = characterDirection.facingUp;
    }

    // update JSON
    public void update(JSONObject botUpdate) {
        if (botUpdate.has("playerUpdate")) {
            botUpdate = (JSONObject) botUpdate.getJSONObject("playerUpdate");
        } else {
            botUpdate = (JSONObject) botUpdate.getJSONObject("Initial Entity");
        }

        name = (String) botUpdate.getString("name");
        x = (int) botUpdate.getInt("x");
        y = (int) botUpdate.getInt("y");
        if (botUpdate.has("direction")) {
            myDirection = (characterDirection) botUpdate.getEnum(enums.characterDirection.class, "direction");
        }
        if (botUpdate.has("entityType")) {
            entityType = (entityTypeEnum) botUpdate.getEnum(enums.entityTypeEnum.class, "entityType");
        }
        if (botUpdate.has("cH")) {
            health = (Double) botUpdate.getDouble("cH");
        }
        if (botUpdate.has("mH")) {
            maxHealth = (Double) botUpdate.getDouble("mH");
        }
        hitbox = new Rectangle(x, y, gp.tileSize, gp.tileSize);

        // Go through Sprites in a loop for loop animation
        {
            spriteSpeed++;
            if (spriteSpeed > 10) {
                if (currentSprite >= (upSprite.size() - 1)) {
                    currentSprite = 0;
                } else {
                    currentSprite++;
                }
                spriteSpeed = 0;
            }
        }
    }

    // Player Sprite Asses
    public void loadSpriteAssets() {
        // set up Left Sprites Addresses
        leftSpriteAdrs.add("resources\\playerSprites\\current\\lWalkRun_0.png");
        leftSpriteAdrs.add("resources\\playerSprites\\current\\lWalkRun_1.png");
        leftSpriteAdrs.add("resources\\playerSprites\\current\\lWalkRun_2.png");
        leftSpriteAdrs.add("resources\\playerSprites\\current\\lWalkRun_3.png");
        addSprite(spriteAdrs.leftSpriteAdrs, leftSpriteAdrs); // Load Left Sprites
        // set up Right Sprites Addesses
        rightSpriteAdrs.add("resources\\playerSprites\\current\\RWalkRun_0.png");
        rightSpriteAdrs.add("resources\\playerSprites\\current\\RWalkRun_1.png");
        rightSpriteAdrs.add("resources\\playerSprites\\current\\RWalkRun_2.png");
        rightSpriteAdrs.add("resources\\playerSprites\\current\\RWalkRun_3.png");
        addSprite(spriteAdrs.rightSpriteAdrs, rightSpriteAdrs); // Load Right Sprites
        // set up TOP Sprites Addresses
        upSpriteAdrs.add("resources\\playerSprites\\current\\lWalkRun_0.png");
        upSpriteAdrs.add("resources\\playerSprites\\current\\lWalkRun_1.png");
        upSpriteAdrs.add("resources\\playerSprites\\current\\lWalkRun_2.png");
        upSpriteAdrs.add("resources\\playerSprites\\current\\lWalkRun_3.png");
        addSprite(spriteAdrs.upSpriteAdrs, upSpriteAdrs); // Load Left Sprites
        // set up Right Sprites Addesses
        downSpriteAdrs.add("resources\\playerSprites\\current\\RWalkRun_0.png");
        downSpriteAdrs.add("resources\\playerSprites\\current\\RWalkRun_1.png");
        downSpriteAdrs.add("resources\\playerSprites\\current\\RWalkRun_2.png");
        downSpriteAdrs.add("resources\\playerSprites\\current\\RWalkRun_3.png");
        addSprite(spriteAdrs.downSpriteAdrs, downSpriteAdrs); // Load Right Sprites
    }

    public void draw(Graphics2D g2d) {
        BufferedImage currentSpriteImg = null;
        {
            switch (myDirection) {
                case facingUp -> {
                    currentSpriteImg = upSprite.get(currentSprite);
                }
                case facingDown -> {
                    currentSpriteImg = downSprite.get(currentSprite);
                }
                case facingLeft -> {
                    currentSpriteImg = leftSprite.get(currentSprite);
                }
                case facingRight -> {
                    currentSpriteImg = rightSprite.get(currentSprite);
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + myDirection);
            }
        }
        g2d.drawImage(currentSpriteImg, x, y, gp.tileSize, gp.tileSize, null);
        // g2d.drawString(name, x + 5, y);

        // Health
        {
            double scale = gp.tileSize / maxHealth;
            double healthVal = scale * health;
            // HEALTH
            {
                g2d.setColor(Color.black);
                g2d.fillRect(x, (y + gp.tileSize), gp.tileSize, 7);
                // g2d.setColor(new TexturePaint());
                // g2d.fillRect(x, (y + gp.tileSize), (int) healthVal, 5);
                Rectangle health = new Rectangle(x, (y + gp.tileSize), (int) healthVal, 5);
                TexturePaint spe = new TexturePaint(test, health);
                g2d.setPaint(spe);
                // g2d.draw(health);
                g2d.fill(health);
            }
            g2d.setColor(Color.white);
            g2d.drawString("health: " + health, x + 90, y);
        }
    }
}

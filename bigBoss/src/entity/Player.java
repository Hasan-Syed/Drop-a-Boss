package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import abilities.Abilities.abilityHeal;
import inputHandleing.KeyHandler;
import inputHandleing.mouseHandler;
import inputHandleing.mouseMotionHandler;
import main.gamePanel;
import main.enums.characterDirection;
import main.enums.entityTypeEnum;
import main.enums.spriteAdrs;

public class Player extends Entity {
    KeyHandler keyH; // Key Settings
    mouseMotionHandler mouseMotionH;
    mouseHandler mouseH;
    abilityHeal ah = new abilityHeal(this);

    public Player(gamePanel gp, String name) {
        super(gp);
        this.keyH = gp.keyH;
        this.mouseMotionH = gp.mouseMotionH;
        this.mouseH = gp.mouseH;
        this.name = name;

        init(); // Initialize Entity Back End
        setDefaultValues(); // Set Defaults
        loadSpriteAssets();
    }

    // Player Default Values
    public void setDefaultValues() {
        entityType = entityTypeEnum.player;
        maxHealth = 100;
        health = 5;
        x = 100;
        y = 100;
        speed = 4;
        myDirection = characterDirection.facingUp;
        hitbox = new Rectangle(20, 10, 24, 32);
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

    // Graphics Updates and Draw Method
    public void update() {
        // Player Direction
        if (keyH.upKey) {
            myDirection = characterDirection.facingUp; // Change Player Direction UP
        } else if (keyH.downKey) {
            myDirection = characterDirection.facingDown; // Change Player Direction DOWN
        } else if (keyH.leftKey) {
            myDirection = characterDirection.facingLeft; // Change Player Direction LEFT
        } else if (keyH.rightKey) {
            myDirection = characterDirection.facingRight; // Change Player Direction RIGHT
        }

        ah.update(); // Update Ability Heal <test>

        if (keyH.ability[0]) {
            ah.abilityArm();
        }

        if (mouseH.leftClick) {
            ah.abilityUse();
        }

        if (!keyH.ability[0]) {
            ah.abilityArmed = false;
        }

        // Collision Detection
        collision = false; // Current Collision is false
        gp.cChecker.checkTile(this); // Check for Collision
        // Update Player's Position
        if (!collision) { // If the player isn't Collided
            if (keyH.upKey) {
                y -= speed; // Move UP
            } else if (keyH.downKey) {
                y += speed; // Move DOWN
            } else if (keyH.leftKey) {
                x -= speed; // Move LEFT
            } else if (keyH.rightKey) {
                x += speed; // Move RIGHT
            }
        }

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

    // Player Draw
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
        ah.draw(g2d); // Draw abilityHeal
        g2d.drawImage(currentSpriteImg, x, y, gp.tileSize, gp.tileSize, null); // Draw Players
        g2d.drawString("myHealth: " + String.format("%,.2f", health), x, y - 30);
    }
}

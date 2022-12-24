package entity;

// External Libraries
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

// Internal Libraries
import abilities.Ability.ability;
import inputHandleing.KeyHandler;
import inputHandleing.mouseHandler;
import inputHandleing.mouseMotionHandler;
import main.gamePanel;
import main.enums.characterDirection;
import main.enums.entityTypeEnum;
import main.enums.spriteAdrs;

/**
 * Player Class
 * 
 * @author Hasan Syed
 * @version 1.2
 * @see Entity
 */
public class Player extends Entity {
    KeyHandler keyH; // Key Settings
    mouseMotionHandler mouseMotionH;
    mouseHandler mouseH;

    String file = "resources\\abilityResources\\healAbility.json";

    public void loadAbility(ability ability) {
        abilities.add(ability);
    }

    public static String readFileAsString(String file) throws Exception {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public Player(gamePanel gp, String name) {
        super(gp);
        this.keyH = gp.keyH;
        this.mouseMotionH = gp.mouseMotionH;
        this.mouseH = gp.mouseH;
        this.name = name;
        this.abilities = new ArrayList<ability>();

        init(); // Initialize Entity Back End
        setDefaultValues(); // Set Defaults
        loadSpriteAssets();
    }

    // Player Default Values
    public void setDefaultValues() {
        entityType = entityTypeEnum.player;
        maxMana = 1000;
        mana = 50;
        maxHealth = 1000;
        health = 500;
        position.x = 100;
        position.y = 100;
        speed = 5;
        myDirection = characterDirection.facingUp;
        hitbox = new Rectangle(18, 20, 22, 16);
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
        healthManipulate.update();
        // Update ScreenSize
        screenX = gp.screenSize.width / 2 - (gp.tileSize / 2);
        screenY = gp.screenSize.height / 2 - (gp.tileSize / 2);
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

        int abilityIndex = 0;
        for (ability abilityArm : abilities) {
            if (keyH.ability.get(abilityIndex)) {
                abilityArm.abilityArm();
            }
            if (!keyH.ability.get(abilityIndex)) {
                abilityArm.abilityDisArm();
            }
            if (mouseH.leftClick) {
                abilities.get(abilityIndex).abilityUse();
            }
            abilityIndex++;
        }

        // Collision Detection
        collision = false; // Current Collision is false
        gp.cChecker.checkTile(this); // Check for Collision
        // Update Player's Position
        if (!collision) { // If the player isn't Collided
            if (keyH.upKey) {
                position.y -= speed; // Move UP
            } else if (keyH.downKey) {
                position.y += speed; // Move DOWN
            } else if (keyH.leftKey) {
                position.x -= speed; // Move LEFT
            } else if (keyH.rightKey) {
                position.x += speed; // Move RIGHT
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
        // update Abilities
        for (ability ability : abilities) {
            ability.update();
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
        g2d.drawImage(currentSpriteImg, screenX, screenY, gp.tileSize, gp.tileSize, null); // Draw Players
        g2d.draw(hitbox);
        // Draw Abilities
        for (ability ability : abilities) {
            ability.draw(g2d);
        }
    }
}

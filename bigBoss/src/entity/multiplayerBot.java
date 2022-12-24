package entity;

// External Libraries
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
// Internal Libraries
import main.gamePanel;
import main.enums.characterDirection;
import main.enums.spriteAdrs;

/**
 * multiplayerBot, is a entity that is controlled by remote player inputs
 * 
 * @author Hasan Syed
 * @version 1.2
 * @see Entity
 */
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
        position.x = 100;
        position.y = 100;
        speed = 4;
        myDirection = characterDirection.facingUp;
    }

    public synchronized void update() {
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

    // Player Sprite Assets
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

    public synchronized void draw(Graphics2D g2d) {
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
            }
        }

        int screenX = position.x - gp.player.position.x + gp.player.screenX;
        int screenY = position.y - gp.player.position.y + gp.player.screenY;

        g2d.drawString("Health: " + health, screenX, screenY - 10);

        g2d.drawImage(currentSpriteImg, screenX, screenY, gp.tileSize, gp.tileSize, gp);
    }
}

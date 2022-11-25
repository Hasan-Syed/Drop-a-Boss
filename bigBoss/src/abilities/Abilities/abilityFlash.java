package abilities.Abilities;

import java.awt.Graphics2D;

import abilities.abilityBase;
import abilities.abilityCarcus;
import entity.Entity;

public class abilityFlash extends abilityBase implements abilityCarcus {
    public abilityFlash(Entity entity) {
        super(entity);
        init();
        setDefaultValues();
    }

    @Override
    public void setDefaultValues() {
        abilityName = "Flash";
        abilityArmed = false;
        abilityRange = 80;
        abilityCooldown = 50;
        abilityDescription = "This ability allows you to flash, " + abilityRange
                + " px from your character. The calculated from your mouse X, Y.";
    }

    @Override
    public void loadSpriteAssets() {
        // TODO Auto-generated method stub

    }

    @Override
    public void abilityArm() {
        if (!abilityTimer.cooldownActive) {
            abilityArmed = true;
        }
    }

    void calculateNewPosition() {
        int playerPrespectiveMouseX = (mouseMotionH.mouseX - gp.screenWidth) + mouseMotionH.mouseX - 42;
        int playerPrespectiveMouseY = (mouseMotionH.mouseY - gp.screenHeight) + mouseMotionH.mouseY - 52;

        if (playerPrespectiveMouseY > -20) {
            entity.y += abilityRange;
        }
        if (playerPrespectiveMouseY < 20) {
            entity.y -= abilityRange;
        }
        if (playerPrespectiveMouseX < -10) {
            entity.x -= abilityRange;
        }
        if (playerPrespectiveMouseX > 10) {
            entity.x += abilityRange;
        }
    }

    @Override
    public void abilityUse() {
        if (abilityArmed) {
            calculateNewPosition();
            abilityArmed = false;
            abilityTimer.startCooldown();
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

}

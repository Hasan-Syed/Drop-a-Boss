package abilities;

import java.awt.Graphics2D;

public interface abilityCarcus {
    public void setDefaultValues();

    public void loadSpriteAssets();

    public void abilityArm();

    public void abilityUse();

    public void update();

    public void draw(Graphics2D g2d);
}

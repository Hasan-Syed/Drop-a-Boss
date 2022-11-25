package abilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import entity.Entity;
import main.gamePanel;
import main.enums.abilitySpriteAddress;
import inputHandleing.KeyHandler;
import inputHandleing.mouseMotionHandler;

public class abilityBase {
    public Entity entity;
    public gamePanel gp;
    public mouseMotionHandler mouseMotionH;
    public KeyHandler keyH;
    public abilityTimer abilityTimer;

    public boolean abilityArmed;
    public String abilityName;
    public String abilityDescription;
    public int abilityCooldown = 0;
    public int abilityRange;

    public BufferedImage abilityIcon;
    public List<String> abilitySpriteAdrs;
    public List<BufferedImage> abilitySprites;

    protected abilityBase(Entity entity) {
        this.entity = entity;
        this.gp = entity.gp;
        this.mouseMotionH = gp.mouseMotionH;
        this.keyH = gp.keyH;
    }

    public void init() {
        abilitySpriteAdrs = new ArrayList<>();
        abilitySprites = new ArrayList<>();
        abilityTimer = new abilityTimer(this);
    }

    // Load Ability Sprites
    public void addSprite(abilitySpriteAddress spriteType, String abilityIconArds, List<String> spriteArds) {
        try {
            switch (spriteType) {
                case abilityIcon -> {
                    abilityIcon = ImageIO.read(new File(abilityIconArds));
                }
                case abilitySprite -> {
                    for (String address : spriteArds) {
                        abilitySprites.add(ImageIO.read(new File(address)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

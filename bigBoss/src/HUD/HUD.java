package HUD;

// External Libraries
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.TexturePaint;
import javax.imageio.ImageIO;
import javax.swing.plaf.ColorUIResource;
import java.awt.Point;
// Internal Libraries
import abilities.Ability.ability;
import main.gamePanel;
import entity.Entity;

/**
 * The HUD, heads up Display, displays all critical information of the player on
 * the screen
 * 
 * @author Hasan Syed
 * @version 1.0
 */
public class HUD {
    public gamePanel gp;
    public Entity entity;
    List<ability> abilityList;
    public int hudX, hudY;
    public int height = 125, width = 450;
    RoundRectangle2D hudRect;

    List<BufferedImage> textures;

    public HUD(gamePanel gp, Entity entity) {
        this.gp = gp;
        this.entity = entity;
        this.abilityList = entity.abilities;
        this.textures = new ArrayList<BufferedImage>();
        hudRect = new RoundRectangle2D.Double();
        init(); // Initialize Class
    }

    void init() {
        textures.add(textureLoader("resources\\hudResources\\playerHealth.png")); // Load health texture
        textures.add(textureLoader("resources\\hudResources\\playerMana.png")); // Load mana texture
        logger("HUD Initialized!");
    }

    /**
     * This method is called to load Textures
     * 
     * @param address The Texture Address
     * @return the Texture itself
     */
    BufferedImage textureLoader(String address) {
        try {
            return ImageIO.read(new File(address)); // Load the File
        } catch (Exception e) {
            logger(e.getMessage() + ", " + address + " not found"); // File not Found most likely
            return null; // return null
        }
    }

    public void update() {
        // Position
        {
            int screenX = hudX = (gp.screenSize.width / 2) - (width / 2);
            int screenY = hudY = gp.screenSize.height - height;
            hudRect.setRoundRect(screenX, screenY, width, height, 20, 25);
        }
    }

    /**
     * Pretty Self Explanatory
     * 
     * @param log what its printing
     */
    void logger(String log) {
        System.out.println("[HUD Logger]: " + log);
    }

    /**
     * draw Mathod is Used to draw the HUD, and HUD Items on the screen.
     * 
     * @param graphics pass the Graphics object to draw on the screen
     */
    public void draw(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics.create(); // Parse the Graphics object to Graphics2D
        FontMetrics fontMaMetrics = g2d.getFontMetrics();
        // Draw the HUD Rectangle
        {
            g2d.setColor(new ColorUIResource(22, 41, 50)); // Set the HUD rect Color
            g2d.fill(hudRect); // Fill/Draw the HUD Rect
        }
        // Recreate the Graphics Object, for inside the HUD Rect
        g2d = (Graphics2D) graphics.create(hudX, hudY, width, height);
        int barY = 0; // Y/Height Positioning of the *Bars*
        // Draw Health and Mana Bar
        {
            barY = 20; // Y/Height Positioning of the *Bars*

            int barXStrt = 20; // The Start/finish positioning of the *Bars* (outside bounds)
            int barXEnd = width - 40; // The Start/finish positioning of the *Bars* (outside bounds)

            double barXStrtInternal = 22.5; // The start/finish *value* positioning of the *Bars* (internal bounds)
            double barXEndInternal = width - 45; // The start/finish *value* positioning of the *Bars* (internal bounds)

            {
                // Draw the Health Bar Background
                RoundRectangle2D healthBase = new RoundRectangle2D.Double(barXStrt, barY, barXEnd, 15, 12.5, 12.5);
                g2d.setColor(Color.black); // Color the bar Background
                g2d.fill(healthBase); // Draw/Fill the Health bar Background
                // Health Calc
                double scaleHealth = barXEndInternal / entity.maxHealth; // Create a Scale for the Health calculation
                double healthVal = scaleHealth * entity.health; // Calculate the Health
                // Health Value Bar
                Rectangle2D healthLinetct = new Rectangle2D.Double(barXStrtInternal, barY + 2.5, healthVal, 10);
                RoundRectangle2D healthLine = new RoundRectangle2D.Double(barXStrtInternal, barY + 2.5, healthVal,
                        10, 12, 12);
                g2d.setPaint(new TexturePaint(textures.get(0), healthLinetct));
                g2d.fill(healthLine);
                // Draw Health Text
                g2d.setColor(Color.WHITE);
                Rectangle2D healthInfoBounds = fontMaMetrics.getStringBounds(
                        Math.round(entity.health) + "/" + Math.round(entity.maxHealth),
                        g2d);
                double textStartX = barXStrtInternal
                        + ((healthBase.getWidth() / 2) - (healthInfoBounds.getWidth() / 2));
                g2d.drawString(Math.round(entity.health) + "/" + Math.round(entity.maxHealth), (int) textStartX,
                        (int) (barY + 12));
            }
            barY = barY + 20; // RePosition the Bar Y/Height
            {
                // Draw the Mana Bar Background
                RoundRectangle2D manaBase = new RoundRectangle2D.Double(barXStrt, barY, barXEnd, 15, 12.5, 12.5);
                g2d.setColor(Color.black); // Color the bar Background
                g2d.fill(manaBase); // Draw/Fill the Health bar Background
                // Health Calc
                double scaleMana = barXEndInternal / entity.maxMana; // Create a Scale for the Mana calculation
                double manaVal = scaleMana * entity.mana; // Calculate the Mana
                // Health Value Bar
                Rectangle2D manaLinetct = new Rectangle2D.Double(barXStrtInternal, barY + 2.5, manaVal, 10);
                RoundRectangle2D manaLine = new RoundRectangle2D.Double(barXStrtInternal, barY + 2.5, manaVal,
                        10, 12, 12);
                g2d.setPaint(new TexturePaint(textures.get(1), manaLinetct));
                g2d.fill(manaLine);
                // Draw Health Text
                g2d.setColor(Color.WHITE);
                Rectangle2D manaInfoBounds = fontMaMetrics.getStringBounds(
                        Math.round(entity.health) + "/" + Math.round(entity.maxHealth),
                        g2d);
                double textStartX = barXStrtInternal
                        + ((manaBase.getWidth() / 2) - (manaInfoBounds.getWidth() / 2));
                g2d.drawString(Math.round(entity.mana) + "/" + Math.round(entity.maxMana), (int) textStartX,
                        (int) (barY + 12));
            }
        }
        // Create Ability Boxes
        {
            int abilityY = barY + (20); // RePosition the Bar Y/Height
            int newWidth = width - 50;
            int abilityX = (width / 2) - (newWidth / 2);
            Graphics2D abilitiesSection = (Graphics2D) g2d.create(abilityX, abilityY, newWidth, height);
            {
                int abilitiesEquipped = abilityList.size();
                int abilitiesLength = 0;

                abilitiesLength = (newWidth / 2) - ((50 * abilitiesEquipped) / 2);

                for (ability ability : entity.abilities) {
                    ability.HUD(abilitiesSection, new Point(abilitiesLength, 0));
                    abilitiesLength += 50;
                }

            }
        }

        g2d.dispose();
    }
}

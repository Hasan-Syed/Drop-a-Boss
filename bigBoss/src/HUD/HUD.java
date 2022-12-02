package HUD;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import main.gamePanel;
import entity.Entity;

public class HUD extends JPanel {
    public gamePanel gp;
    public Entity entity;

    double healthVal;

    public HUD(gamePanel gp, Entity entity) {
        this.gp = gp;
        this.entity = entity;
        this.setPreferredSize(new Dimension(250, 150));
        this.setLocation(0, 0);
        this.setBackground(Color.gray);
    }

    public void update() {
        setLocation(0, 0);

        // Health
        {
            double scale = 145 / entity.maxHealth;
            healthVal = scale * entity.health;
        }
    }

    public void draw(Graphics2D g2d) {
        g2d.create();
        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, 0, 250, 125);
        // HEALTH
        {
            g2d.setColor(Color.black);
            g2d.fillRect(90, 11, 150, 11);
            g2d.setColor(Color.green);
            g2d.fillRect(95, 14, (int) healthVal, 5);
        }
    }
}

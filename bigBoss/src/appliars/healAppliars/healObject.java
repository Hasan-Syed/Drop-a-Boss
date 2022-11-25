package appliars.healAppliars;

import java.util.List;
import java.util.Timer;

import org.json.JSONObject;

import entity.Entity;
import entity.multiplayerBot;
import main.gamePanel;

public class healObject {
    gamePanel gp;
    Entity player;
    List<multiplayerBot> multiplayerAIArray;
    JSONObject healObject;
    int instantHeal;
    double maxHeal;
    double healDuration;

    public healObject(gamePanel gp, Entity player, List<multiplayerBot> multiplayerAIArray, JSONObject healObject) {
        this.gp = gp;
        this.player = player;
        this.multiplayerAIArray = multiplayerAIArray;
        this.healObject = healObject.getJSONObject("healObject");
        System.out.println("Healer Initialized");
        getHealParams();
    }

    public void getHealParams() {
        int causedBy = (int) healObject.getInt("causedBy");
        instantHeal = (int) healObject.getInt("instantHeal");
        maxHeal = (double) healObject.getDouble("maxHeal");
        healDuration = (double) healObject.getDouble("healDuration");
        int causedTo = (int) healObject.getInt("causedTo");
        boolean entityFound = false;
        {
            if (player.ID == causedTo) {
                applyHeal(player);
                entityFound = true;
            }
        }
        {
            if (!entityFound) {
                for (multiplayerBot remotePlayer : multiplayerAIArray) {
                    System.out.println(remotePlayer.name + ", " + remotePlayer.ID);
                    if (remotePlayer.ID == causedTo) {
                        applyHeal(remotePlayer);
                    }
                }
            }
        }
        if (!entityFound) {
            System.out.println("Heal Failed");
        }
    }

    public void applyHeal(Entity target) {
        if (!((target.health + instantHeal) > target.maxHealth)) {
            target.health += instantHeal;
        }
        healThread ht = new healThread(target, maxHeal, healDuration);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(ht, 1000, 1000);
    }
}

package appliars.healAppliars;

import java.util.TimerTask;

import entity.Entity;

public class healThread extends TimerTask {

    public Entity target;
    public double timeLeft = 0;
    final double maxheal;
    final double healRate;

    healThread(Entity target, double maxHeal, double healDuration) {
        System.out.println("[Healing Thead]: Healing Thead is now Active healing: " + target.name);
        this.target = target;
        this.maxheal = maxHeal;
        this.healRate = maxHeal / healDuration;
        this.timeLeft = healDuration;
    }

    @Override
    public void run() {
        if (timeLeft < 1) {
            this.cancel();
        }
        if (!((target.health + healRate) > target.maxHealth)) {
            target.health += healRate;
        }
        timeLeft--;
    }

}

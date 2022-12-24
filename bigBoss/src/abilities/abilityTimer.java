package abilities;

import abilities.Ability.ability;

/**
 * The Ability Timer is used for Ability CoolDowns
 * 
 * @author Hasan Syed
 * @version 1.2
 */
public class abilityTimer extends Thread {

    public long startTime;
    public boolean cooldownActive;
    public int timeLeft = 0;

    public ability ability;

    public abilityTimer(ability ability) {
        super();
        this.ability = ability;
    }

    public synchronized void startCooldown() {
        this.startTime = System.currentTimeMillis();
        this.cooldownActive = true;
        try {
            this.start();
        } catch (Exception e) {
            System.err.println("[Client] [Ability] [abilityTimer]: " + e.getCause());
            e.printStackTrace();
        }
    }

    public synchronized void run() {
        while (cooldownActive) {
            long timeDifference = System.currentTimeMillis() - this.startTime;
            ability.cooldownLeft = ability.abilityCooldown - (((int) (timeDifference) / 1000) % 60);
            this.cooldownActive = true;
            boolean timerFinished = (((int) (timeDifference) / 1000) % 60) == ability.abilityCooldown;
            if (timerFinished) {
                this.cooldownActive = false;
                ability.cooldownTimer = new abilityTimer(ability);
            }
        }
    }
}

package abilities;

public class abilityTimer extends Thread {

    public long startTime;
    public boolean cooldownActive;
    public int timeLeft = 0;

    public abilityBase ability;

    public abilityTimer(abilityBase ability) {
        this.ability = ability;
    }

    public void startCooldown() {
        this.startTime = System.currentTimeMillis();
        this.cooldownActive = true;
        try {
            this.start();
        } catch (Exception e) {
            System.err.println("hola" + e.getMessage());
        }
    }

    public void run() {
        while (cooldownActive) {
            long timeDifference = System.currentTimeMillis() - this.startTime;
            timeLeft = ability.abilityCooldown - (((int) (timeDifference) / 1000) % 60);
            this.cooldownActive = true;
            boolean timerFinished = (((int) (timeDifference) / 1000) % 60) == ability.abilityCooldown;
            if (timerFinished) {
                this.cooldownActive = false;
                ability.abilityTimer = new abilityTimer(ability);
            }
        }
    }
}

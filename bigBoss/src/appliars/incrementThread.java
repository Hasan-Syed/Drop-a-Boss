package appliars;

import java.util.TimerTask;

import entity.Entity;

/**
 * The Increment Thread, is used to increment values in a timely manner on
 * entities
 * 
 * @author Hasan Syed
 * @version 1.1
 */
public class incrementThread extends TimerTask {
    final Entity target;
    final double maxValue;
    final double incrementRate;
    final appliarsObject appliar;
    public double timeLeft;
    public boolean running = false;

    /**
     * Increment Thread is used to slowly increase values on an entity, example
     * Health, Mana, Power, etc.
     * 
     * First the {@code incrementRate} is calculated by {@code maxValue / duration}.
     * 
     * The TimerTask is then Started and on every tick
     * 
     * @param target
     * @param maxValue
     * @param duration
     */
    public incrementThread(Entity target, double maxValue, double duration, appliarsObject appliar) {
        this.target = target; // Set default Parameters
        this.maxValue = maxValue; // Set default Parameters
        this.timeLeft = duration; // Set default Parameters
        this.incrementRate = maxValue / timeLeft; // Set default Parameters
        this.appliar = appliar; // Set default Parameters
    }

    @Override
    public synchronized void run() {
        appliar.apply(incrementRate); // Apply Increment
        timeLeft--; // Subract Time
        running = true; // Set Running TRUE
        // IF the Time is below 0
        if (timeLeft <= 0) {
            running = false; // Set Running false
            this.cancel(); // End the Timer
        }
    }
}

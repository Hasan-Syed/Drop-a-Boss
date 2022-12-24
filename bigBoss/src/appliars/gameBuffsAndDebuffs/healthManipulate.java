package appliars.gameBuffsAndDebuffs;

// Imported Classes
import org.json.JSONObject;
// My Classes
import appliars.appliarsObject;
import appliars.incrementThread;
import entity.Entity;
import main.gamePanel;

/**
 * The health Object is used to manipulate the player's health bar, either
 * healing or damaging the player. the damage can be applied slowly or instant.
 * 
 * @author Hasan Syed
 * @version 1.2
 */
public class healthManipulate extends appliarsObject {
    boolean stackable = false;
    double instantHeal;
    double maxHeal;
    int causedBy;
    double healDuration;
    int causedTo;

    /**
     * The Health Manipulation Class is used to manipulate the {@code target} health
     * value
     * 
     * @param gp
     * @param target
     */
    public healthManipulate(gamePanel gp, Entity target) {
        super(gp, target);
        this.appliarType = main.enums.abilityAppliar.healthObject;
    }

    @Override
    public void getParams() {
        // Get Impact Values
        JSONObject impact = this.currentApplyObject.getJSONObject("impact");
        // Get Impact Values
        {
            instantHeal = impact.getDouble("instantHeal");
            maxHeal = impact.getDouble("maxHeal");
            causedBy = impact.getInt("causedBy");
            healDuration = impact.getDouble("healDuration");
            causedTo = impact.getInt("causedTo");
            if (impact.has("stackable")) {
                stackable = impact.getBoolean("stackable");
            }
        }
    }

    @Override
    public boolean apply(double incrementRate) {
        // if the *Increased* health is below maxHealth
        if (!((target.health + incrementRate) > target.maxHealth)) {
            // Increment the Health
            target.health += incrementRate;
            this.incrementThread.running = true;
            return true; // Return true if any changes where applied
        }
        this.incrementThread.running = false;
        return false; // Return False if no changes where applied
    }

    @Override
    public void apply(Entity entity) {
        // Instant Heal Section
        // Calculation Variables
        double currentEntityHealth = entity.health;
        double maxEntityHealth = entity.maxHealth;
        double trueHeal = 0;
        double overHeal = 0; // entity overHeal (currently not Active, no mechanics are implemented)
        {
            // Instant Heal the Player
            // Calculate Heal and overHeal
            currentEntityHealth = +instantHeal; //
            overHeal = maxEntityHealth - currentEntityHealth; // Calculate the Over Heal Amount
            trueHeal = instantHeal - trueHeal; // Calculate the Real Heal Amount
            currentEntityHealth = entity.health + trueHeal; // New Current Heal
        }
        // Calculations done apply new values (instant Heal)
        entity.overHeal = overHeal; // Assign overHeal (currently not Active, no mechanics are implemented)
        entity.health = currentEntityHealth; // Assign the new Current health Value
        // Move on to Slow HEAL
        incrementThread = new incrementThread(target, maxHeal, healDuration, this);
        initializeTimer("heal Thread Timer");
        this.incrementThread.running = true;
        timer.scheduleAtFixedRate(incrementThread, 1000, 1000);
    }

    @Override
    public void update() {
        // Check if a heal is currently Active
        if (incrementThread != null) {
            active = this.incrementThread.running;
        }
        // Check if the Queue is not Empty
        if (!this.applyObjectQueue.isEmpty()) {
            // Check if the (active) healing object is unstackable
            if (stackable || !active) {
                this.currentApplyObject = this.applyObjectQueue.getJSONObject(0);
                this.applyObjectQueue.remove(0);
                getParams(); // Load the Parameters
                apply(this.target); // Apply to the target
            }
        }
    }

    @Override
    public void draw() {
        // TODO Auto-generated method stub

    }
}

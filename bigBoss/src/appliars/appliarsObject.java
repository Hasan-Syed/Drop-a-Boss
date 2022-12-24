package appliars;

// External Libraries
import java.util.Timer;
import org.json.JSONArray;
import org.json.JSONObject;

// My Libraries
import entity.Entity;
import main.enums;
import main.gamePanel;
import main.enums.abilityAppliar;
import main.enums.appliarsObjectLogger;

/**
 * The appliarsObject is a abstract class, and is required to create any buffs
 * and debuff effects in the game
 * 
 * @author Hasan Syed
 * @version 1.2
 */
public abstract class appliarsObject {
    public gamePanel gp; // GamePanel
    public Entity target; // Target
    public abilityAppliar appliarType; // What kind of Appliar is it?
    public JSONArray applyObjectQueue; // The apply Queue
    public JSONObject currentApplyObject; // current Apply Object
    public Timer timer; // Timer
    public incrementThread incrementThread; // Increment Thread
    public boolean active; // if the appliar is Active

    /**
     * appliarsObject Constructor; used to initialize the super class for use of the
     * clild class
     * 
     * @param gp     GamePanel
     * @param target Target Entity
     */
    public appliarsObject(gamePanel gp, Entity target) {
        // Initialize the appliars object
        this.gp = gp;
        this.target = target;
        this.applyObjectQueue = new JSONArray();
    }

    public void initializeTimer(String timerName) {
        this.timer = new Timer(timerName);
    }

    /**
     * addToQueue; Is used to add a new Apply object to the list of the appliar.
     * 
     * @param newApply new Apply Object (JSON)
     * @return true = successfully added.
     *         <p>
     *         false = sent to the wrong applier.
     */
    public boolean addToQueue(JSONObject newApply) {
        abilityAppliar checkAppliarType = newApply.getEnum(enums.abilityAppliar.class, "abilityType");
        if (this.appliarType == checkAppliarType) {
            this.applyObjectQueue.put(newApply);
            // Log
            logger(appliarsObjectLogger.addToQueue,
                    "Successfully added: " + newApply + " to queue for " + appliarType + " object");
            return true;
        } else {
            // Log
            logger(appliarsObjectLogger.addToQueue,
                    "Incorrect Object sent to " + appliarType + " the sent object is for: " + checkAppliarType);
            return false;
        }
    }

    /**
     * Apply, applies the health Increment passed through thr args
     * 
     * @param incrementRate The incrementRate is Applied to {@code target}
     *                      health
     * @return {@code true} if the incrementRate was appliead to the {@code target}
     *         Health; and {@code false} if the incrementRate was not appliead to
     *         the {@code target}
     */
    public abstract boolean apply(double incrementRate);

    /**
     * getParams; reads and assigns/reassigns the appliar parameters sent through
     * the JSON
     */
    public abstract void getParams();

    /**
     * apply is called once all Parameters are ready for the {@code target} value
     * incrementation.
     * <p>
     * apply is Starts initializes the timer, and starts the incrementation
     */
    public abstract void apply(Entity entity);

    public abstract void update();

    public abstract void draw();

    /**
     * Logger... pretty self explanatory
     * 
     * @param logType
     * @param log
     */
    public void logger(appliarsObjectLogger logType, Object log) {
        switch (logType) {
            case addToQueue -> {
                System.out.println("[appliarsObject] [addToQueue]: " + log);
            }
            case initializeTimer -> {
                System.out.println("[appliarsObject] [(re)initialize Timer]: " + log);
            }
        }
    }
}

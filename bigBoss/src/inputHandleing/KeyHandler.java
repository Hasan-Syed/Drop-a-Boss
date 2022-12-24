package inputHandleing;

import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.KeyEvent;

/**
 * Handles Keyboards Inputs
 * 
 * @author Hasan Syed
 * @version 1.0
 */
public class KeyHandler implements KeyListener {

    public boolean upKey, downKey, leftKey, rightKey; // Character movement Keys
    public List<Boolean> ability = new ArrayList<>();

    public KeyHandler() {
        init();
    }

    void init() {
        ability.add(false);
        ability.add(false);
        ability.add(false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W -> {
                upKey = true;
            }
            case KeyEvent.VK_S -> {
                downKey = true;
            }
            case KeyEvent.VK_A -> {
                leftKey = true;
            }
            case KeyEvent.VK_D -> {
                rightKey = true;
            }
            case KeyEvent.VK_1 -> {
                ability.set(0, true);
            }
            case KeyEvent.VK_2 -> {
                ability.set(1, true);
            }
            case KeyEvent.VK_3 -> {
                ability.set(2, true);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W -> {
                upKey = false;
            }
            case KeyEvent.VK_S -> {
                downKey = false;
            }
            case KeyEvent.VK_A -> {
                leftKey = false;
            }
            case KeyEvent.VK_D -> {
                rightKey = false;
            }
            case KeyEvent.VK_1 -> {
                ability.set(0, false);
            }
            case KeyEvent.VK_2 -> {
                ability.set(1, false);
            }
            case KeyEvent.VK_3 -> {
                ability.set(2, false);
            }
        }
    }

}

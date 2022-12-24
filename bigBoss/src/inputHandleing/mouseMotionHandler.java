package inputHandleing;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

/**
 * Handles Mouse Motion Inputs
 * 
 * @author Hasan Syed
 * @version 1.0
 */
public class mouseMotionHandler implements MouseMotionListener {
    public int mouseX, mouseY;

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}

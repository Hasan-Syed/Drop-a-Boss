package inputHandleing;

import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

public class mouseMotionHandler implements MouseMotionListener {
    public int mouseX, mouseY;

    public void mouseDragged(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}

package inputHandleing;

import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import java.awt.event.MouseEvent;

/**
 * Handles Mouse Inputs
 * 
 * @author Hasan Syed
 * @version 1.0
 */
public class mouseHandler implements MouseListener {
    public int mouseX, mouseY;
    public boolean rightClick, leftClick;

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftClick = true;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            rightClick = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftClick = false;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            rightClick = false;
        }
    }
}

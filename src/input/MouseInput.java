package input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Keeps the last click position and the current cursor position so the world
 * and HUD can read them without touching AWT events directly.
 */
public class MouseInput implements MouseListener, MouseMotionListener {
	public int clickX;
	public int clickY;
	public int mouseX;
	public int mouseY;

	private boolean clicked;

	@Override
	public void mousePressed(MouseEvent e) {
		clickX = e.getX();
		clickY = e.getY();
		clicked = true;
		System.out.println("CLICO ");
	}

	/**
	 * Returns true once for each click, then resets until the next one.
	 */
	public boolean consumeClick() {
		if (clicked) {
			clicked = false;
			return true;
		}
		return false;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}
}

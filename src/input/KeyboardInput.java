package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Tracks the WASD movement keys as a set of boolean flags the world can poll
 * each frame.
 */
public class KeyboardInput implements KeyListener {
	public boolean up;
	public boolean down;
	public boolean left;
	public boolean right;

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		set(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		set(e.getKeyCode(), false);
	}

	private void set(int key, boolean value) {
		if (key == KeyEvent.VK_W) {
			up = value;
		}
		if (key == KeyEvent.VK_S) {
			down = value;
		}
		if (key == KeyEvent.VK_A) {
			left = value;
		}
		if (key == KeyEvent.VK_D) {
			right = value;
		}
	}
}

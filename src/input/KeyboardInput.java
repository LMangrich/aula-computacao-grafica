package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/** Reduces AWT key events to pollable booleans. */
public class KeyboardInput implements KeyListener {
	public volatile boolean w, a, s, d, q, e, left, right, up, down; //volatile: garante q a thread de renderizacao vai ver o valor atualizado
	//synchronized funciona tb mas eh mais pesada 

	@Override
	public void keyPressed(KeyEvent ev) {
		set(ev.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent ev) {
		set(ev.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent ev) {
	}

	private void set(int code, boolean v) {
		switch (code) {
		case KeyEvent.VK_W: w = v; break; //detecta ql tecla foi pressionada e seta a variavel correspondente
		case KeyEvent.VK_A: a = v; break;
		case KeyEvent.VK_S: s = v; break;
		case KeyEvent.VK_D: d = v; break;
		case KeyEvent.VK_Q: q = v; break;
		case KeyEvent.VK_E: e = v; break;
		case KeyEvent.VK_LEFT: left = v; break;
		case KeyEvent.VK_RIGHT: right = v; break;
		case KeyEvent.VK_UP: up = v; break;
		case KeyEvent.VK_DOWN: down = v; break;
		default: break;
		}
	}
}

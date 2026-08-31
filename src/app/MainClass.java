package app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import engine.GameCanvas;

/**
 * Application entry point: builds the window, drops the engine canvas into it
 * and starts the game loop.
 */
public class MainClass {
	public static void main(String[] args) {
		GameCanvas meuCanvas = new GameCanvas();

		JFrame f = new JFrame();
		f.setSize(640, 480);
		f.setVisible(true);
		f.getContentPane().add(meuCanvas);

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		meuCanvas.start();
	}
}

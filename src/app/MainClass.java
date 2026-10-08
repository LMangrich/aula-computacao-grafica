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
	public static void main(String[] args) throws java.io.IOException {
		GameCanvas meuCanvas = new GameCanvas(); //drawing surface

		JFrame f = new JFrame(); // window shell -- botões principais close/minimize/etc
		f.getContentPane().add(meuCanvas); // adiciona o canvas na janela
		f.pack(); // sizes the frame to fit the canvas's preferred size
		f.setVisible(true); // faz aparecer na tela

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0); // encerra o programa qnd eh fechado
			}
		});

		meuCanvas.start(); // inicia o loop 
	}
}

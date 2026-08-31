package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import input.KeyboardInput;
import input.MouseInput;
import render.Framebuffer;
import render.Renderer;
import resource.ImageLoader;
import world.World;

/**
 * The engine core: a Swing surface plus the game-loop thread. It wires together
 * input, world, framebuffer and renderer, ticks the simulation, and blits the
 * finished frame to screen with a small HUD.
 */
public class GameCanvas extends JPanel implements Runnable {
	private static final int W = 640;
	private static final int H = 480;

	private Thread runner;
	private boolean ativo = true;

	private final Framebuffer framebuffer = new Framebuffer(W, H);
	private final Renderer renderer = new Renderer(framebuffer);
	private final KeyboardInput keyboard = new KeyboardInput();
	private final MouseInput mouse = new MouseInput();
	private final ImageLoader imageLoader = new ImageLoader();
	private final World world = new World(keyboard, mouse);

	private final BufferedImage sprite;
	private final Font font = new Font("", Font.PLAIN, 30);

	private int framecount = 0;
	private int fps = 0;

	private static final String ASSETS = "assets/";

	public GameCanvas() {
		imageLoader.dumpBmpBytes(ASSETS + "imgbmp.bmp");

		setSize(W, H);
		setFocusable(true);

		sprite = imageLoader.loadImage(ASSETS + "fundo.jpg");

		System.out.println("Buffer SIZE " + framebuffer.pixels.length);

		addKeyListener(keyboard);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}

	@Override
	public void paint(Graphics g) {
		framebuffer.clear();
		framebuffer.fill(255, 255, 255, 255);

		for (int i = 0; i < 100; i++) {
			int p0 = 50 * 4 + W * 4 * 100;
			int pos = p0 + i * 4;
			framebuffer.pixels[pos] = (byte) 255;
			framebuffer.pixels[pos + 1] = (byte) 255;
			framebuffer.pixels[pos + 2] = (byte) 0;
			framebuffer.pixels[pos + 3] = (byte) 0;
		}

		renderer.drawImage(sprite, (int) world.posx, (int) world.posy,
				world.filtroR, world.filtroG, world.filtroB);

		g.setFont(font);

		g.setColor(Color.white);
		g.fillRect(0, 0, W, H);

		g.drawImage(framebuffer.image, 0, 0, null);

		g.setColor(Color.black);
		g.drawString("FPS " + fps + " mouse: " + mouse.mouseX + "," + mouse.mouseY, 10, 25);
	}

	public void start() {
		runner = new Thread(this);
		runner.start();
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		long segundo = time / 1000;
		long diftime = 0;
		while (ativo) {
			world.update(diftime);
			paintImmediately(0, 0, W, H);

			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long newtime = System.currentTimeMillis();
			long novoSegundo = newtime / 1000;
			diftime = System.currentTimeMillis() - time;
			time = System.currentTimeMillis();
			framecount++;
			if (novoSegundo != segundo) {
				fps = framecount;
				framecount = 0;
				segundo = novoSegundo;
			}
		}
	}
}

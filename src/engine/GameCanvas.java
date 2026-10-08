package engine;

import java.awt.Graphics;

import javax.swing.JPanel;

import render.Framebuffer;
import render.Renderer;
import world.World;

/**
 * The engine core: a Swing surface plus the game-loop thread. It ticks the
 * world, draws it into the framebuffer with the line rasterizer and blits the
 * frame to screen.
 */
public class GameCanvas extends JPanel implements Runnable {
	private static final int W = 640;
	private static final int H = 480;

	private final Framebuffer framebuffer = new Framebuffer(W, H);
	private final Renderer renderer = new Renderer(framebuffer);
	private final World world = new World();

	public GameCanvas() {
		setSize(W, H);
	}

	@Override
	public void paint(Graphics g) {
		framebuffer.clear();

		// Orthographic projection: drop z, draw each cube edge with Bresenham.
		for (int[] e : World.EDGES) {
			double[] a = world.vertices[e[0]];
			double[] b = world.vertices[e[1]];
			renderer.drawLine((int) Math.round(a[0]), (int) Math.round(a[1]),
					(int) Math.round(b[0]), (int) Math.round(b[1]), 0, 0, 0);
		}

		// The rotation axis, p1 -> p2.
		renderer.drawLine((int) Math.round(world.p1[0]), (int) Math.round(world.p1[1]),
				(int) Math.round(world.p2[0]), (int) Math.round(world.p2[1]), 255, 0, 0);

		g.drawImage(framebuffer.image, 0, 0, null);
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		while (true) {
			long now = System.currentTimeMillis();
			world.update(now - time);
			time = now;
			paintImmediately(0, 0, W, H);

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}

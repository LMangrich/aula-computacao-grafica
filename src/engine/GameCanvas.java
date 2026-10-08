package engine;

import java.awt.Graphics;

import javax.swing.JPanel;

import geometry.Mesh;
import input.KeyboardInput;
import math.Matrix4;
import render.Framebuffer;
import render.Renderer;
import scene.Camera;
import scene.Scene;
import scene.SceneObject;

/**
 * Orchestrator: ticks the scene, and for every object computes
 * mvp = projection * view * model, transforms the mesh vertices and draws the
 * edges with the line rasterizer. Long-lived state lives in {@link Scene}.
 */
public class GameCanvas extends JPanel implements Runnable {
	private static final int W = 640;
	private static final int H = 480;
	private static final double D = 500; // projection plane distance
	private static final double NEAR = 1; // minimum depth kept (w * D)

	private final Framebuffer framebuffer = new Framebuffer(W, H);
	private final Renderer renderer = new Renderer(framebuffer);
	private final KeyboardInput keyboard = new KeyboardInput();
	private final Scene scene;
	private final Matrix4 projection = Matrix4.perspective(D);

	public GameCanvas() {
		setSize(W, H);
		setFocusable(true);
		addKeyListener(keyboard);

		scene = new Scene(new Camera(new double[] { 0, 0, 600 }, new double[] { 0, 0, 0 }), keyboard);
		scene.add(new SceneObject(Mesh.cube(), new double[] { 0, 0, 0 }, 160,
				new double[] { -150, -100, -60 }, new double[] { 150, 100, 60 }, Math.toRadians(90)));
	}

	@Override
	public void paint(Graphics g) {
		framebuffer.clear();

		Matrix4 viewProj = projection.multiply(scene.camera.viewMatrix());

		for (SceneObject o : scene.objects) {
			Matrix4 mvp = viewProj.multiply(o.modelMatrix());

			double[][] screen = new double[o.mesh.vertices.length][];
			for (int i = 0; i < screen.length; i++) {
				double[] v = o.mesh.vertices[i];
				screen[i] = toScreen(mvp.transformHomogeneous(v[0], v[1], v[2]));
			}
			// for (int[] e : o.mesh.edges) {
			// 	drawSegment(screen[e[0]], screen[e[1]], 0, 0, 0);
			// }

			// Rotation axis (world space), drawn in red.
			// drawSegment(toScreen(viewProj.transformHomogeneous(o.axisP1[0], o.axisP1[1], o.axisP1[2])),
					// toScreen(viewProj.transformHomogeneous(o.axisP2[0], o.axisP2[1], o.axisP2[2])), 255, 0, 0);
		}

		g.drawImage(framebuffer.image, 0, 0, null);
	}

	/** Homogeneous divide + screen mapping (origin at the centre, y up). Null if behind the camera. */
	private double[] toScreen(double[] p) {
		if (p[3] * D < NEAR) {
			return null;
		}
		return new double[] { W / 2.0 + p[0] / p[3], H / 2.0 - p[1] / p[3] };
	}

	// private void drawSegment(double[] a, double[] b, int r, int g, int bl) {
	// 	if (a == null || b == null) {
	// 		return;
	// 	}
	// 	renderer.drawLine((int) Math.round(a[0]), (int) Math.round(a[1]),
	// 			(int) Math.round(b[0]), (int) Math.round(b[1]), r, g, bl);
	// }

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		long time = System.currentTimeMillis();
		while (true) {
			long now = System.currentTimeMillis();
			scene.update(now - time);
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

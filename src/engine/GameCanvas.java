package engine;

import java.awt.Graphics;
import java.io.IOException;

import javax.swing.JPanel;

import geometry.ObjLoader;
import input.KeyboardInput;
import math.Matrix4;
import render.Framebuffer;
import render.Renderer;
import scene.Camera;
import scene.Scene;
import scene.SceneObject;

/**
 * Orchestrator: ticks the scene and, every frame, draws the reference base
 * (floor grid + axes at the scene centre) and rasterizes every object: for each
 * triangle it computes mvp = projection * view * model, shades it from its
 * normal and fills it through the z-buffer. Long-lived state lives in {@link Scene}.
 */
public class GameCanvas extends JPanel implements Runnable {
	private static final int W = 800;
	private static final int H = 500;
	private static final double D = 500; // projection plane distance
	private static final double NEAR = 1; // minimum depth kept (w * D)

	private static final double GRID_HALF = 300; // the floor grid spans [-300, 300] on X and Z
	private static final double GRID_STEP = 50;
	private static final double AXIS_LEN = 150; // a multiple of GRID_STEP, so the axes replace whole grid pieces
	private static final String ASSETS = "src/assets/obj_1/";
	private static final double[] LIGHT = normalize(new double[] { 0.4, 1, 0.6 });

	private final Framebuffer framebuffer = new Framebuffer(W, H);
	private final Renderer renderer = new Renderer(framebuffer);
	private final KeyboardInput keyboard = new KeyboardInput();
	private final Scene scene;
	private final Matrix4 projection = Matrix4.perspective(D);

	public GameCanvas() throws IOException {
		setFocusable(true);
		addKeyListener(keyboard);

		// Camera above the floor, looking at the centre of the base.
		scene = new Scene(new Camera(new double[] { 0, 350, 750 }, new double[] { 0, 0, 0 }), keyboard);
		// Each object rests on the floor with its centre at (x, z). The scale brings every
		// file to roughly the same size (~150 units), since each one was exported differently.
		scene.add(new SceneObject(ObjLoader.load(ASSETS + "tank.obj"), 0, 0, 1, new int[] { 110, 140, 90 }));
		scene.add(new SceneObject(ObjLoader.load(ASSETS + "medieval house.obj"), -200, -200, 12, new int[] { 180, 130, 90 }));
		scene.add(new SceneObject(ObjLoader.load(ASSETS + "Mig_29_obj.obj"), 200, -200, 0.7, new int[] { 130, 150, 180 }));
		scene.add(new SceneObject(ObjLoader.load(ASSETS + "Bench_LowRes.obj"), -200, 200, 0.8, new int[] { 160, 110, 70 }));
		scene.add(new SceneObject(ObjLoader.load(ASSETS + "chair_01.obj"), 200, 200, 150, new int[] { 200, 80, 80 }));
	}

	@Override
	public java.awt.Dimension getPreferredSize() {
		return new java.awt.Dimension(W, H);
	}

	@Override
	public void paint(Graphics g) {
		framebuffer.clear();

		Matrix4 viewProj = projection.multiply(scene.camera.viewMatrix());

		drawBase(viewProj);

		for (SceneObject o : scene.objects) {
			Matrix4 model = o.modelMatrix();
			Matrix4 mvp = viewProj.multiply(model);

			int n = o.mesh.vertices.length;
			double[][] world = new double[n][];
			double[][] screen = new double[n][];
			for (int i = 0; i < n; i++) {
				double[] v = o.mesh.vertices[i];
				world[i] = model.transform(v[0], v[1], v[2]);
				screen[i] = toScreen(mvp.transformHomogeneous(v[0], v[1], v[2]));
			}

			for (int[] t : o.mesh.triangles) {
				double[] a = screen[t[0]], b = screen[t[1]], c = screen[t[2]];
				if (a == null || b == null || c == null) {
					continue; // a vertex is behind the camera
				}
				// Flat shading: brightness from the angle between the face normal and the light.
				// abs() lights both sides, since .obj files do not always agree on winding.
				double[] normal = normalize(cross(sub(world[t[1]], world[t[0]]), sub(world[t[2]], world[t[0]])));
				double light = 0.3 + 0.7 * Math.abs(dot(normal, LIGHT));
				renderer.fillTriangle(a[0], a[1], a[2], b[0], b[1], b[2], c[0], c[1], c[2],
						(int) (o.color[0] * light), (int) (o.color[1] * light), (int) (o.color[2] * light));
			}
		}

		g.drawImage(framebuffer.image, 0, 0, null);
	}

	/**
	 * Fixed reference at the scene centre: a floor grid on y = 0 and the X (red),
	 * Y (green) and Z (blue) axes from the origin. Lines are depth-tested, so the
	 * object hides the parts of the base behind it.
	 */
	private void drawBase(Matrix4 viewProj) {
		double[] o = { 0, 0, 0 };
		drawSegment3D(viewProj, o, new double[] { AXIS_LEN, 0, 0 }, 220, 0, 0);
		drawSegment3D(viewProj, o, new double[] { 0, AXIS_LEN, 0 }, 0, 170, 0);
		drawSegment3D(viewProj, o, new double[] { 0, 0, AXIS_LEN }, 0, 0, 220);
		for (double k = -GRID_HALF; k <= GRID_HALF; k += GRID_STEP) {
			// split every grid line into short pieces so the parts in front of the camera
			// still draw when the camera is close and other parts are behind it
			for (double s = -GRID_HALF; s < GRID_HALF; s += GRID_STEP) {
				boolean underAxis = k == 0 && s >= 0 && s < AXIS_LEN; // the X/Z axes are drawn there instead
				if (!underAxis) {
					drawSegment3D(viewProj, new double[] { k, 0, s }, new double[] { k, 0, s + GRID_STEP }, 200, 200, 200);
					drawSegment3D(viewProj, new double[] { s, 0, k }, new double[] { s + GRID_STEP, 0, k }, 200, 200, 200);
				}
			}
		}
	}

	/**
	 * Homogeneous divide + screen mapping (origin at the centre, y up). Returns
	 * { x, y, 1/w } — 1/w is what the z-buffer compares. Null if behind the camera.
	 */
	private double[] toScreen(double[] p) {
		if (p[3] * D < NEAR) {
			return null;
		}
		return new double[] { W / 2.0 + p[0] / p[3], H / 2.0 - p[1] / p[3], 1 / p[3] };
	}

	private void drawSegment3D(Matrix4 viewProj, double[] p, double[] q, int r, int g, int b) {
		double[] a = toScreen(viewProj.transformHomogeneous(p[0], p[1], p[2]));
		double[] c = toScreen(viewProj.transformHomogeneous(q[0], q[1], q[2]));
		if (a == null || c == null) {
			return;
		}
		renderer.drawLine((int) Math.round(a[0]), (int) Math.round(a[1]), a[2],
				(int) Math.round(c[0]), (int) Math.round(c[1]), c[2], r, g, b);
	}

	private static double[] sub(double[] a, double[] b) {
		return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
	}

	private static double[] cross(double[] a, double[] b) {
		return new double[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
	}

	private static double dot(double[] a, double[] b) {
		return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
	}

	private static double[] normalize(double[] a) {
		double l = Math.sqrt(dot(a, a));
		return l < 1e-12 ? new double[] { 0, 0, 0 } : new double[] { a[0] / l, a[1] / l, a[2] / l };
	}

	public void start() {
		requestFocusInWindow(); // key events only reach the component that has focus
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

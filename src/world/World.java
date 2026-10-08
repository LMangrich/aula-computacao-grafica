package world;

import math.Matrix4;

/**
 * Simulation state: a cube spinning about an arbitrary axis defined by two
 * points, p1 and p2.
 */
public class World {
	public final double[] p1 = { 200, 380, -60 };
	public final double[] p2 = { 440, 100, 60 };

	/** Cube edges as pairs of indices into {@link #vertices}. */
	public static final int[][] EDGES = {
			{ 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
			{ 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
			{ 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 } };

	private final double[][] base = new double[8][];
	public final double[][] vertices = new double[8][];

	private double angle = 0;

	public World() {
		double cx = 320, cy = 240, h = 80;
		int i = 0;
		for (double z : new double[] { -h, h }) {
			for (double[] xy : new double[][] { { -h, -h }, { h, -h }, { h, h }, { -h, h } }) {
				base[i++] = new double[] { cx + xy[0], cy + xy[1], z };
			}
		}
		update(0);
	}

	public void update(long diftime) {
		angle += Math.toRadians(90) * diftime / 1000.0;

		Matrix4 rotation = Matrix4.rotationAboutAxis(p1, p2, angle);
		for (int i = 0; i < base.length; i++) {
			vertices[i] = rotation.transform(base[i][0], base[i][1], base[i][2]);
		}
	}
}

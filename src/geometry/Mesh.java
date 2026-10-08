package geometry;

/**
 * Pure geometry: local-space vertices and the edges connecting them. Knows
 * nothing about where it is in the world, how it moves or how it is drawn.
 */
public class Mesh {
	public final double[][] vertices;
	public final int[][] edges;

	public Mesh(double[][] vertices, int[][] edges) {
		this.vertices = vertices;
		this.edges = edges;
	}

	/** Unit cube centred on the origin (side 1). */
	public static Mesh cube() {
		double h = 0.5;
		double[][] v = new double[8][];
		int i = 0;
		for (double z : new double[] { -h, h }) {
			for (double[] xy : new double[][] { { -h, -h }, { h, -h }, { h, h }, { -h, h } }) {
				v[i++] = new double[] { xy[0], xy[1], z };
			}
		}
		int[][] e = {
				{ 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
				{ 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
				{ 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 } };
		return new Mesh(v, e);
	}
}

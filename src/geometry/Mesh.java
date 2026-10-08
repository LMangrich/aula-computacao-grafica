package geometry;

/**
 * Pure geometry: local-space vertices, the edges connecting them and the
 * triangles filling them. Knows nothing about where it is in the world, how it
 * moves or how it is drawn.
 */
public class Mesh {
	public final double[][] vertices;
	public final int[][] edges;
	public final int[][] triangles; // each entry is { a, b, c }, indices into vertices

	public Mesh(double[][] vertices, int[][] edges, int[][] triangles) {
		this.vertices = vertices;
		this.edges = edges;
		this.triangles = triangles;
	}
}

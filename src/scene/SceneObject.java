package scene;

import geometry.Mesh;
import math.Matrix4;

/**
 * A mesh placed in the world: its centre at a point of the floor, a uniform
 * scale and a base colour. It does not move; only the camera does. It only
 * knows how to compute its own model matrix.
 */
public class SceneObject {
	public final Mesh mesh;
	public final double scale;
	public final int[] color; // { r, g, b } before shading
	public final double[] position; // world position of the mesh's centre

	private final double[] pivot; // centre of the mesh's bounding box, in its own coordinates

	/**
	 * Places the mesh so its centre is at (x, z) and its bottom rests on the floor
	 * (y = 0), whatever coordinates the .obj file was exported with.
	 */
	public SceneObject(Mesh mesh, double x, double z, double scale, int[] color) {
		this.mesh = mesh;
		this.scale = scale;
		this.color = color;

		double[] min = mesh.vertices[0].clone();
		double[] max = mesh.vertices[0].clone();
		for (double[] v : mesh.vertices) {
			for (int i = 0; i < 3; i++) {
				min[i] = Math.min(min[i], v[i]);
				max[i] = Math.max(max[i], v[i]);
			}
		}
		this.pivot = new double[] { (min[0] + max[0]) / 2, (min[1] + max[1]) / 2, (min[2] + max[2]) / 2 };
		this.position = new double[] { x, (pivot[1] - min[1]) * scale, z };
	}

	/** Model matrix: Translation(position) * Scale * Translation(-pivot). */
	public Matrix4 modelMatrix() {
		return Matrix4.translation(position[0], position[1], position[2])
				.multiply(Matrix4.scale(scale, scale, scale))
				.multiply(Matrix4.translation(-pivot[0], -pivot[1], -pivot[2]));
	}
}

package scene;

import geometry.Mesh;
import math.Matrix4;

/**
 * A mesh placed in the world, spinning about an axis defined by two world-space
 * points. It only knows how to compute its own model matrix.
 */
public class SceneObject {
	public final Mesh mesh;
	public final double[] position;
	public final double scale;
	public final double[] axisP1;
	public final double[] axisP2;
	public final double angularSpeed; // rad/s

	private double angle = 0;

	public SceneObject(Mesh mesh, double[] position, double scale,
			double[] axisP1, double[] axisP2, double angularSpeed) {
		this.mesh = mesh;
		this.position = position;
		this.scale = scale;
		this.axisP1 = axisP1;
		this.axisP2 = axisP2;
		this.angularSpeed = angularSpeed;
	}

	public void update(long diftime) {
		angle += angularSpeed * diftime / 1000.0;
	}

	/** Model matrix: Rotation about the axis * Translation * Scale. */
	public Matrix4 modelMatrix() {
		return Matrix4.rotationAboutAxis(axisP1, axisP2, angle)
				.multiply(Matrix4.translation(position[0], position[1], position[2]))
				.multiply(Matrix4.scale(scale, scale, scale));
	}
}

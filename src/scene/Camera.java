package scene;

import input.KeyboardInput;
import math.Matrix4;

/**
 * Viewer: eye, target and up vector. Builds the view matrix and reacts to
 * navigation input. Knows nothing about what is in the scene.
 */
public class Camera {
	private static final double[] UP = { 0, 1, 0 };

	public double[] eye;
	public double[] target;

	public Camera(double[] eye, double[] target) {
		this.eye = eye;
		this.target = target;
	}

	public Matrix4 viewMatrix() {
		return Matrix4.lookAt(eye, target, UP);
	}

	/**
	 * W/S: forward/back, A/D: strafe, Q/E: down/up (eye and target move together);
	 * arrow keys: orbit the eye around the target.
	 */
	public void update(long diftime, KeyboardInput k) {
		double dt = diftime / 1000.0;
		double speed = 300 * dt;

		double[] forward = normalize(sub(target, eye));
		double[] right = normalize(cross(forward, UP));

		double[] move = { 0, 0, 0 };
		if (k.w) add(move, forward, speed);
		if (k.s) add(move, forward, -speed);
		if (k.d) add(move, right, speed);
		if (k.a) add(move, right, -speed);
		if (k.e) add(move, UP, speed);
		if (k.q) add(move, UP, -speed);
		for (int i = 0; i < 3; i++) {
			eye[i] += move[i];
			target[i] += move[i];
		}

		double orbit = 0;
		if (k.left) orbit += 1.5 * dt;
		if (k.right) orbit -= 1.5 * dt;
		if (orbit != 0) {
			double[] axisTop = { target[0], target[1] + 1, target[2] };
			eye = Matrix4.rotationAboutAxis(target, axisTop, orbit).transform(eye[0], eye[1], eye[2]);
		}
	}

	private static void add(double[] out, double[] v, double s) {
		for (int i = 0; i < 3; i++) {
			out[i] += v[i] * s;
		}
	}

	private static double[] sub(double[] a, double[] b) {
		return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
	}

	private static double[] cross(double[] a, double[] b) {
		return new double[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
	}

	private static double[] normalize(double[] a) {
		double l = Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
		return new double[] { a[0] / l, a[1] / l, a[2] / l };
	}
}

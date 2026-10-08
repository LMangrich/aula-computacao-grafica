package scene;

import input.KeyboardInput;
import math.Matrix4;

/**
 * Viewer: eye, target and up vector. Builds the view matrix and reacts to
 * navigation input. Knows nothing about what is in the scene.
 */
public class Camera {
	private static final double[] UP = { 0, 1, 0 };
	private static final double[] CENTER = { 0, 0, 0 }; // fixed orbit pivot: the middle of the scene
	private static final double MIN_RADIUS = 50;
	private static final double MAX_ELEVATION = Math.toRadians(85);

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
	 * left/right arrows: orbit the eye around the fixed scene centre; up/down arrows: raise/lower it
	 * while still looking at the centre (elevation is clamped so the view never flips over the poles).
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
		double pitch = 0;
		if (k.up) pitch += 1.5 * dt;
		if (k.down) pitch -= 1.5 * dt;
		if (orbit != 0 || pitch != 0) {
			// Check first: the scene centre must be in the middle of the view before orbiting.
			if (recenter(dt)) {
				orbitAroundCenter(orbit, pitch);
			}
		}
	}

	/**
	 * Eases the look-at point back to the scene centre (the middle of the screen).
	 * Returns true once the view is centred, so orbiting only happens around a centred pivot.
	 */
	private boolean recenter(double dt) {
		double[] off = sub(CENTER, target);
		double dist = Math.sqrt(off[0] * off[0] + off[1] * off[1] + off[2] * off[2]);
		if (dist < 1) {
			target = CENTER.clone();
			return true;
		}
		double step = Math.min(1, 6 * dt); // fraction of the remaining offset closed this frame
		for (int i = 0; i < 3; i++) {
			target[i] += off[i] * step;
		}
		return false;
	}

	/** Orbits the eye around the fixed scene centre and looks at it, so the pivot never drifts. */
	private void orbitAroundCenter(double yaw, double pitch) {
		if (length(sub(eye, CENTER)) < MIN_RADIUS) { // avoid orbiting through the pivot itself
			double[] dir = sub(eye, CENTER);
			double len = Math.max(length(dir), 1e-9);
			for (int i = 0; i < 3; i++) {
				eye[i] = CENTER[i] + (len < 1e-6 ? (i == 2 ? 1 : 0) : dir[i] / len) * MIN_RADIUS;
			}
		}
		if (yaw != 0) {
			double[] axisTop = { CENTER[0], CENTER[1] + 1, CENTER[2] };
			eye = Matrix4.rotationAboutAxis(CENTER, axisTop, yaw).transform(eye[0], eye[1], eye[2]);
		}
		if (pitch != 0) {
			double[] dir = sub(eye, CENTER);
			double elevation = Math.asin(dir[1] / Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1] + dir[2] * dir[2]));
			pitch = Math.max(-MAX_ELEVATION - elevation, Math.min(MAX_ELEVATION - elevation, pitch));
			double[] r = normalize(cross(normalize(sub(CENTER, eye)), UP));
			double[] axisEnd = { CENTER[0] + r[0], CENTER[1] + r[1], CENTER[2] + r[2] };
			eye = Matrix4.rotationAboutAxis(CENTER, axisEnd, -pitch).transform(eye[0], eye[1], eye[2]);
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

	private static double length(double[] a) {
		return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
	}

	private static double[] normalize(double[] a) {
		double l = Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
		return new double[] { a[0] / l, a[1] / l, a[2] / l };
	}
}

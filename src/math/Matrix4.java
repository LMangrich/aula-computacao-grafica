package math;

/**
 * 4x4 matrix in homogeneous coordinates (column-vector convention, P' = M * P),
 * as in the 3D transformation lecture. Only what the axis rotation needs.
 */
public class Matrix4 {
	public final double[][] m = new double[4][4];

	public static Matrix4 identity() {
		Matrix4 r = new Matrix4();
		for (int i = 0; i < 4; i++) {
			r.m[i][i] = 1;
		}
		return r;
	}

	public static Matrix4 translation(double tx, double ty, double tz) {
		Matrix4 r = identity();
		r.m[0][3] = tx;
		r.m[1][3] = ty;
		r.m[2][3] = tz;
		return r;
	}

	/** Counter-clockwise rotation about the Z axis (Rz of the lecture). */
	public static Matrix4 rotationZ(double theta) {
		Matrix4 r = identity();
		double c = Math.cos(theta);
		double s = Math.sin(theta);
		r.m[0][0] = c;
		r.m[0][1] = -s;
		r.m[1][0] = s;
		r.m[1][1] = c;
		return r;
	}

	public Matrix4 multiply(Matrix4 o) {
		Matrix4 r = new Matrix4();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				double sum = 0;
				for (int k = 0; k < 4; k++) {
					sum += m[i][k] * o.m[k][j];
				}
				r.m[i][j] = sum;
			}
		}
		return r;
	}

	public Matrix4 transpose() {
		Matrix4 r = new Matrix4();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				r.m[i][j] = m[j][i];
			}
		}
		return r;
	}

	/** Transforms the point (x, y, z, 1) and returns {x', y', z'}. */
	public double[] transform(double x, double y, double z) {
		double[] out = new double[3];
		for (int i = 0; i < 3; i++) {
			out[i] = m[i][0] * x + m[i][1] * y + m[i][2] * z + m[i][3];
		}
		return out;
	}

	/**
	 * Rotation by theta about the axis through p1 and p2:
	 * P' = T^-1 * M^-1 * R * M * T * P.
	 * <ol>
	 * <li>T: move p1 to the origin.</li>
	 * <li>M: rows U, V, W (orthonormal, W along the axis), so the axis maps to Z.</li>
	 * <li>R: rotate by theta about Z.</li>
	 * <li>M^-1 = M^T: back to the original orientation.</li>
	 * <li>T^-1: move back to p1.</li>
	 * </ol>
	 */
	public static Matrix4 rotationAboutAxis(double[] p1, double[] p2, double theta) {
		// W = s / |s|, with s = p2 - p1
		double[] w = normalize(sub(p2, p1));

		// V = (W x k) / |W x k|; if W is parallel to k any perpendicular vector works
		double[] a = cross(w, new double[] { 0, 0, 1 });
		if (length(a) < 1e-9) {
			a = cross(w, new double[] { 1, 0, 0 });
		}
		double[] v = normalize(a);

		// U = V x W completes the right-handed orthonormal system
		double[] u = cross(v, w);

		Matrix4 t = translation(-p1[0], -p1[1], -p1[2]);
		Matrix4 tInv = translation(p1[0], p1[1], p1[2]);

		Matrix4 basis = identity();
		for (int j = 0; j < 3; j++) {
			basis.m[0][j] = u[j];
			basis.m[1][j] = v[j];
			basis.m[2][j] = w[j];
		}

		return tInv.multiply(basis.transpose()).multiply(rotationZ(theta)).multiply(basis).multiply(t);
	}

	private static double[] sub(double[] a, double[] b) {
		return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
	}

	private static double[] cross(double[] a, double[] b) {
		return new double[] {
				a[1] * b[2] - a[2] * b[1],
				a[2] * b[0] - a[0] * b[2],
				a[0] * b[1] - a[1] * b[0] };
	}

	private static double length(double[] a) {
		return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
	}

	private static double[] normalize(double[] a) {
		double len = length(a);
		return new double[] { a[0] / len, a[1] / len, a[2] / len };
	}
}

package geometry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Stateless loader: reads the "v" and "f" lines of a Wavefront .obj file and
 * returns a wireframe {@link Mesh} (every face edge, without duplicates).
 */
public class ObjLoader {
	private ObjLoader() {
	}

	public static Mesh load(String path) throws IOException {
		List<double[]> vertices = new ArrayList<>();
		Set<Long> edgeKeys = new LinkedHashSet<>();

		try (BufferedReader in = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = in.readLine()) != null) {
				String[] t = line.trim().split("\\s+");
				if (t[0].equals("v")) {
					vertices.add(new double[] {
							Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]) });
				} else if (t[0].equals("f")) {
					int n = t.length - 1;
					for (int k = 0; k < n; k++) {
						int a = index(t[1 + k], vertices.size());
						int b = index(t[1 + (k + 1) % n], vertices.size());
						edgeKeys.add((long) Math.min(a, b) << 32 | Math.max(a, b));
					}
				}
			}
		}

		int[][] edges = new int[edgeKeys.size()][];
		int i = 0;
		for (long key : edgeKeys) {
			edges[i++] = new int[] { (int) (key >> 32), (int) (key & 0xffffffffL) };
		}
		return new Mesh(vertices.toArray(new double[0][]), edges);
	}

	/** Parses "v", "v/vt" or "v/vt/vn"; handles 1-based and negative indices. */
	private static int index(String token, int vertexCount) {
		int idx = Integer.parseInt(token.split("/")[0]);
		return idx > 0 ? idx - 1 : vertexCount + idx;
	}
}

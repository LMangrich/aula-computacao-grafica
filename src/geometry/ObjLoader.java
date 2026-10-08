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
 * returns a {@link Mesh}: every face edge (without duplicates) and every face
 * split into triangles (fan triangulation), so it can be filled.
 */
public class ObjLoader {
	private ObjLoader() {
	}

	public static Mesh load(String path) throws IOException {
		List<double[]> vertices = new ArrayList<>();
		Set<Long> edgeKeys = new LinkedHashSet<>();
		List<int[]> triangles = new ArrayList<>();

		try (BufferedReader in = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = in.readLine()) != null) {
				String[] t = line.trim().split("\\s+");
				if (t[0].equals("v")) {
					vertices.add(new double[] {
							Double.parseDouble(t[1]), Double.parseDouble(t[2]), Double.parseDouble(t[3]) });
				} else if (t[0].equals("f")) {
					int n = t.length - 1;
					int[] face = new int[n];
					for (int k = 0; k < n; k++) {
						face[k] = index(t[1 + k], vertices.size());
					}
					for (int k = 0; k < n; k++) {
						int a = face[k];
						int b = face[(k + 1) % n];
						edgeKeys.add((long) Math.min(a, b) << 32 | Math.max(a, b));
					}
					for (int k = 1; k + 1 < n; k++) { // polygon -> triangles sharing face[0]
						triangles.add(new int[] { face[0], face[k], face[k + 1] });
					}
				}
			}
		}

		int[][] edges = new int[edgeKeys.size()][];
		int i = 0;
		for (long key : edgeKeys) {
			edges[i++] = new int[] { (int) (key >> 32), (int) (key & 0xffffffffL) };
		}
		return new Mesh(vertices.toArray(new double[0][]), edges, triangles.toArray(new int[0][]));
	}

	/** Parses "v", "v/vt" or "v/vt/vn"; handles 1-based and negative indices. */
	private static int index(String token, int vertexCount) {
		int idx = Integer.parseInt(token.split("/")[0]);
		return idx > 0 ? idx - 1 : vertexCount + idx;
	}
}

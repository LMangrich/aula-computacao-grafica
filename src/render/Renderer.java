package render;

/**
 * Software rasterizer. Draws primitives straight into a {@link Framebuffer}.
 */
public class Renderer {
	private final Framebuffer target;

	public Renderer(Framebuffer target) {
		this.target = target;
	}

	// problema: linha tem infinitos pontos, porém na tela eles não são contínuos (discretos)
	// como resolver: pega o pixel mais próximo de onde a linha deve estar e usa só adição/subtração
	
	/** Generalized Bresenham: draws a line from (x1,y1) to (x2,y2) in every octant. */
	public void drawLine(int x1, int y1, int x2, int y2, int r, int g, int b) {
		int dx = Math.abs(x2 - x1); // horizontal distance -> positivo por convenção
		int dy = -Math.abs(y2 - y1); // vertical distance -> negativo por convenção, para facilitar o cálculo do erro
		int sx = x1 < x2 ? 1 : -1; // step right or left
		int sy = y1 < y2 ? 1 : -1; // step down or up

		int err = dx + dy; // inicializa o erro, que é a soma das distâncias horizontais e verticais (dx + dy) -> negativo pq dy é negativo

		int x = x1;
		int y = y1;

		while (true) { 
			target.setPixel(x, y, r, g, b); //passa pixel por pixel até ir no destino
			if (x == x2 && y == y2) {
				break;
			}

			int e2 = 2 * err; // doubled to avoid division and keep precision
			if (e2 >= dy) { // step in X
				err += dy;
				x += sx;
			}
			if (e2 <= dx) { // step in Y
				err += dx;
				y += sy;
			}
		}
	}

	/**
	 * Same Bresenham walk, but every pixel is depth-tested: invW (1/w) is
	 * interpolated linearly in screen space, which is exact for perspective.
	 */
	public void drawLine(int x1, int y1, double iw1, int x2, int y2, double iw2, int r, int g, int b) {
		int dx = Math.abs(x2 - x1);
		int dy = -Math.abs(y2 - y1);
		int sx = x1 < x2 ? 1 : -1;
		int sy = y1 < y2 ? 1 : -1;
		int err = dx + dy;
		int steps = Math.max(dx, -dy);

		int x = x1;
		int y = y1;
		int n = 0;
		while (true) {
			double t = steps == 0 ? 0 : (double) n / steps;
			target.setPixelDepth(x, y, iw1 + (iw2 - iw1) * t, r, g, b);
			if (x == x2 && y == y2) {
				break;
			}
			int e2 = 2 * err;
			if (e2 >= dy) {
				err += dy;
				x += sx;
			}
			if (e2 <= dx) {
				err += dx;
				y += sy;
			}
			n++;
		}
	}

	/**
	 * Fills a triangle given in screen space. Every pixel centre inside the
	 * bounding box is tested with edge functions (barycentric coordinates); inside
	 * pixels get 1/w interpolated and go through the z-buffer, so nearer surfaces
	 * hide farther ones regardless of drawing order.
	 */
	public void fillTriangle(double x0, double y0, double iw0, double x1, double y1, double iw1,
			double x2, double y2, double iw2, int r, int g, int b) {
		double area = (x1 - x0) * (y2 - y0) - (x2 - x0) * (y1 - y0);
		if (Math.abs(area) < 1e-9) {
			return; // degenerate (seen edge-on)
		}

		int minX = Math.max(0, (int) Math.floor(Math.min(x0, Math.min(x1, x2))));
		int maxX = Math.min(target.width - 1, (int) Math.ceil(Math.max(x0, Math.max(x1, x2))));
		int minY = Math.max(0, (int) Math.floor(Math.min(y0, Math.min(y1, y2))));
		int maxY = Math.min(target.height - 1, (int) Math.ceil(Math.max(y0, Math.max(y1, y2))));

		for (int y = minY; y <= maxY; y++) {
			double py = y + 0.5;
			for (int x = minX; x <= maxX; x++) {
				double px = x + 0.5;
				// barycentric weights: how much of each vertex this pixel takes
				double w0 = ((x1 - px) * (y2 - py) - (x2 - px) * (y1 - py)) / area;
				double w1 = ((x2 - px) * (y0 - py) - (x0 - px) * (y2 - py)) / area;
				double w2 = 1 - w0 - w1;
				if (w0 < 0 || w1 < 0 || w2 < 0) {
					continue; // pixel outside the triangle
				}
				target.setPixelDepth(x, y, w0 * iw0 + w1 * iw1 + w2 * iw2, r, g, b);
			}
		}
	}
}

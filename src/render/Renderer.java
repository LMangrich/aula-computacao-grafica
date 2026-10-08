package render;

/**
 * Software rasterizer. Draws primitives straight into a {@link Framebuffer}.
 */
public class Renderer {
	private final Framebuffer target;

	public Renderer(Framebuffer target) {
		this.target = target;
	}

	/** Generalized Bresenham: draws a line from (x1,y1) to (x2,y2) in every octant. */
	public void drawLine(int x1, int y1, int x2, int y2, int r, int g, int b) {
		int dx = Math.abs(x2 - x1); // horizontal distance
		int dy = -Math.abs(y2 - y1); // vertical distance (negative)
		int sx = x1 < x2 ? 1 : -1; // step right or left
		int sy = y1 < y2 ? 1 : -1; // step down or up
		int err = dx + dy; // error between the real line and the current pixel

		int x = x1;
		int y = y1;

		while (true) {
			target.setPixel(x, y, r, g, b);

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
}

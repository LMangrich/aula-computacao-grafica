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
}

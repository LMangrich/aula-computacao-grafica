package render;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Software rasterizer. Draws primitives and blits images straight into a
 * {@link Framebuffer}, applying a per-channel colour filter plus grayscale
 * averaging while copying.
 */
public class Renderer {
	private final Framebuffer target;

	public Renderer(Framebuffer target) {
		this.target = target;
	}

	public void drawImage(BufferedImage image, int x, int y, float fr, float fg, float fb) {
		byte[] imgBuffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		byte[] out = target.pixels;
		int W = target.width;

		int iw = image.getWidth();
		int ih = image.getHeight();

		for (int yi = 0; yi < ih; yi++) {
			for (int xi = 0; xi < iw; xi++) {
				int pixi = yi * iw * 4 + xi * 4;
				int pixb = (yi + y) * W * 4 + (xi + x) * 4;
				out[pixb] = imgBuffer[pixi];

				int b = (imgBuffer[pixi + 1] & 0xff);
				int g = (imgBuffer[pixi + 2] & 0xff);
				int r = (imgBuffer[pixi + 3] & 0xff);

				b = (int) (b * fb);
				g = (int) (g * fg);
				r = (int) (r * fr);

				int media = (b + g + r) / 3;
				b = media;
				g = media;
				r = media;

				out[pixb + 1] = (byte) (b & 0xff);
				out[pixb + 2] = (byte) (g & 0xff);
				out[pixb + 3] = (byte) (r & 0xff);
			}
		}
	}

	public void drawHorizontalLine(int x, int y, int w) {
		byte[] px = target.pixels;
		int pospix = y * (target.width * 4) + x * 4;
		for (int i = 0; i < w; i++) {
			px[pospix] = (byte) 255;
			px[pospix + 1] = (byte) 0;
			px[pospix + 2] = (byte) 0;
			px[pospix + 3] = (byte) 0;
			pospix += 4;
		}
	}

	public void drawVerticalLine(int x, int y, int h) {
		byte[] px = target.pixels;
		int pospix = y * (target.width * 4) + x * 4;
		for (int i = 0; i < h; i++) {
			px[pospix] = (byte) 255;
			px[pospix + 1] = (byte) 0;
			px[pospix + 2] = (byte) 0;
			px[pospix + 3] = (byte) 255;
			pospix += target.width * 4;
		}
	}
	//Bresenham generalizado 
	public void drawLine(int x1, int y1, int x2, int y2, int r, int g, int b) {
		int dx = Math.abs(x2 - x1); // distancia horizontal
		int dy = -Math.abs(y2 - y1); // distancia vertical
		int sx = x1 < x2 ? 1 : -1; // direita ou esquerda
		int sy = y1 < y2 ? 1 : -1; // baixo ou cima
		int err = dx + dy; // erro inicial - a distância vertical entre a linha real e o pixel atual

		int x = x1;
		int y = y1;

		while (true) {
			target.setPixel(x, y, r, g, b);

			if (x == x2 && y == y2) {
				break;
			}

			int e2 = 2 * err; //dobra erro para evitar divisão e não perder precisão
			if (e2 >= dy) { //avança em X, equivale a err >= dy/2
				err += dy;
				x += sx;
			}
			if (e2 <= dx) { //avança em Y, err <= dx/2
				err += dx;
				y += sy;
			}
		}
	}
}

package render;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Owns the raw video memory: a TYPE_4BYTE_ABGR image and a direct view of its
 * byte array. Every pixel write in the engine goes through this buffer.
 */
public class Framebuffer {
	public final int width;
	public final int height;
	public final BufferedImage image;
	public final byte[] pixels;

	public Framebuffer(int width, int height) {
		this.width = width;
		this.height = height;
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		this.pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
		}
	}

	public void fill(int a, int b, int g, int r) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int pos = i * 4 + width * 4 * j;
				pixels[pos] = (byte) a;
				pixels[pos + 1] = (byte) b;
				pixels[pos + 2] = (byte) g;
				pixels[pos + 3] = (byte) r;
			}
		}
	}

	public void setPixel(int x, int y, int r, int g, int b) {
		int pospix = y * (width * 4) + x * 4;
		pixels[pospix] = (byte) 255;
		pixels[pospix + 1] = (byte) (b & 0xff);
		pixels[pospix + 2] = (byte) (g & 0xff);
		pixels[pospix + 3] = (byte) (r & 0xff);
	}
}

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
	public final BufferedImage image; // imagem que vai ser desenhada na tela, eh o que a jframe vai mostrar
	public final byte[] pixels; // visão direta da imagem, para escrever diretamente na memoria de video
	public final double[] depth; // z-buffer: 1/w por pixel (maior = mais perto da camera), 0 = vazio

	public Framebuffer(int width, int height) {
		this.width = width;
		this.height = height;
		// TYPE_4BYTE_ABGR: 4 bytes por pixel, alpha, blue, green, red
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		// pega a visão direta da imagem, para escrever diretamente na memoria de video
		this.pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		this.depth = new double[width * height];
	}

	/** Fills the whole buffer with opaque white and empties the z-buffer. */
	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = (byte) 255;
		}
		java.util.Arrays.fill(depth, 0);
	}

	/**
	 * Depth-tested write: only draws if invW (1/w, larger = nearer) is nearer than
	 * what is already stored at (x, y).
	 */
	public void setPixelDepth(int x, int y, double invW, int r, int g, int b) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return;
		}
		int i = y * width + x;
		if (invW <= depth[i]) {
			return;
		}
		depth[i] = invW;
		setPixel(x, y, r, g, b);
	}

	/** Writes one pixel directly into the byte array; out-of-bounds writes are ignored. */
	public void setPixel(int x, int y, int r, int g, int b) {
		// controla o out-of-bounds, para nao escrever fora da memoria de video (0,0) -> eh o top-left
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return;
		}

		// calcula a posição do pixel na memoria de video, considerando q cada pixel tem 4 bytes
		int pospix = y * (width * 4) + x * 4;

		// escreve os 4 bytes do pixel na memoria de video, na ordem ABGR
		pixels[pospix] = (byte) 255; // alpha: 255 = opaque, 0 = transparent
		pixels[pospix + 1] = (byte) (b & 0xff); // & 0xff: garante q o valor esteja entre 0 e 255, azul
		pixels[pospix + 2] = (byte) (g & 0xff); // & 0xff: garante q o valor esteja entre 0 e 255, verde
		pixels[pospix + 3] = (byte) (r & 0xff); // & 0xff: garante q o valor esteja entre 0 e 255, vermelho
	}
}

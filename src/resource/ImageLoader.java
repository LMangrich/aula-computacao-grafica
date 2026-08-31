package resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * All disk I/O for images: decoding a file into a TYPE_4BYTE_ABGR buffer the
 * renderer can read, plus the raw BMP byte dump used for inspection.
 */
public class ImageLoader {

	public BufferedImage loadImage(String filename) {
		try {
			BufferedImage src = ImageIO.read(new File(filename));

			BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			out.getGraphics().drawImage(src, 0, 0, null);

			return out;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void dumpBmpBytes(String filename) {
		File f = new File(filename);
		try (FileInputStream fin = new FileInputStream(f)) {
			byte[] todosodbytes = new byte[64000];
			int byteslidos = fin.read(todosodbytes);
			System.out.println("Bytes Lidos " + byteslidos);
			for (int i = 0; i < byteslidos; i++) {
				System.out.println(i + ": " + todosodbytes[i]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

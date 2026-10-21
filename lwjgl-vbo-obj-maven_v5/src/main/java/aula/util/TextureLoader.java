package aula.util;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import static org.lwjgl.opengl.GL11.*;

public class TextureLoader {
    private static final int BYTES_PER_PIXEL = 4;//3 for RGB, 4 for RGBA

    public static int loadTexture(BufferedImage image){
    	
    	System.out.println(" "+image.getWidth() +" "+ image.getHeight());

          int[] pixels = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

            ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

            for(int y = 0; y < image.getHeight(); y++){
                for(int x = 0; x < image.getWidth(); x++){
                    int pixel = pixels[y * image.getWidth() + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                    buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                    buffer.put((byte) (pixel & 0xFF));               // Blue component
                    buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
                }
            }

            buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

            // You now have a ByteBuffer filled with the color data of each pixel.
            // Now just create a texture ID and bind it. Then you can load it using 
            // whatever OpenGL method you want, for example:

          int textureID = glGenTextures(); //Generate texture ID
            glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

            //Setup wrap mode
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

            //Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            //Send texel data to OpenGL
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            //Return the texture ID so we can bind it later again
          return textureID;
       }

       public static BufferedImage loadImage(String loc)
       {
            try {
               return ImageIO.read(new FileInputStream(loc));
            } catch (IOException e) {
                //Error Handling Here
            }
           return null;
       }

       public static BufferedImage loadPcx(String file) throws IOException { // lê um PCX e devolve uma imagem (BufferedImage)
        byte[] bytes = Files.readAllBytes(Paths.get(file)); // lê o arquivo inteiro como um array de bytes
        ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN); // leitor com posição; PCX guarda números com o byte menos significativo primeiro

        // ---- header do pcx (128 bytes) ----
        int manufacturer = buf.get() & 0xFF; // byte 0: sempre 10 (identifica PCX); & 0xFF evita número negativo
        int version = buf.get() & 0xFF; // byte 1: versão (5 = tem paleta de 256 cores)
        int encoding = buf.get() & 0xFF; // byte 2: tipo de compressão (1 = run-length)
        int bitsPerPixel = buf.get() & 0xFF; // byte 3: bits por pixel (8 = um byte por pixel)
        int xmin = buf.getShort(); // bytes 4-5: coluna da borda esquerda
        int ymin = buf.getShort(); // bytes 6-7: linha da borda de cima
        int xmax = buf.getShort(); // bytes 8-9: coluna da borda direita
        int ymax = buf.getShort(); // bytes 10-11: linha da borda de baixo

        int planes = buf.get(65) & 0xFF; // byte 65: número de camadas de cor (1 aqui); get(65) lê nessa posição sem mover a posição
        int bytesPerLine = buf.getShort(66); // bytes 66-67: tamanho de uma linha de pixels no arquivo

        int width = xmax - xmin + 1; // largura: soma 1 porque a primeira e a última coluna contam
        int height = ymax - ymin + 1; // altura: mesma conta

        System.out.println("manufacturer=" + manufacturer + " version=" + version
                + " encoding=" + encoding + " bitsPerPixel=" + bitsPerPixel); // mostra o header para conferir
        System.out.println("width=" + width + " height=" + height
                + " planes=" + planes + " bytesPerLine=" + bytesPerLine); // mostra o tamanho para conferir

        // ---- pixels comprimidos ----
        buf.position(128); // o header tem sempre 128 bytes; os pixels começam depois dele

        byte[] indices = new byte[bytesPerLine * planes * height]; // um número por pixel (320 x 1 x 200 = 64000)
        int pos = 0; // próxima posição livre em indices

        while (pos < indices.length) { // repete até descomprimir todos os pixels
            int b = buf.get() & 0xFF; // lê o próximo byte do arquivo
            if (b >= 0xC0) { // 192 ou mais: este byte é uma repetição, não um pixel
                int count = b & 0x3F; // os 6 bits de baixo são quantas vezes repetir
                byte value = buf.get(); // o byte seguinte é o valor a repetir
                for (int i = 0; i < count; i++) { // repete 'count' vezes
                    indices[pos++] = value; // grava o valor e avança a posição
                }
            } else { // menos que 192: é um pixel só
                indices[pos++] = (byte) b; // grava o pixel uma vez e avança a posição
            }
        }

        // ---- paleta de cores ----
        int marker = buf.get() & 0xFF; // byte 12 que avisa "a paleta vem agora"
        System.out.println("marcador da paleta = " + marker); // mostra para conferir (deve ser 12)

        byte[] palette = new byte[256 * 3]; // 256 cores x 3 bytes (vermelho, verde, azul)
        buf.get(palette); // preenche o array com os 768 bytes da paleta

        // ---- pinta a imagem ----
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); // imagem vazia, cada pixel guardado como um int
        for (int y = 0; y < height; y++) { // percorre as linhas
            for (int x = 0; x < width; x++) { // percorre as colunas de cada linha
                int index = indices[y * bytesPerLine + x] & 0xFF; // número da cor deste pixel (linha y começa em y * bytesPerLine)
                int r = palette[index * 3] & 0xFF; // vermelho dessa cor na paleta
                int g = palette[index * 3 + 1] & 0xFF; // verde dessa cor
                int b = palette[index * 3 + 2] & 0xFF; // azul dessa cor
                image.setRGB(x, y, (r << 16) | (g << 8) | b); // junta r, g, b em um só int (0xRRGGBB) e pinta o pixel
            }
        }

        return image; // devolve a imagem pronta para o loadTexture
    }
}

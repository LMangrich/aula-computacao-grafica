package aula.md2;

import java.io.IOException;
import java.nio.ByteBuffer;

// guarda os 17 números do começo do arquivo MD2 (o header), que dizem quantos itens tem e onde cada parte começa
public class Md2Header {
	int skinWidth, skinHeight; // tamanho da imagem da skin, em pixels
	int frameSize; // quantos bytes ocupa cada frame no arquivo
	int numSkins, numVertices, numTexCoords, numTriangles, numGlCmds, numFrames; // quantidade de itens de cada parte
	int offsetSkins, offsetTexCoords, offsetTriangles, offsetFrames, offsetGlCmds, offsetEnd; // byte do arquivo onde cada parte começa

	// lê o header do início do buffer; o buffer precisa estar na posição 0
	public void read(ByteBuffer buf) throws IOException {
		int magic = buf.getInt(); //'magic number' é para checar se é um arquivo MD2 já que vê os primeiros quatro bytes
		int version = buf.getInt();
		System.out.println("magic = " + magic + " | version = " + version);

		if (magic != 844121161 || version != 8) { //Todo arquivo MD2 válido começa com os mesmos 4 bytes, então sempre deve retornar 844121161, a versao é pq Quake II quis que fosse 8 e nao aceita outro valor
			throw new IOException("Não é um MD2 valido");
		}

		// ---- resto do header: cada getInt() lê o próximo campo, na ordem exata do formato ----
		skinWidth = buf.getInt(); // largura da skin em pixels (320)
		skinHeight = buf.getInt(); // altura da skin em pixels (200)
		frameSize = buf.getInt(); // bytes de cada frame (956)
		numSkins = buf.getInt(); // quantas skins o arquivo lista (1)
		numVertices = buf.getInt(); // vértices em cada frame (229)
		numTexCoords = buf.getInt(); // coordenadas de textura (386)
		numTriangles = buf.getInt(); // triângulos do modelo (392)
		numGlCmds = buf.getInt(); // comandos GL, lista opcional que vamos ignorar (2192)
		numFrames = buf.getInt(); // quadros da animação (200)
		offsetSkins = buf.getInt(); // posição no arquivo onde começam as skins (68)
		offsetTexCoords = buf.getInt(); // onde começam as coordenadas de textura (132)
		offsetTriangles = buf.getInt(); // onde começam os triângulos (1676)
		offsetFrames = buf.getInt(); // onde começam os frames (6380)
		offsetGlCmds = buf.getInt(); // onde começam os comandos GL (197580)
		offsetEnd = buf.getInt(); // fim do arquivo (206348)

		// ---- teste pra ver se tá tudo ok: compara com os números entre parênteses acima ----
		System.out.println("skin " + skinWidth + "x" + skinHeight + " | frameSize = " + frameSize);
		System.out.println("skins = " + numSkins + " | vertices = " + numVertices + " | texcoords = " + numTexCoords
				+ " | triangulos = " + numTriangles + " | glcmds = " + numGlCmds + " | frames = " + numFrames);
		System.out.println("offsets: skins = " + offsetSkins + " | texcoords = " + offsetTexCoords
				+ " | triangulos = " + offsetTriangles + " | frames = " + offsetFrames
				+ " | glcmds = " + offsetGlCmds + " | fim = " + offsetEnd);
	}
}

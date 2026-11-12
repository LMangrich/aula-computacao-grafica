package aula.md2;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import aula.obj.Face3D;
import aula.obj.Vector2f;
import aula.obj.Vector3f;

// lê as partes do arquivo MD2 (texcoords, triângulos e frames); cada método devolve a sua lista
public class Md2Loader {

	static ArrayList<Vector2f> decodeTexCoords(ByteBuffer buf, Md2Header h) {
		ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>(); // coordenadas de textura (u, v de 0 a 1), é igual à lista vt do ObjModel
		buf.position(h.offsetTexCoords); // pula direto para onde começam as texcoords (132)

		for (int i = 0; i < h.numTexCoords; i++) { // uma entrada por coordenada (386)
			int s = buf.getShort() & 0xFFFF; // posição horizontal na skin, em pixels
			int t = buf.getShort() & 0xFFFF; // posição vertical na skin, em pixels

			Vector2f uv = new Vector2f();
			uv.x = (float) s / h.skinWidth; // pixels -> valor de 0 a 1 (divide pela largura, 320)
			uv.y = (float) t / h.skinHeight; // pixels -> valor de 0 a 1 (divide pela altura, 200)
			texCoords.add(uv); // guarda na lista, na mesma ordem do arquivo
		}

		int ultima = texCoords.size() - 1; // posição da última texcoord
		System.out.println("texcoords lidas = " + texCoords.size());
		System.out.println("texcoord 0 = (" + texCoords.get(0).x + ", " + texCoords.get(0).y + ")");
		System.out.println("texcoord " + ultima + " = (" + texCoords.get(ultima).x + ", " + texCoords.get(ultima).y + ")");
		return texCoords;
	}

	static ArrayList<Face3D> decodeTriangles(ByteBuffer buf, Md2Header h) {
		ArrayList<Face3D> triangles = new ArrayList<Face3D>(); // triângulos do modelo; reaproveita o Face3D do professor (igual à lista f do ObjModel)
		buf.position(h.offsetTriangles); // pula para onde começam os triângulos (1676)

		for (int i = 0; i < h.numTriangles; i++) { // um triângulo por vez (392)
			Face3D tri = new Face3D();
			tri.nvertices = 3; // todo triângulo MD2 tem 3 cantos

			for (int k = 0; k < 3; k++) {
				tri.v[k] = buf.getShort() & 0xFFFF; // índices dos 3 vértices
			}
			for (int k = 0; k < 3; k++) {
				tri.t[k] = buf.getShort() & 0xFFFF; // índices das 3 texcoords
			}
			triangles.add(tri); // guarda na lista, na ordem do arquivo
		}

		Face3D first = triangles.get(0);
		Face3D last = triangles.get(h.numTriangles - 1);
		System.out.println("triangulos lidos = " + triangles.size());
		System.out.println("triangulo 0 = v(" + first.v[0] + "," + first.v[1] + "," + first.v[2]
				+ ") t(" + first.t[0] + "," + first.t[1] + "," + first.t[2] + ")");
		System.out.println("triangulo " + (h.numTriangles - 1) + " = v(" + last.v[0] + "," + last.v[1] + "," + last.v[2]
				+ ") t(" + last.t[0] + "," + last.t[1] + "," + last.t[2] + ")");
		return triangles;
	}

	static Md2Frame[] decodeFrames(ByteBuffer buf, Md2Header h) {
		Md2Frame[] frames = new Md2Frame[h.numFrames]; // 200 frames, cada um com 229 vértices

		for (int fr = 0; fr < h.numFrames; fr++) { // um frame por vez
			buf.position(h.offsetFrames + fr * h.frameSize); // pula para o início deste frame (6380 + fr * 956)

			float sx = buf.getFloat(); // escala de x
			float sy = buf.getFloat(); // escala de y
			float sz = buf.getFloat(); // escala de z
			float tx = buf.getFloat(); // deslocamento de x
			float ty = buf.getFloat(); // deslocamento de y
			float tz = buf.getFloat(); // deslocamento de z

			byte[] nameBytes = new byte[16]; // o nome ocupa 16 bytes
			buf.get(nameBytes);
			int len = 0;
			while (len < 16 && nameBytes[len] != 0) { // o texto termina no primeiro byte zero
				len++;
			}
			String name = new String(nameBytes, 0, len);

			Vector3f[] vertices = new Vector3f[h.numVertices]; // posições dos vértices deste frame
			for (int i = 0; i < h.numVertices; i++) { // os 229 vértices deste frame
				int x = buf.get() & 0xFF; // byte de x (0 a 255)
				int y = buf.get() & 0xFF; // byte de y
				int z = buf.get() & 0xFF; // byte de z
				buf.get(); // índice da normal: lido só para avançar, não usamos
				vertices[i] = new Vector3f(x * sx + tx, y * sy + ty, z * sz + tz); // posição real
			}
			frames[fr] = new Md2Frame(name, vertices); // junta nome e vértices num frame só
		}

		int ultimo = frames.length - 1; // posição do último frame
		System.out.println("frames lidos = " + frames.length);
		System.out.println("frame 0 = " + frames[0].name + " | ultimo frame (" + ultimo + ") = " + frames[ultimo].name);
		System.out.println("frame 0, vertice 0 = " + frames[0].vertices[0]);
		System.out.println("frame 0, ultimo vertice = " + frames[0].vertices[h.numVertices - 1]);
		return frames;
	}
}

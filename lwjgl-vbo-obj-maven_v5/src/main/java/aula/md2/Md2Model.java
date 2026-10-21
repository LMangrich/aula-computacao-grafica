package aula.md2;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import aula.obj.Vector2f;
import aula.obj.Face3D;
import aula.obj.Vector3f;

import aula.Model.Model;

public class Md2Model extends Model {
	Md2Header header = new Md2Header(); // dados do header (preenchidos em loadMd2)

	ArrayList<Vector2f> texCoords; // coordenadas de textura (u, v de 0 a 1), é igual à lista vt do ObjModel
	ArrayList<Face3D> triangles; // triângulos do modelo; reaproveita o Face3D do professor (igual à lista f do ObjModel)
	Md2Frame[] frames; // frames[fr].vertices[i] = posição do vértice i no frame fr (como a lista v do ObjModel, mas uma por frame)

	Md2Animation animation = new Md2Animation(); // qual animação está tocando e em qual frame

	public Md2Model(String file) throws IOException { // recebe o caminho do arquivo
		loadMd2(file); // lê o arquivo e guarda os dados na memória
		load(); // prepara os dados para o OpenGL (ainda vazio)
	}

	public void loadMd2(String file) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(file)); //torna a string num caminho de objeto
		//que o readAllbytes acaba lendo do disco e retornando como bytes
		ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN); //bytebyffer.wrap começa a ler os bytes desde o início e marca a posição de leitura
		//o order diz como combinar os 4 bytes em um número; MD2 usa 'little-endian' (o byte menos significante vem antes)

		header.read(buf); // lê e confere os 17 números do header

		texCoords = Md2Loader.decodeTexCoords(buf, header); // lê as coordenadas de textura
		triangles = Md2Loader.decodeTriangles(buf, header); // lê os triângulos
		frames = Md2Loader.decodeFrames(buf, header); // lê os frames com as posições dos vértices
	}

	@Override
	public void load() { // vai montar os buffers para o OpenGL (como o load do ObjModel)
	}

	@Override
	public void draw() { // vai desenhar o modelo a cada frame, com a interpolação
		drawInterpolated(animation.currentFrame, animation.nextFrame, animation.blend); // pose entre o frame atual e o próximo
	}

    void drawInterpolated(int a, int b, float t) { // desenha uma pose entre o frame a e o frame b
		glColor3f(1f, 1f, 1f); // branco: não altera as cores da textura
		glBegin(GL_TRIANGLES); // cada 3 vértices formam um triângulo
		for (Face3D tri : triangles) { // percorre os 392 triângulos
			for (int k = 0; k < 3; k++) { // os 3 cantos do triângulo
				Vector2f uv = texCoords.get(tri.t[k]); // coordenada de textura deste canto
                Vector3f pa = frames[a].vertices[tri.v[k]]; // posição do vértice no frame a
                Vector3f pb = frames[b].vertices[tri.v[k]]; // posição do mesmo vértice no frame b

				float x = pa.x + t * (pb.x - pa.x); // A + t * (B - A), para cada coordenada
				float y = pa.y + t * (pb.y - pa.y);
				float z = pa.z + t * (pb.z - pa.z);

				glTexCoord2f(uv.x, uv.y); // ponto da skin que vai neste vértice (não muda entre frames)
				glVertex3f(x, y, z); // posição misturada entre os dois frames
			}
		}
		glEnd(); // fim dos triângulos
	}

	public void setAnimation(String name) { // escolhe a animação pelo nome: "stand", "run", "attack"...
		animation.setAnimation(name, frames); // o Md2Animation procura o nome nos frames
	}

	public void animate(double time) { // chamado a cada volta do loop, com o tempo em segundos
		animation.animate(time); // o Md2Animation calcula o frame atual
	}
}

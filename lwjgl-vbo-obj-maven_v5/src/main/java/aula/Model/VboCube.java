package aula.Model;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

public class VboCube extends Model{
	int vbo_vertex_handle;
	int vbo_color_handle;
	int vbo_texture_handle;

	int vertices = 36;
	int vertex_size = 3; // X, Y, Z,
	int color_size = 3; // R, G, B,
	int texture_size = 2;

	// os 8 cantos do cubo
	static final float[][] CANTOS = {
		{ -1f, -1f, -1f }, { 1f, -1f, -1f }, { 1f,  1f, -1f }, { -1f,  1f, -1f },
		{ -1f, -1f,  1f }, { 1f, -1f,  1f }, { 1f,  1f,  1f }, { -1f,  1f,  1f },
	};

	// uma cor por canto
	static final float[][] CORES = {
		{ 1f, 0f, 0f }, { 0f, 1f, 0f }, { 0f, 0f, 1f }, { 1f, 0f, 1f },
		{ 1f, 1f, 0f }, { 0f, 1f, 1f }, { 0.8f, 0.8f, 0.8f }, { 0.4f, 0.4f, 0.4f },
	};

	// cada face: 2 triangulos, indices em CANTOS / CORES
	static final int[][] FACES = {
		{ 0, 1, 2,  2, 3, 0 }, // front
		{ 4, 5, 6,  6, 7, 4 }, // back
		{ 4, 0, 3,  3, 7, 4 }, // left
		{ 5, 1, 2,  2, 6, 5 }, // right
		{ 0, 1, 5,  5, 4, 0 }, // top
		{ 3, 2, 6,  6, 7, 3 }, // bottom
	};

	// a mesma UV em todas as faces
	static final float[][] UV_FACE = {
		{ 0f, 0f }, { 1f, 0f }, { 1f, 1f },
		{ 1f, 1f }, { 0f, 1f }, { 0f, 0f },
	};

	@Override
	public void load() {
		FloatBuffer vertex_data = BufferUtils.createFloatBuffer(vertices * vertex_size);
		FloatBuffer color_data = BufferUtils.createFloatBuffer(vertices * color_size);
		FloatBuffer texture_data = BufferUtils.createFloatBuffer(vertices * texture_size);

		for (int[] face : FACES) {
			for (int i = 0; i < face.length; i++) {
				vertex_data.put(CANTOS[face[i]]);
				color_data.put(CORES[face[i]]);
				texture_data.put(UV_FACE[i]);
			}
		}
		vertex_data.flip();
		color_data.flip();
		texture_data.flip();

		vbo_vertex_handle = criaVbo(vertex_data);
		vbo_color_handle = criaVbo(color_data);
		vbo_texture_handle = criaVbo(texture_data);
	}

	static int criaVbo(FloatBuffer data) {
		int handle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return handle;
	}

	@Override
	public void draw() {
		glBindBuffer(GL_ARRAY_BUFFER, vbo_vertex_handle);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0,vertex_size,GL_FLOAT,false,0,0);

		glBindBuffer(GL_ARRAY_BUFFER, vbo_color_handle);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1,color_size,GL_FLOAT,false,0,0);

		glBindBuffer(GL_ARRAY_BUFFER, vbo_texture_handle);
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(3,texture_size,GL_FLOAT,false,0,0);

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_COLOR_ARRAY);

		glDrawArrays(GL_TRIANGLES, 0, vertices);

		glDisableClientState(GL_COLOR_ARRAY);
		glDisableClientState(GL_VERTEX_ARRAY);
	}
}

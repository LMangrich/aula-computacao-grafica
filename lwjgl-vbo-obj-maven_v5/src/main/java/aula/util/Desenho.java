package aula.util;

import aula.md2.Md2Model;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

// tudo que é do modelo na tela: trocar a animação pelas teclas e desenhar o banana
public class Desenho {

	// a posição na lista é a tecla: posição 0 = tecla 1
	static final String[] ANIMACOES = { "stand", "run", "attack", "pain", "jump", "flip", "salute", "taunt", "wave" };

	// olha as teclas 1 a 9 e troca a animação pela tecla apertada
	public static void trocaAnimacaoPelaTecla(long window, Md2Model mesh) {
		for (int i = 0; i < ANIMACOES.length; i++) { // testa as teclas 1 a 9
			if (glfwGetKey(window, GLFW_KEY_1 + i) == GLFW_PRESS) {
				mesh.setAnimation(ANIMACOES[i]); // troca a animação pela tecla apertada
				break; // se duas teclas estão apertadas, vale só a primeira
			}
		}
	}

	// posiciona o banana na cena, liga a textura e desenha o modelo
	public static void desenhaModelo(long window, Md2Model mesh, int textureId) {
		if (glfwGetKey(window, GLFW_KEY_S) != GLFW_PRESS) {
			glRotatef(20, 1, 0, 0); // inclina 20 graus; segurar S tira a inclinação
		}

		glPolygonMode(GL_FRONT_AND_BACK, glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS ? GL_LINE : GL_FILL);

		glEnable (GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, textureId);

		glTranslatef(0, -1, 0);
		glRotatef(-90, 0, 1, 0); // vira o banana para ficar de frente para a câmera
		glRotatef(-90, 1, 0, 0); // modelo do Quake tem o Z para cima; gira para o Y ficar para cima
		glScaled(0.05f,0.05f,0.05f);
		mesh.draw();
	}
}

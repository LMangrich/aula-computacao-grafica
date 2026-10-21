package aula.util;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// tudo que é da janela e da câmera: criar a janela e ajustar a visão a cada frame
public class Janela {

	// cria a janela, liga o OpenGL nela e devolve o id da janela
	public static long criaJanela() {
		if (!glfwInit()) //lib que inicia a janela, se nao iniciar, lança uma exceção
			throw new IllegalStateException("GLFW nao iniciou");

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2); //mantém pq deixa a mesma versão em todos os computadores, evitando incompatibilidade
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		long window = glfwCreateWindow(1024, 768, "Trabalho M2", 0, 0);

		if (window == NULL) //ajustado pq acho só == 0 mt feio
			throw new IllegalStateException("Falha ao criar janela");

		glfwMakeContextCurrent(window); //contexto do opengl é onde mantém todas as configs, texturas,buffer etc
		//dessa forma, o contexto da janela criada é o atual, e todas as operações do opengl vão ser feitas nessa janela
		glfwSwapInterval(1); // a janela tem duas imagens, uma na tela e outra que vc desenha no background
		// o 1 significa que a troca de imagens vai ser sincronizada com a taxa de atualização do monitor (usualmente 60/s), evitando desgaste do processador
		GL.createCapabilities(); //encontra onde q tá o driver gráfico no pc
		glEnable(GL_DEPTH_TEST); //habilita o teste de profundidade, então o openGL lembra da distância de cada pixel
		//glActiveTexture(GL_TEXTURE0); -- comentei pq o 0 já vem por default e só vai ser usado um slot de textura

		glClearColor(.5f, .5f, .5f, 1); //cor de fundo da janela, cinza
		return window;
	}

	// ajusta a câmera ao tamanho da janela e afasta a cena 4 unidades para frente da câmera
	public static void configuraCamera(int largura, int altura) {
		glViewport(0, 0, largura, altura);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		double aspect = (double) largura / altura;
		glFrustum(-aspect * .7, aspect * .7, -.7, .7, 1, 100);
		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();
		glTranslatef(0, 0, -4);
	}
}

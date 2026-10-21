package aula;

import aula.md2.Md2Model;
import aula.util.Desenho;
import aula.util.Janela;
import aula.util.TextureLoader;

import java.io.IOException;
import java.nio.file.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {

	public static void main(String[] args) throws Exception {
		Path path = Paths.get(args.length > 0 ? args[0] : "src/main/resources/TRIS.MD2"); //pega o arquivo para carregar ao passar um argumento, sem argumento = pega o MD2 como default

		long window = Janela.criaJanela();
		try {
			Md2Model mesh = new Md2Model(path.toString()); //gera o modelo md2
			mesh.setAnimation("run");
			int textureId = carregaTextura("src/main/resources/Bip.pcx");

			double start = glfwGetTime();
			while (!glfwWindowShouldClose(window)) {

				if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
					glfwSetWindowShouldClose(window, true);
				int[] w = new int[1], h = new int[1];
				glfwGetFramebufferSize(window, w, h);
				if (h[0] == 0) {
					glfwPollEvents();
					continue;
				}

				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				Janela.configuraCamera(w[0], h[0]);
				Desenho.trocaAnimacaoPelaTecla(window, mesh);
				mesh.animate(glfwGetTime() - start); // atualiza o frame pelo tempo passado
				Desenho.desenhaModelo(window, mesh, textureId);

				glfwSwapBuffers(window);
				glfwPollEvents();
			}
		} finally {
			glfwDestroyWindow(window);
			glfwTerminate();
		}
	}

	static int carregaTextura(String arquivo) throws IOException {
		return TextureLoader.loadTexture(TextureLoader.loadPcx(arquivo));
	}
}

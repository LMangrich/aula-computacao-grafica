package aula;

import org.lwjgl.opengl.GL;

import aula.obj.ObjModel;
import aula.util.TextureLoader;

import java.io.*;
import java.nio.file.*;

import javax.imageio.ImageIO;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL13.*;

public class Main {

	public static void main(String[] args) throws Exception {
		
		Path path = Paths.get(args.length > 0 ? args[0] : "../objs/x-35_obj.obj");
		if (!glfwInit())
			throw new IllegalStateException("GLFW nao iniciou");
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
		long window = glfwCreateWindow(1024, 768, "Aula VBO - OBJ (sem shaders)", 0, 0);
		if (window == 0)
			throw new IllegalStateException("Falha ao criar janela");
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE0);
		
		glClearColor(.5f, .5f, .5f, 1);
		try {
			ObjModel mesh = new ObjModel(path.toString());
			
			System.out.println("mesh "+mesh.drawboundingbox());
			
			int textureId = carregaTextura("src/main/resources/saida_textura_03___Default.png");
			mesh.setMaterialTexture("03___Default",textureId);
			mesh.setMaterialTexture("glass",carregaTextura("src/main/resources/saida_textura_glass.png"));
			
			
			System.out.println("Vertices renderizados: " + mesh.f.size() + " | OBJ: " + path);
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
				
				
				glViewport(0, 0, w[0], h[0]);
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				double aspect = (double) w[0] / h[0];
				glFrustum(-aspect * .7, aspect * .7, -.7, .7, 1, 100);
				glMatrixMode(GL_MODELVIEW);
				
				glLoadIdentity();
				glTranslatef(0, 0, -4);
				glRotatef((float) ((glfwGetTime() - start) * 25), 0, 1, 0);
				
				if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
					glRotatef(0, 1, 0, 0);
				}else {
					glRotatef(20, 1, 0, 0);
				}
				
				glPolygonMode(GL_FRONT_AND_BACK, glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS ? GL_LINE : GL_FILL);
				
				glColor3f(1, 1, 1);
				glEnable(GL_COLOR_MATERIAL);
				glEnable (GL_TEXTURE_2D);
				glBindTexture(GL_TEXTURE_2D, textureId);
				
				glTranslatef(0, -1, 0);
				glScaled(0.05f,0.05f,0.05f);
				mesh.draw();
				
				glfwSwapBuffers(window);
				glfwPollEvents();
			}
		} finally {
			glfwDestroyWindow(window);
			glfwTerminate();
		}
	}
	
	static int carregaTextura(String arquivo) throws IOException {
		return TextureLoader.loadTexture(ImageIO.read(new File(arquivo)));
	}
}

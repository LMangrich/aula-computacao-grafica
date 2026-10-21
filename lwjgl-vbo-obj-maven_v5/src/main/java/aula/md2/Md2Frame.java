package aula.md2;

import aula.obj.Vector3f;

// um quadro (frame) da animação: o nome e a posição de cada vértice naquele instante
public class Md2Frame {
	String name; // nome do frame ("stand01", "run1"...)
	Vector3f[] vertices; // vertices[i] = posição do vértice i neste frame (como a lista v do ObjModel)

	public Md2Frame(String name, Vector3f[] vertices) {
		this.name = name;
		this.vertices = vertices;
	}
}

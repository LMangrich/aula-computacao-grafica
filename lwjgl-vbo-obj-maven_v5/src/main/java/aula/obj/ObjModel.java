package aula.obj;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import aula.Model.Model;

public class ObjModel extends Model {
	public ArrayList<Vector3f> v;
	public ArrayList<Vector3f> vn;
	public ArrayList<Vector2f> vt;
	public ArrayList<Face3D> f;
	public ArrayList<GrupoFaces> g;
	public ConcurrentHashMap<String, GrupoFaces> gname;

	public HashMap<String,Integer> materiais;
	public ArrayList<MaterialPos> listaDePosicoesDeMaterial;


	int vbo_vertex_handle;
	int vbo_normal_handle;
	int vbo_textcood_handle;
	int vertex_size = 3;
	int texture_size = 2;
	int nvertex = 0;

	public Vector3f bbox_min = null;
	public Vector3f bbox_max = null;

	// ordem dos cantos de uma face ao virar triangulos: o quad 0 1 2 3 vira 0 1 2 + 2 3 0
	static final int[] CANTOS_TRIANGULO = { 0, 1, 2 };
	static final int[] CANTOS_QUAD = { 0, 1, 2, 2, 3, 0 };
	static final int[] SEM_CANTOS = {};

	public ObjModel() {
		v = new ArrayList<Vector3f>();
		vn = new ArrayList<Vector3f>();
		vt = new ArrayList<Vector2f>();
		f = new ArrayList<Face3D>();
		g = new ArrayList<GrupoFaces>();
		gname = new ConcurrentHashMap<String, GrupoFaces>();
		materiais = new HashMap<String,Integer>();
		listaDePosicoesDeMaterial = new ArrayList<MaterialPos>();
	}

	public ObjModel(String file) {
		this();
		loadObj(file);
		load();
	}

	public void loadObj(String file) {
		InputStream in;
		try {
			in = new FileInputStream(file);

			BufferedReader dados = new BufferedReader(new InputStreamReader(in));

			String str;

			try {

				while ((str = dados.readLine()) != null) {
					if (str.length() > 0) {
						if (str.contains("#")) {
							continue;
						}
						if (str.contains("v ")) {
							decodeVertice(str);
						}
						if (str.contains("vn ")) {
							decodeVerticeNormal(str);
						}
						if (str.contains("vt ")) {
							decodeTextureMapping(str);
						}
						if (str.contains("f ")) {
							decodeFace(str);
						}
						if (str.contains("g ")) {
							decodeGrupo(str);
						}
						if (str.contains("usemtl ")) {
							decodeMaterial(str);
						}
					}
				}

				if (g.size() > 0) {
					g.get(g.size() - 1).ffinal = f.size() - 1;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	// Le os primeiros "max" numeros da linha, pulando o que nao e numero (ex.: o "v" do inicio).
	// Valores que faltarem ficam 0.
	static float[] lerNumeros(String str, int max) {
		float numeros[] = new float[max];
		int index = 0;
		for (String s : str.split(" ")) {
			if (index == max) {
				break;
			}
			try {
				numeros[index] = Float.parseFloat(s);
				index++;
			} catch (Exception e) {
			}
		}
		return numeros;
	}

	public void decodeVertice(String str) {
		float n[] = lerNumeros(str, 3);
		Vector3f vec = new Vector3f(n[0], n[1], n[2]);

		if(bbox_min==null) {
			bbox_min = new Vector3f(vec);
			bbox_max = new Vector3f(vec);
		}else {
			bbox_min.x = Math.min(bbox_min.x,vec.x);
			bbox_min.y = Math.min(bbox_min.y,vec.y);
			bbox_min.z = Math.min(bbox_min.z,vec.z);

			bbox_max.x = Math.max(bbox_max.x,vec.x);
			bbox_max.y = Math.max(bbox_max.y,vec.y);
			bbox_max.z = Math.max(bbox_max.z,vec.z);
		}
		v.add(vec);
	}

	public void decodeVerticeNormal(String str) {
		float n[] = lerNumeros(str, 3);
		vn.add(new Vector3f(n[0], n[1], n[2]));
	}

	public void decodeTextureMapping(String str) {
		float n[] = lerNumeros(str, 2);
		Vector2f vec = new Vector2f();
		// x recebe o 2o numero e y o 1o (u e v trocados)
		vec.x = n[1];
		vec.y = n[0];
		vt.add(vec);
	}

	int valorestmp[] = new int[3];

	public void decodeFace(String str) {
		String s[] = str.split(" ");

		Face3D face = new Face3D();

		int index = 0;

		for (int i = 0; i < s.length; i++) {

			if (s[i].contains("/")) {
				String s2[] = s[i].split("/");

				valorestmp[0] = -1;
				valorestmp[1] = -1;
				valorestmp[2] = -1;

				for (int j = 0; j < s2.length; j++) {
					try {
						int numero = Integer.parseInt(s2[j]);
						valorestmp[j] = numero;
					} catch (Exception e) {
					}
				}
				if(index==4) {
					continue;
				}

				face.v[index] = valorestmp[0];
				face.n[index] = valorestmp[2];
				face.t[index] = valorestmp[1];

				index++;
			}

		}

		face.nvertices = (byte) index;
		f.add(face);
	}

	public void decodeGrupo(String str) {
		String sttmp[] = str.split(" ");
		String nome = "";
		if (sttmp.length >= 2) {
			nome = sttmp[1];
		}

		GrupoFaces gp = new GrupoFaces();
		gp.nome = nome;

		gp.finicial = f.size();

		if (g.size() > 0) {
			g.get(g.size() - 1).ffinal = f.size() - 1;
		}

		g.add(gp);
		gname.put(nome, gp);
		System.out.println("nome " + nome + "--");

	}

	public void decodeMaterial(String str) {
		String sttmp[] = str.split(" ");

		materiais.put(sttmp[1],0);
		listaDePosicoesDeMaterial.add(new MaterialPos(sttmp[1],f.size()));
	}

	// Cantos da face na ordem em que viram triangulos. Faces que nao sao triangulo nem quad sao ignoradas.
	static int[] cantos(Face3D face) {
		if (face.nvertices == 3) {
			return CANTOS_TRIANGULO;
		} else if (face.nvertices == 4) {
			return CANTOS_QUAD;
		}
		return SEM_CANTOS;
	}

	@Override
	public void load() {
		nvertex = 0;
		for (Face3D face : f) {
			nvertex += cantos(face).length;
		}

		FloatBuffer vertex_data = BufferUtils.createFloatBuffer(vertex_size*nvertex);
		FloatBuffer normal_data = BufferUtils.createFloatBuffer(vertex_size*nvertex);
		FloatBuffer textcoord_data = BufferUtils.createFloatBuffer(texture_size*nvertex);

		for (Face3D face : f) {
			for (int c : cantos(face)) {
				Vector3f p = v.get(face.v[c]-1);
				Vector3f n = vn.get(face.n[c]-1);
				Vector2f uv = vt.get(face.t[c]-1);

				vertex_data.put(p.x).put(p.y).put(p.z);
				normal_data.put(n.x).put(n.y).put(n.z);
				textcoord_data.put(uv.x).put(uv.y);
			}
		}

		vbo_vertex_handle = criaVbo(vertex_data);
		vbo_normal_handle = criaVbo(normal_data);
		vbo_textcood_handle = criaVbo(textcoord_data);

		salvaMapasDeUV();
	}

	static int criaVbo(FloatBuffer data) {
		data.flip();
		int handle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return handle;
	}

	// Desenha as arestas UV de cada face numa imagem por material e salva em saida_textura_<material>.png
	void salvaMapasDeUV() {
		HashMap<String,BufferedImage> mtImage = new HashMap<String, BufferedImage>();
		for (String material : materiais.keySet()) {
			System.out.println("material "+material);

			BufferedImage outimg = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
			Graphics2D dbg = (Graphics2D)outimg.getGraphics();
			dbg.setColor(Color.white);
			dbg.fillRect(0, 0, 1024, 1024);
			dbg.setColor(Color.black);

			mtImage.put(material, outimg);
		}

		int index = 0;
		for (int i = 0; i < f.size();i++) {
			Face3D face = f.get(i);

			// avanca para o proximo material quando a face i e a primeira dele
			if(index < listaDePosicoesDeMaterial.size()-1 && i >= listaDePosicoesDeMaterial.get(index+1).pos) {
				System.out.println("pos "+listaDePosicoesDeMaterial.get(index+1).pos);
				index++;
			}
			String material = listaDePosicoesDeMaterial.get(index).m;
			Graphics2D dbg = (Graphics2D)mtImage.get(material).getGraphics();
			System.out.println(""+i+" m "+material);

			// arestas do primeiro triangulo da face
			Vector2f uv1 = vt.get(face.t[0]-1);
			Vector2f uv2 = vt.get(face.t[1]-1);
			Vector2f uv3 = vt.get(face.t[2]-1);

			dbg.setColor(Color.black);
			dbg.drawLine((int)(uv1.x*1024), (int)(uv1.y*1024), (int)(uv2.x*1024), (int)(uv2.y*1024));
			dbg.drawLine((int)(uv2.x*1024), (int)(uv2.y*1024), (int)(uv3.x*1024), (int)(uv3.y*1024));
			dbg.drawLine((int)(uv1.x*1024), (int)(uv1.y*1024), (int)(uv3.x*1024), (int)(uv3.y*1024));
		}

		try {
			for (String mtname : mtImage.keySet()) {
				ImageIO.write(mtImage.get(mtname), "PNG", new File("saida_textura_"+mtname+".png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMaterialTexture(String name, int id) {
		if(materiais.containsKey(name)) {
			materiais.put(name, id);
		}else {
			System.out.println("Não existe um material com esse nome");
		}
	}

	// Desenha em modo imediato (glBegin/glEnd), sem VBO
	public void desenhaSe() {
		desenhaFaces(0, f.size());
	}

	@Override
	public void draw() {
		for(int i = 0; i < listaDePosicoesDeMaterial.size();i++) {
			MaterialPos mtpos = listaDePosicoesDeMaterial.get(i);
			int ini = mtpos.pos*3;
			int cont = nvertex-ini;
			if(i < listaDePosicoesDeMaterial.size()-1) {
				cont = (listaDePosicoesDeMaterial.get(i+1).pos*3)-ini;
			}

			glBindTexture(GL_TEXTURE_2D, materiais.get(mtpos.m));

			glEnableClientState(GL_VERTEX_ARRAY);
			glBindBuffer(GL_ARRAY_BUFFER, vbo_vertex_handle);
			glVertexPointer(3, GL_FLOAT, 0, 0L);
			glEnableClientState(GL_NORMAL_ARRAY);
			glBindBuffer(GL_ARRAY_BUFFER, vbo_normal_handle);
			glNormalPointer(GL_FLOAT, 0, 0L);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			glBindBuffer(GL_ARRAY_BUFFER, vbo_textcood_handle);
			glTexCoordPointer(2, GL_FLOAT, 0, 0L);
			glDrawArrays(GL_TRIANGLES, ini, cont);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glDisableClientState(GL_TEXTURE_COORD_ARRAY);
			glDisableClientState(GL_NORMAL_ARRAY);
			glDisableClientState(GL_VERTEX_ARRAY);
		}
	}


	public void desenhaseGrupo(String nome) {
		if (gname.containsKey(nome) == false) {
			System.out.println(" nao rolo");
			return;
		}

		GrupoFaces gp = gname.get(nome);
		desenhaFaces(gp.finicial, gp.ffinal);
	}

	// Desenha as faces [inicio, fim) em modo imediato
	void desenhaFaces(int inicio, int fim) {
		for (int i = inicio; i < fim; i++) {
			Face3D face = f.get(i);
			int[] cantos = cantos(face);
			if (cantos.length == 0) {
				continue;
			}

			for (int k = 0; k < face.nvertices; k++) {
				vn.get(face.n[k] - 1).Normalize();
			}

			glColor3f(1f, 1f, 1f);
			glBegin(GL_TRIANGLES);
			for (int c : cantos) {
				Vector2f uv = vt.get(face.t[c] - 1);
				Vector3f n = vn.get(face.n[c] - 1);
				Vector3f p = v.get(face.v[c] - 1);

				glTexCoord2f(uv.x, uv.y);
				glNormal3f(n.x, n.y, n.z);
				glVertex3f(p.x, p.y, p.z);
			}
			glEnd();
		}
	}

	public float[] boundingbox() {
		float bbox[] = {bbox_min.x,bbox_min.y,bbox_min.z,bbox_max.x-bbox_min.x,bbox_max.y-bbox_min.y,bbox_max.z-bbox_min.z};
		return bbox;
	}

	public String drawboundingbox() {
		float bbox[] = boundingbox();
		return "["+bbox[0]+","+bbox[1]+","+bbox[2]+","+bbox[3]+","+bbox[4]+","+bbox[5]+"]";
	}

}

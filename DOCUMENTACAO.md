# Documentação do projeto

Aplicação Java/Swing que desenha escrevendo pixels diretamente em um buffer de bytes. Implementa:

1. **Desenho de linhas** (Bresenham generalizado) sobre os pixels do buffer.
2. **Rotação 3D por um eixo definido por dois pontos** (Aula 4).
3. **Câmera e projeção em perspectiva** (Aula 5), com cena de objetos.

## Arquitetura

Cada classe responde a no máximo uma pergunta: *o que é desenhado?* (geometria), *como aparece na tela?* (pixels) ou *o que existe na cena e onde está?* (estado de cena).

```
GameCanvas  → Scene, Renderer, Matrix4
Scene       → SceneObject, Camera
SceneObject → Mesh, Matrix4
Camera      → Matrix4
Mesh        → (nada: dado puro)
Renderer    → Framebuffer
ObjLoader   → produz Mesh
```

Fluxo por quadro: `GameCanvas.run` → `Scene.update(dt)` (câmera lê teclado, objetos giram) → `GameCanvas.paint` calcula `mvp = projeção × view × modelo` por objeto, transforma os vértices do `Mesh`, faz a divisão homogênea e chama `Renderer.drawLine` para cada aresta.

---

## `src/app/`

### `MainClass.java`
| Função | Descrição |
|---|---|
| `main(String[] args)` | Cria o `GameCanvas` e a `JFrame` 640x480, registra o fechamento da janela e inicia o laço (`start()`). |

## `src/engine/`

### `GameCanvas.java`
Orquestrador: `JPanel` + `Runnable`. Não guarda estado de longo prazo (isso é da `Scene`).

| Função | Descrição |
|---|---|
| `GameCanvas()` | Define tamanho e foco, registra o `KeyboardInput`, cria a `Scene` com uma `Camera` em (0,0,600) e adiciona um cubo (escala 160) girando 90°/s em torno do eixo (-150,-100,-60)→(150,100,60). |
| `paint(Graphics g)` | Limpa o framebuffer; para cada objeto calcula `mvp`, projeta os vértices (`toScreen`), desenha as arestas em preto e o eixo de rotação em vermelho (`drawSegment`); copia o framebuffer para a tela. |
| `toScreen(double[] p)` | Divisão por `w` e mapeamento para a tela (origem no centro, y para cima). Retorna `null` se o ponto está atrás da câmera. |
| `drawSegment(a, b, r, g, bl)` | Arredonda e chama `Renderer.drawLine`; ignora segmentos com ponto `null`. |
| `start()` | Inicia a thread do laço. |
| `run()` | Laço: `scene.update(dt)`, `paintImmediately`, `sleep(1)`. |

## `src/render/`

### `Framebuffer.java`
Memória de vídeo: `BufferedImage` `TYPE_4BYTE_ABGR` + acesso direto ao `byte[]`.

| Membro / função | Descrição |
|---|---|
| `width`, `height`, `image`, `pixels` | Dimensões, imagem exibida e array de bytes (A,B,G,R por pixel). |
| `Framebuffer(width, height)` | Cria a imagem e obtém o array. |
| `clear()` | Preenche com branco opaco. |
| `setPixel(x, y, r, g, b)` | Escreve um pixel opaco em `y*width*4 + x*4`; ignora fora da tela. |

### `Renderer.java`
Rasterizador; não conhece cena, objeto nem câmera.

| Função | Descrição |
|---|---|
| `Renderer(Framebuffer target)` | Guarda o buffer de destino. |
| `drawLine(x1, y1, x2, y2, r, g, b)` | **Bresenham generalizado**, todos os octantes, só inteiros: `dx=\|x2-x1\|`, `dy=-\|y2-y1\|`, `err=dx+dy`; com `e2=2*err` avança em X se `e2>=dy` e em Y se `e2<=dx`. |

## `src/math/`

### `Matrix4.java`
Matriz 4x4 homogênea (vetor coluna, `P' = M·P`).

| Função | Descrição |
|---|---|
| `m` | `double[4][4]` com os valores. |
| `identity()` | Identidade. |
| `translation(tx, ty, tz)` | Translação 3D. |
| `scale(a, b, c)` | Escala 3D. |
| `rotationZ(theta)` | Rotação anti-horária em Z (slide 14, Aula 4). |
| `lookAt(eye, target, up)` | Mundo → coordenadas de visão (Aula 5): `zv=N=(eye-target)` normalizado, `xv=(V×N)` normalizado, `yv=zv×xv`; `M = R·T`. |
| `perspective(d)` | Projeção em perspectiva (slide 25, Aula 5) para câmera na origem olhando para -Z; após dividir por `w`: `xp = d·x/profundidade`. |
| `multiply(Matrix4 o)` | Produto `this · o`. |
| `transpose()` | Transposta (inversa de uma rotação ortonormal). |
| `transform(x, y, z)` | Aplica ao ponto `(x,y,z,1)` e devolve `{x',y',z'}`. |
| `transformHomogeneous(x, y, z)` | Igual, mas devolve `{x,y,z,w}` sem dividir por `w`. |
| `rotationAboutAxis(p1, p2, theta)` | **Rotação por eixo qualquer** (Aula 4): `T⁻¹·M⁻¹·R·M·T`. `W=(p2-p1)/\|p2-p1\|`, `V=(W×k)/\|W×k\|`, `U=V×W`; se o eixo é paralelo a Z usa X para achar V. |
| `sub`, `cross`, `length`, `normalize` *(privadas)* | Utilitários de vetor 3D. |

## `src/geometry/`

### `Mesh.java`
Geometria pura em coordenadas locais; não sabe onde está no mundo nem como é desenhada.

| Membro / função | Descrição |
|---|---|
| `vertices` | `double[][]` com os vértices locais. |
| `edges` | `int[][]` com pares de índices de vértices. |
| `Mesh(vertices, edges)` | Construtor. |
| `cube()` | Cubo unitário centrado na origem (lado 1). |

### `ObjLoader.java`
Função pura, sem estado após carregar.

| Função | Descrição |
|---|---|
| `load(String path)` | Lê as linhas `v` e `f` de um `.obj` e devolve um `Mesh` wireframe (arestas das faces, sem duplicatas). Lança `IOException`. |
| `index(token, vertexCount)` *(privada)* | Interpreta `v`, `v/vt`, `v/vt/vn`; trata índices base 1 e negativos. |

## `src/scene/`

### `SceneObject.java`
Liga um `Mesh` a uma transformação; só sabe calcular a própria matriz modelo.

| Membro / função | Descrição |
|---|---|
| `mesh`, `position`, `scale` | Geometria, posição e escala uniforme. |
| `axisP1`, `axisP2`, `angularSpeed` | Eixo de rotação (coordenadas de mundo) e velocidade (rad/s). |
| `update(diftime)` | Acumula o ângulo: `angularSpeed · dt`. |
| `modelMatrix()` | `RotaçãoPorEixo · Translação · Escala`. |

### `Camera.java`
Observador; não sabe quais objetos existem.

| Membro / função | Descrição |
|---|---|
| `eye`, `target` | Posição da câmera e ponto observado (`up` fixo em (0,1,0)). |
| `viewMatrix()` | `Matrix4.lookAt(eye, target, up)`. |
| `update(diftime, KeyboardInput k)` | W/S frente/trás, A/D lateral, Q/E baixo/cima (eye e target juntos); ←/→ orbita o eye em torno do target usando `rotationAboutAxis`. |
| `add`, `sub`, `cross`, `normalize` *(privadas)* | Utilitários de vetor. |

### `Scene.java`
Lista de objetos + câmera. Não guarda geometria, não desenha.

| Membro / função | Descrição |
|---|---|
| `objects`, `camera` | Objetos da cena e câmera. |
| `Scene(Camera, KeyboardInput)` | Construtor. |
| `add(SceneObject o)` | Adiciona um objeto. |
| `update(diftime)` | Atualiza a câmera e delega `update` a cada objeto. |

## `src/input/`

### `KeyboardInput.java`
Reduz eventos AWT a booleans consultáveis: `w a s d q e left right`.

| Função | Descrição |
|---|---|
| `keyPressed`, `keyReleased` | Marcam/desmarcam a tecla via `set`. |
| `keyTyped` | Vazio. |
| `set(code, v)` *(privada)* | Mapeia o código da tecla para o campo correspondente. |

---

## Controles

| Tecla | Ação |
|---|---|
| `W` `S` | Câmera para frente / trás |
| `A` `D` | Câmera para esquerda / direita |
| `Q` `E` | Câmera para baixo / cima |
| `←` `→` | Orbita em torno do alvo |

## Compilar e executar

```bash
javac -d bin $(find src -name '*.java') && java -cp bin app.MainClass
```

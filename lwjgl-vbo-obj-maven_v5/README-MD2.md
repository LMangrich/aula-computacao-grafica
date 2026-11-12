# Visualizador de animações MD2 (LWJGL)

Trabalho de Computação Gráfica (UNIVALI). O programa carrega um modelo de animação por keyframe **MD2** do Quake II, aplica a textura (skin), **interpola** entre os keyframes e **executa as animações** em uma janela.

O código foi feito sobre o código base do professor, `lwjgl-vbo-obj-maven_v5` (LWJGL 3, OpenGL 2.1, sem shaders), mantendo a estrutura dele: uma classe que estende `Model` com `load()` e `draw()`, e o reaproveitamento de `Face3D`, `Vector2f`, `Vector3f` e `TextureLoader`.

Itens do trabalho e onde cada um está:

| Pedido do trabalho | Onde está |
|---|---|
| Carregar arquivos MD2 de animação por keyframe | `aula/md2/Md2Model`, `Md2Header`, `Md2Loader`, `Md2Frame` |
| Interpolar animações | `Md2Animation` (calcula a mistura) e `Md2Model.drawInterpolated` (aplica) |
| Texturizar | `aula/util/TextureLoader` (`loadPcx` + `loadTexture`), `Md2Loader.decodeTexCoords` |
| Executar animações | `Md2Animation`, `Desenho.trocaAnimacaoPelaTecla`, o loop do `Main` |

---

## Requisitos

- **JDK 17 ou mais novo** (o projeto é compilado como Java 17).
- **Maven 3.9+** para rodar pela linha de comando, ou uma IDE que abra projetos Maven (VS Code com a extensão de Java, IntelliJ, Eclipse).
- Uma sessão de desktop com driver gráfico que suporte **OpenGL 2.1** (perfil de compatibilidade).
- Internet na primeira execução, para o Maven baixar o LWJGL.

As bibliotecas nativas do LWJGL são escolhidas pelo `pom.xml` automaticamente para Windows (x64 e ARM64), Linux (x64 e ARM64) e macOS (Intel e Apple Silicon).

## Como executar

Todos os comandos rodam **dentro da pasta do projeto** (a que contém o `pom.xml`, `lwjgl-vbo-obj-maven_v5`). O programa lê os arquivos por caminhos relativos como `src/main/resources/TRIS.MD2`, então rodar de outra pasta dá erro de "arquivo não encontrado".

### Linux, macOS e Windows (linha de comando)

```bash
mvn compile exec:java
```

Para carregar outro arquivo MD2:

```bash
mvn compile exec:java -Dexec.args="caminho/para/modelo.md2"
```

Obs.: o arquivo da skin (`Bip.pcx`) está escrito no `Main.java`. Se carregar outro modelo, troque esse caminho também.

Instalando o Maven, se o comando `mvn` não for encontrado:

- **Linux (Debian/Ubuntu):** `sudo apt install maven` (confira com `mvn -version`: precisa ser 3.9 ou mais novo, senão instale pelo site maven.apache.org).
- **macOS:** `brew install maven`
- **Windows:** baixe pelo site maven.apache.org, descompacte e adicione a pasta `bin` dele à variável `PATH`. Abra um terminal novo depois.

### Pela IDE (qualquer sistema)

1. Abra a pasta **interna** (a que tem o `pom.xml`), não a pasta de fora. Abrir a de fora quebra os caminhos relativos.
2. Espere o Maven terminar de importar.
3. Execute `aula.Main`. O diretório de trabalho precisa ser a pasta do projeto.

### Observação para macOS

O GLFW no macOS pode exigir a opção de JVM `-XstartOnFirstThread`. Se a janela não abrir, execute `aula.Main` pela IDE com essa opção nos argumentos da JVM. (O `mvn exec:java` não cria outra JVM, então a opção não pode ser aplicada por esse caminho.)

### Testado em

Windows, rodando pelo VS Code com Java 21. As instruções de Linux e macOS seguem o `pom.xml` e o README do professor, mas não foram testadas.

---

## Controles

| Tecla | Ação |
|---|---|
| `1` | stand |
| `2` | run |
| `3` | attack |
| `4` | pain |
| `5` | jump |
| `6` | flip |
| `7` | salute |
| `8` | taunt |
| `9` | wave |
| `W` (segurar) | wireframe (só as linhas) |
| `S` (segurar) | tira a inclinação de 20 graus |
| `ESC` | fecha a janela |

Se duas teclas de número estiverem apertadas, vale a de número menor. Apertar a tecla da animação que já está tocando não faz nada. Toda animação nova começa pelo primeiro frame. O programa começa tocando `run`.

---

## Estrutura do projeto

```
lwjgl-vbo-obj-maven_v5/
├── pom.xml                      build do Maven: LWJGL 3.3.6, Java 17, nativos por sistema
├── README.md                    README original do professor
├── README-MD2.md                este arquivo
└── src/main/
    ├── java/aula/
    │   ├── Main.java            ponto de entrada: abre a janela e roda o loop
    │   ├── md2/
    │   │   ├── Md2Model.java        o modelo: guarda os dados e desenha
    │   │   ├── Md2Header.java       os 17 números do header do arquivo
    │   │   ├── Md2Loader.java       lê texcoords, triângulos e frames
    │   │   ├── Md2Frame.java        um keyframe: nome e vértices
    │   │   └── Md2Animation.java    qual animação toca, qual frame, fator de mistura
    │   ├── util/
    │   │   ├── Janela.java          criação da janela, configuração do OpenGL, câmera
    │   │   ├── Desenho.java         teclas de animação e desenho do modelo
    │   │   └── TextureLoader.java   decodificador de PCX e envio da textura ao OpenGL
    │   ├── Model/
    │   │   ├── Model.java           classe abstrata do professor (load, draw)
    │   │   └── VboCube.java         cubo de demonstração do professor (não usado aqui)
    │   └── obj/                 código base de OBJ do professor
    │       ├── Face3D.java          índices de uma face / triângulo (reaproveitada no MD2)
    │       ├── Vector2f.java        vetor 2D (coordenadas de textura)
    │       ├── Vector3f.java        vetor 3D (posições dos vértices)
    │       └── ObjModel.java ...    carregador de OBJ e auxiliares (não usados pelo programa MD2)
    └── resources/
        ├── TRIS.MD2             o modelo (banana)
        ├── Bip.pcx              a skin dele (320x200, PCX)
        ├── WEAPON.MD2, weapon.pcx   a arma do modelo (não é carregada)
        ├── skinmesh.pcx, wepskinmesh.pcx   malhas de UV para pintar skins (não usadas)
        ├── BIP_I.PCX, pic.jpg   ícone de menu e imagem de prévia (não usados)
        └── Banana.txt           readme do autor do modelo (créditos)
```

Os arquivos `saida_textura_*.png` na raiz do projeto foram gerados pelo carregador de OBJ do professor em versões anteriores. O programa MD2 não usa esses arquivos.

---

## O que cada classe faz

### `Main`
Cria a janela (`Janela.criaJanela()`), carrega o modelo e a skin, e roda o loop. A cada volta: confere o ESC, lê o tamanho da janela, limpa a tela, configura a câmera, confere as teclas de animação, chama `mesh.animate(tempo)`, desenha o modelo, troca os buffers e processa os eventos. A janela e o GLFW são fechados em um bloco `finally`.

### `aula/md2`

**`Md2Model`** estende o `Model` do professor. Guarda o header, as coordenadas de textura (`ArrayList<Vector2f>`), os triângulos (`ArrayList<Face3D>`), os frames (`Md2Frame[]`) e um `Md2Animation`. O `loadMd2` lê o arquivo para um `ByteBuffer` e chama o loader. O `draw()` chama `drawInterpolated(currentFrame, nextFrame, blend)`. `setAnimation` e `animate` repassam para o `Md2Animation`.

**`Md2Header`** lê e guarda os 17 inteiros do começo do arquivo: tamanho da skin, tamanho do frame em bytes, as quantidades (skins, vértices, texcoords, triângulos, comandos GL, frames) e o byte onde cada seção começa. Confere o magic number (`844121161`, as letras `IDP2`) e a versão (`8`), e lança uma `IOException` se não baterem.

**`Md2Loader`** tem um método por seção: `decodeTexCoords`, `decodeTriangles` e `decodeFrames`. Cada um pula para a sua seção com `buf.position(offset)`, lê e devolve a lista.

**`Md2Frame`** é um keyframe: o nome (`stand01`, `run3`...) e as posições de todos os vértices dele.

**`Md2Animation`** só faz contas, sem OpenGL. Guarda o primeiro e o último frame da animação atual, a taxa de quadros (`fps = 10`), o instante em que a animação começou e três resultados atualizados por `animate(tempo)`: `currentFrame`, `nextFrame` e `blend`.

### `aula/util`

**`TextureLoader`** é a classe do professor com um método novo, `loadPcx(arquivo)`, que decodifica um arquivo PCX para um `BufferedImage` (o Java não abre PCX sozinho). O `loadTexture(imagem)` não mudou: envia os pixels para o OpenGL e devolve o id da textura.

**`Janela`** cria a janela e configura o OpenGL (`criaJanela`), e define a viewport, a perspectiva e a posição da câmera (`configuraCamera`).

**`Desenho`** lê as teclas 1 a 9 (`trocaAnimacaoPelaTecla`) e posiciona e desenha o modelo (`desenhaModelo`).

### Reaproveitado do código base do professor

`Model`, `Face3D`, `Vector2f`, `Vector3f` e `TextureLoader` são usados. Os campos de `Face3D` foram tornados `public` para o pacote `aula.md2` conseguir lê-los.

---

## Como funciona

### O formato MD2

MD2 é o formato de modelo do Quake II (id Software). É um arquivo **binário**, guardado em **little-endian**. Cada frame guarda a posição de **todos os vértices**, então o modelo é animado trocando as posições de um frame para o outro. Não existem ossos.

```
[ header: 68 bytes ][ skins ][ texcoords ][ triângulos ][ frames ][ comandos GL ]
```

| Seção | Entrada | Conteúdo |
|---|---|---|
| Header | 17 ints | magic `IDP2`, versão 8, tamanho da skin, tamanho do frame, quantidades, offsets |
| Skins | 64 bytes | nomes dos arquivos de skin (não usados: a skin é carregada pelo `Main`) |
| Texcoords | 2 shorts | `s`, `t` em **pixels** da skin |
| Triângulos | 6 shorts | 3 índices de vértice + 3 índices de texcoord |
| Frames | `frameSize` bytes | `scale` (3 floats), `translate` (3 floats), nome (16 bytes), e por vértice 4 bytes: x, y, z, índice da normal |
| Comandos GL | ints | tiras e leques opcionais (ignorados) |

Cada coordenada de vértice é guardada em um byte (0 a 255). A posição real de um vértice é:

```
posição = byte * scale + translate      (separadamente para x, y e z, com o scale e o translate do próprio frame)
```

Por que o formato exige cuidado para ler em Java:

- **Little-endian:** o `ByteBuffer` precisa de `.order(ByteOrder.LITTLE_ENDIAN)`, porque o padrão do Java é o contrário.
- **Sem tipos sem sinal:** os bytes dos vértices são lidos com `& 0xFF` e os shorts com `& 0xFFFF`.
- **Índices separados:** um triângulo guarda índices de vértice e de texcoord separadamente, então o mesmo vértice pode ter coordenadas de textura diferentes em triângulos diferentes. O modelo é desenhado por canto de triângulo.
- **Texcoords em pixels:** são divididas pela largura e pela altura da skin para dar valores de 0 a 1.
- **Sistema de coordenadas:** o Quake usa o Z como "para cima". O OpenGL aqui usa o Y, então o modelo é girado -90 graus em torno do X.

Valores do `TRIS.MD2` incluído: skin 320x200, 229 vértices por frame, 386 texcoords, 392 triângulos, 200 frames de 956 bytes cada.

### Carregamento

1. `Md2Model.loadMd2` lê o arquivo inteiro para um `ByteBuffer`.
2. `Md2Header.read` lê os 17 números e confere o magic e a versão.
3. `Md2Loader` lê as texcoords, os triângulos e os 200 frames (decodificando as posições dos vértices com scale e translate).
4. O console imprime os principais valores (quantidades, offsets, primeiras e últimas entradas) para conferência.

### Animações

Os nomes dos frames marcam onde cada animação começa. `setAnimation("run")` tira os números do fim de cada nome de frame (`run1`... `run6` viram `run`) e pega o primeiro e o último frame com esse nome.

| Animação | Frames | Animação | Frames |
|---|---|---|---|
| `stand` | 0-39 | `point` | 123-134 |
| `run` | 40-45 | `crstnd` | 135-153 |
| `attack` | 46-53 | `crwalk` | 154-159 |
| `pain` | 54-65 | `crattak` | 160-168 |
| `jump` | 66-71 | `crpain` | 169-172 |
| `flip` | 72-83 | `crdeath` | 173-177 |
| `salute` | 84-94 | `death` | 178-197 |
| `taunt` | 95-111 | | |
| `wave` | 112-122 | | |

Os frames 198 e 199 se chamam `base` e não são uma animação. Só os nove primeiros nomes estão ligados a teclas. Os outros podem ser usados com `setAnimation` no código.

### Interpolação

O arquivo tem poucos keyframes por animação (`run` tem 6) a 10 quadros por segundo, mas a tela desenha cerca de 60 imagens por segundo. Sem interpolação, o modelo segura uma pose por várias imagens da tela e depois pula. Com interpolação, cada imagem mostra uma pose entre dois keyframes vizinhos.

`Md2Animation.animate(tempo)` calcula:

```
passed       = (tempo - animStartTime) * fps      ex.: 23.4
whole        = (int) passed                       23
currentFrame = startFrame + whole % count         frame A
nextFrame    = startFrame + (whole + 1) % count   frame B (volta ao primeiro frame no fim)
blend        = passed - whole                     0.4  (este é o t)
```

Depois o `drawInterpolated` mistura cada vértice entre o frame A e o frame B:

```
posição = A + t * (B - A)       para x, y e z
```

Com `t = 0` a pose é exatamente o frame A, e com `t = 1` é exatamente o frame B. Só as posições são misturadas. Os triângulos e as coordenadas de textura são iguais em todos os frames.

### Textura

1. `TextureLoader.loadPcx` decodifica a skin PCX: lê o header de 128 bytes, descomprime os pixels (run-length: um byte de `0xC0` ou mais é uma contagem de repetição, e o byte seguinte é o valor), lê a paleta de 256 cores no fim do arquivo e pinta um `BufferedImage`.
2. `TextureLoader.loadTexture` (método do professor) envia essa imagem ao OpenGL e devolve um id de textura.
3. No `drawInterpolated`, cada canto de cada triângulo chama `glTexCoord2f(u, v)` e depois `glVertex3f(x, y, z)`. A ordem importa: o OpenGL associa a última coordenada de textura ao próximo vértice.

### Desenho

O modelo é desenhado em **modo imediato** (`glBegin(GL_TRIANGLES)` / `glEnd`), como o `ObjModel.desenhaFaces` do professor. As posições mudam a cada imagem da tela por causa da interpolação, então são enviadas toda vez. As transformações são aplicadas pelo OpenGL da última linha para a primeira:

1. `glTranslatef(0, 0, -4)`: afasta a cena da câmera (em `Janela.configuraCamera`)
2. `glRotatef(20, 1, 0, 0)`: inclina a visão (pulado enquanto `S` está apertada)
3. `glTranslatef(0, -1, 0)`: desce o modelo
4. `glRotatef(-90, 0, 1, 0)`: vira o modelo de frente para a câmera
5. `glRotatef(-90, 1, 0, 0)`: Z para cima do Quake para Y para cima do OpenGL
6. `glScaled(0.05, 0.05, 0.05)`: encolhe o modelo (ele tem cerca de 54 unidades do Quake de altura)

Não mude a ordem dessas linhas: a ordem decide onde o modelo aparece.

---

## Limitações

- **Modo imediato, sem VBOs.** Os dados do modelo não ficam em buffers na GPU. O `Md2Model.load()` está vazio.
- **Sem iluminação.** O índice da normal de cada vértice é lido e ignorado.
- **As animações ficam em loop.** `death` e `crdeath` tocam várias vezes e misturam da última pose de volta para a primeira, em vez de parar.
- **A interpolação é só entre frames vizinhos de uma animação,** não entre duas animações diferentes.
- **Uma skin só.** Os nomes de skin guardados dentro do MD2 são ignorados: o `Main` carrega o `Bip.pcx` diretamente.
- **O `WEAPON.MD2` não é carregado.** É um modelo separado (a arma) com a skin própria.
- **Os valores de orientação e escala** no `Desenho` foram escolhidos para este modelo. Outros arquivos MD2 podem precisar de outros números.
- **Comandos GL** (as listas opcionais de tiras e leques) são ignorados.

## Créditos

- **Modelo e skin:** Dan Ward (GaratJax), do clã de Quake "Bananas in Pyjamas" (veja o `Banana.txt`). O autor permite o uso para fins não comerciais, com crédito.
- **Código base:** `lwjgl-vbo-obj-maven_v5`, do repositório da disciplina.
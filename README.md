# CG — Software Renderer

Java/Swing exercise that draws by writing raw pixels into a framebuffer.

- **Lines:** `Renderer.drawLine` is a generalized Bresenham writing directly into the framebuffer bytes.
- **Rotation about an axis:** `Matrix4.rotationAboutAxis(p1, p2, theta)` = `T⁻¹·M⁻¹·R·M·T` (Aula 4).
- **Camera + perspective:** `Matrix4.lookAt` / `Matrix4.perspective` (Aula 5); `mvp = projection · view · model`.
- **Rasterization:** `Renderer.fillTriangle` fills every face (barycentric test + z-buffer) with flat shading.
- **Scene:** five .obj files from `src/assets/obj_1/` (tank, medieval house, MiG-29, bench, chair), read by `ObjLoader`, on a floor grid with X/Y/Z axes at the centre.

Controls (they move the camera): `W`/`S` forward/back, `A`/`D` strafe, `Q`/`E` down/up, `←`/`→` orbit the scene centre, `↑`/`↓` raise/lower the camera around it.

See [DOCUMENTACAO.md](DOCUMENTACAO.md) for the architecture and every class/function.

## Build and run

JDK 8+; run from the project root.

**Linux (Debian) / macOS:** run the script. It needs a full JDK with GUI support (`sudo apt install default-jdk`; the `-headless` package cannot open the window).

```bash
sh run.sh
```

Or by hand (also works in Git Bash):

```bash
javac -d bin $(find src -name '*.java') && java -cp bin app.MainClass
```

**Windows** (PowerShell 5.1 has no `&&`, and `find` is a different Windows tool): just run the script, or double-click it.

```powershell
.\run.bat
```

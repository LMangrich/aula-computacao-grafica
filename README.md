# CG — Software Renderer

Java/Swing exercise that draws by writing raw pixels into a framebuffer.

- **Lines:** `Renderer.drawLine` is a generalized Bresenham writing directly into the framebuffer bytes.
- **Rotation about an axis:** `Matrix4.rotationAboutAxis(p1, p2, theta)` = `T⁻¹·M⁻¹·R·M·T` (Aula 4).
- **Camera + perspective:** `Matrix4.lookAt` / `Matrix4.perspective` (Aula 5); `mvp = projection · view · model`.
- **Demo:** a wireframe cube spins about the red axis p1→p2; fly around with the keyboard.

Controls: `W`/`S` forward/back, `A`/`D` strafe, `Q`/`E` down/up, `←`/`→` orbit.

See [DOCUMENTACAO.md](DOCUMENTACAO.md) for the architecture and every class/function.

## Build and run

JDK 8+; run from the project root.

**Linux / macOS / Git Bash:**

```bash
javac -d bin $(find src -name '*.java') && java -cp bin app.MainClass
```

**Windows** (PowerShell 5.1 has no `&&`, and `find` is a different Windows tool): just run the script, or double-click it.

```powershell
.\run.bat
```

# CG — Software Renderer

Java/Swing exercise that draws by writing raw pixels into a framebuffer.

## What it does

- **Line drawing:** `Renderer.drawLine(x1, y1, x2, y2, r, g, b)` is a generalized
  Bresenham (all octants) that writes directly into the framebuffer bytes.
- **Rotation about an arbitrary axis:** `Matrix4.rotationAboutAxis(p1, p2, theta)`
  builds `P' = T⁻¹ · M⁻¹ · R · M · T · P` (Aula 4): translate p1 to the origin,
  rotate the axis onto Z using the orthonormal basis U, V, W, rotate by θ about Z,
  then undo both steps.
- **Demo:** a cube spins about the red axis p1→p2 (orthographic projection,
  edges drawn with `drawLine`).

## Layout

| Folder         | Classes                      |
|----------------|------------------------------|
| `src/app/`     | `MainClass`                  |
| `src/engine/`  | `GameCanvas`                 |
| `src/render/`  | `Framebuffer`, `Renderer`    |
| `src/math/`    | `Matrix4`                    |
| `src/world/`   | `World`                      |

## Build and run

JDK 8+; run from the project root.

```bash
javac -d bin $(find src -name '*.java') && java -cp bin app.MainClass
```

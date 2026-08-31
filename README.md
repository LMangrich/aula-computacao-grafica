# CG_1 — Software Renderer

A small computer-graphics exercise: a Java/Swing application that draws by
manipulating raw pixels in a framebuffer instead of using the AWT drawing
primitives. It keeps a `TYPE_4BYTE_ABGR` image in memory, writes bytes directly
into its backing array, and blits the result to the window every frame.

## What it does

- Allocates an in-memory framebuffer (640x480, ABGR bytes).
- Loads `assets/fundo.jpg` and blits it into the framebuffer each frame,
  applying a per-channel colour filter followed by grayscale averaging.
- Moves the sprite with **W / A / S / D**.
- Animates a point that chases the mouse cursor.
- Randomises the colour filter once per second.
- Shows an FPS counter and the current mouse position in the HUD.
- Dumps the raw bytes of `assets/imgbmp.bmp` to stdout on startup (inspection).

## Project layout

Responsibilities are split into one package per concern under `src/`:

| Folder          | Responsibility                                              | Classes                  |
|-----------------|------------------------------------------------------------|--------------------------|
| `src/app/`      | Entry point: builds the window and starts the loop         | `MainClass`              |
| `src/engine/`   | Swing surface + game-loop thread, FPS, frame orchestration | `GameCanvas`             |
| `src/render/`   | Video memory (ABGR buffer) and the software rasterizer     | `Framebuffer`, `Renderer`|
| `src/input/`    | AWT listeners reduced to pollable state                    | `KeyboardInput`, `MouseInput` |
| `src/world/`    | Simulation state and the per-tick update                   | `World`                  |
| `src/resource/` | Image decoding and the BMP byte dump (disk I/O)            | `ImageLoader`            |
| `assets/`       | Image files loaded at runtime                              | `fundo.jpg`, `imgbmp.bmp`, ... |

## Requirements

- JDK 8 or newer (`javac` and `java` on your `PATH`).

## Build and run

Asset paths are resolved relative to the working directory, so run the commands
from the project root.

```bash
javac -d bin $(find src -name '*.java')
java -cp bin app.MainClass
```

Or as a single line:

```bash
javac -d bin $(find src -name '*.java') && java -cp bin app.MainClass
```

Compiled `.class` files land in `bin/` (git-ignored).

## Controls

| Key            | Action              |
|----------------|---------------------|
| `W` `A` `S` `D`| Move the sprite     |
| Mouse move     | Point chases cursor |
| Close window   | Quit                |

package scene;

import java.util.ArrayList;
import java.util.List;

import input.KeyboardInput;

/**
 * What exists and where: the list of objects plus the camera. Does not hold
 * geometry or transformed vertices and never draws.
 */
public class Scene {
	public final List<SceneObject> objects = new ArrayList<>();
	public final Camera camera;

	private final KeyboardInput keyboard;

	public Scene(Camera camera, KeyboardInput keyboard) {
		this.camera = camera;
		this.keyboard = keyboard;
	}

	public void add(SceneObject o) {
		objects.add(o);
	}

	public void update(long diftime) {
		camera.update(diftime, keyboard);
	}
}

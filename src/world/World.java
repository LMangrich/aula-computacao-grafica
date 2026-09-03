package world;

import java.util.Random;

import input.KeyboardInput;
import input.MouseInput;

/**
 * Holds every piece of simulation state (sprite position, colour filter, the
 * two tracked points) and advances it one tick at a time from the current
 * input snapshot.
 */
public class World {
	public float posx = 0;
	public float posy = 0;

	public float filtroR = 1;
	public float filtroG = 1;
	public float filtroB = 1;

	public float q1x = 10, q1y = 100;
	public float q2x = 10, q2y = 200;

	private int timer = 0;
	private final Random rand = new Random();
	private boolean settingFirstPoint = true;

	private final KeyboardInput keyboard;
	private final MouseInput mouse;

	public World(KeyboardInput keyboard, MouseInput mouse) {
		this.keyboard = keyboard;
		this.mouse = mouse;
	}

	public void update(long diftime) {
		float difS = diftime / 1000.0f;
		float vel = 50;

		timer += diftime;
		if (timer >= 1000) {
			timer = 0;
			filtroR = rand.nextFloat();
			filtroG = rand.nextFloat();
			filtroB = rand.nextFloat();
		}

		if (keyboard.up) {
			posy -= vel * difS;
		}
		if (keyboard.down) {
			posy += vel * difS;
		}
		if (keyboard.left) {
			posx -= vel * difS;
		}
		if (keyboard.right) {
			posx += vel * difS;
		}

		// First click sets (x1,y1), next click sets (x2,y2), then it loops back.
		if (mouse.consumeClick()) {
			if (settingFirstPoint) {
				q1x = mouse.clickX;
				q1y = mouse.clickY;
			} else {
				q2x = mouse.clickX;
				q2y = mouse.clickY;
			}
			settingFirstPoint = !settingFirstPoint;
		}
	}
}

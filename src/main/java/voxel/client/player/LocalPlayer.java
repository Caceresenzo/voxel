package voxel.client.player;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.DoubleBuffer;
import java.util.UUID;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;

import lombok.Getter;
import voxel.client.Game;
import voxel.client.Settings;
import voxel.client.render.Camera;

public class LocalPlayer extends Camera implements Player {

	private final UUID uuid;
	private final @Getter String login;
	private Vector2i lastPosition;

	public LocalPlayer(UUID uuid, String login) {
		super(Settings.PLAYER_POSITION, -90, 0);

		this.uuid = uuid;
		this.login = login;
	}
	
	public boolean handleMouvement() {
		final var moved = handleKeyboard();
		final var rotated = handleMouse();
		
		return moved || rotated;
	}

	@Override
	public void update() {
		handleKeyboard();
		handleMouse();
		super.update();
	}

	private boolean handleMouse() {
		var updated = false;
		
		Vector2i position;
		try (MemoryStack stack = stackPush()) {
			DoubleBuffer xpos = stack.mallocDouble(1);
			DoubleBuffer ypos = stack.mallocDouble(1);

			glfwGetCursorPos(Game.window, xpos, ypos);

			position = new Vector2i(
				(int) xpos.get(0),
				(int) ypos.get(0)
			);
		}

		if (lastPosition != null) {
			final var delta = position.sub(lastPosition, new Vector2i());
			if (delta.y != 0) {
				rotatePitch(delta.y * Settings.MOUSE_SENSITIVITY);
				updated = true;
			}

			if (delta.x != 0) {
				rotateYaw(delta.x * Settings.MOUSE_SENSITIVITY);
				updated = true;
			}
		}

		lastPosition = position;
		return updated;
	}

	private boolean handleKeyboard() {
		var updated = false;
		
		float delta = 2f;
		float velocity = Settings.PLAYER_SPEED * delta;

		if (glfwGetKey(Game.window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS || glfwGetKey(Game.window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS) {
			velocity *= 5;
			updated = true;
		}

		if (glfwGetKey(Game.window, GLFW_KEY_Z) == GLFW_PRESS || glfwGetKey(Game.window, GLFW_KEY_W) == GLFW_PRESS) {
			moveForward(velocity);
			updated = true;
		}

		if (glfwGetKey(Game.window, GLFW_KEY_S) == GLFW_PRESS) {
			moveBackward(velocity);
			updated = true;
		}

		if (glfwGetKey(Game.window, GLFW_KEY_Q) == GLFW_PRESS || glfwGetKey(Game.window, GLFW_KEY_A) == GLFW_PRESS) {
			moveLeft(velocity);
			updated = true;
		}

		if (glfwGetKey(Game.window, GLFW_KEY_D) == GLFW_PRESS) {
			moveRight(velocity);
			updated = true;
		}

		if (glfwGetKey(Game.window, GLFW_KEY_SPACE) == GLFW_PRESS) {
			moveUp(velocity);
			updated = true;
		}

		if (glfwGetKey(Game.window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS || glfwGetKey(Game.window, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS) {
			moveDown(velocity);
			updated = true;
		}
		
		return updated;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

}
package voxel;

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

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class Player extends Camera {
	
	private Vector2i lastPosition;
	
	public Player() {
		this(Settings.PLAYER_POSITION, -90, 0);
	}
	
	public Player(Vector3f position, float yaw, float pitch) {
		super(position, yaw, pitch);
	}
	
	@Override
	public void update() {
		handleKeyboard();
		handleMouse();
		super.update();
		
//		System.out.println("x=%3.2f y=%3.2f z=%3.2f yaw=%3.2f pitch=%3.2f".formatted(getPosition().x, getPosition().y, getPosition().z, getYaw(), getPitch()));
	}
	
	private void handleMouse() {
		Vector2i position;
		try (MemoryStack stack = stackPush()) {
			DoubleBuffer xpos = stack.mallocDouble(1);
			DoubleBuffer ypos = stack.mallocDouble(1);
			
			glfwGetCursorPos(Main.window, xpos, ypos);
			
			position = new Vector2i(
				(int) xpos.get(0),
				(int) ypos.get(0)
			);
		}
		
		if (lastPosition != null) {
			final var delta = position.sub(lastPosition, new Vector2i());
			if (delta.y != 0) {
				rotatePitch(delta.y * Settings.MOUSE_SENSITIVITY);
				// System.out.println("rotatePitch");
			}
			
			if (delta.x != 0) {
				rotateYaw(delta.x * Settings.MOUSE_SENSITIVITY);
				// System.out.println("rotateYaw");
			}
		}
		
		lastPosition = position;
	}
	
	private void handleKeyboard() {
		float delta = 2f;
		float velocity = Settings.PLAYER_SPEED * delta;
		
		if (glfwGetKey(Main.window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS || glfwGetKey(Main.window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS) {
			velocity *= 5;
		}
		
		if (glfwGetKey(Main.window, GLFW_KEY_Z) == GLFW_PRESS || glfwGetKey(Main.window, GLFW_KEY_W) == GLFW_PRESS) {
			moveForward(velocity);
			// System.out.println("moveForward");
		}
		
		if (glfwGetKey(Main.window, GLFW_KEY_S) == GLFW_PRESS) {
			moveBackward(velocity);
			// System.out.println("moveBackward");
		}
		
		if (glfwGetKey(Main.window, GLFW_KEY_Q) == GLFW_PRESS || glfwGetKey(Main.window, GLFW_KEY_A) == GLFW_PRESS) {
			moveLeft(velocity);
			// System.out.println("moveLeft");
		}
		
		if (glfwGetKey(Main.window, GLFW_KEY_D) == GLFW_PRESS) {
			moveRight(velocity);
			// System.out.println("moveRight");
		}
		
		if (glfwGetKey(Main.window, GLFW_KEY_SPACE) == GLFW_PRESS) {
			moveUp(velocity);
			// System.out.println("moveUp");
		}
		
		if (glfwGetKey(Main.window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS || glfwGetKey(Main.window, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS) {
			moveDown(velocity);
			// System.out.println("moveDown");
		}
	}
	
}
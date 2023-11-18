package voxel.client.graphics.opengl.util;

import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;

import java.lang.ref.Cleaner;
import java.lang.ref.Cleaner.Cleanable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import voxel.client.game.Game;

@UtilityClass
public class OpenGL {

	private static Cleaner CLEANER = Cleaner.create();
	private static final Map<Integer, String> ERRORS;

	static {
		final var errors = new HashMap<Integer, String>();
		errors.put(GL_INVALID_ENUM, "GL_INVALID_ENUM");
		errors.put(GL_INVALID_VALUE, "GL_INVALID_VALUE");
		errors.put(GL_INVALID_OPERATION, "GL_INVALID_OPERATION");
		errors.put(GL_INVALID_FRAMEBUFFER_OPERATION, "GL_INVALID_FRAMEBUFFER_OPERATION");
		errors.put(GL_OUT_OF_MEMORY, "GL_OUT_OF_MEMORY");
		errors.put(GL_STACK_UNDERFLOW, "GL_STACK_UNDERFLOW");
		errors.put(GL_STACK_OVERFLOW, "GL_STACK_OVERFLOW");

		ERRORS = Collections.unmodifiableMap(errors);
	}

	public static void checkErrors() {
		final var error = glGetError();
		if (error != GL_NO_ERROR) {
			final var string = ERRORS.get(error);
			System.out.println(error + ": " + string);
			throw new IllegalStateException(string);
		}
	}

	public Cleanable registerForGarbageCollect(Object object, Runnable action) {
//		System.out.println("registerForGarbageCollect: " + object + " -- " + action);
		return CLEANER.register(object, new MoveToDeleteActionQueue(action));
	}

	private record MoveToDeleteActionQueue(Runnable action) implements Runnable {

		@Override
		@SneakyThrows
		public void run() {
			Game.deleteActions.put(action);
		}

	}

}
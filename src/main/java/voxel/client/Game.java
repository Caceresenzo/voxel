package voxel.client;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import opengl.OpenGL;
import voxel.client.state.GameState;
import voxel.client.state.NullGameState;

public class Game {

	public static long window;

	private static GameState state = NullGameState.INSTANCE;
	private static GameState oldState;

	public static void run() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		initialize();
	
		try {
			loop();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		glfwTerminate();
		glfwSetErrorCallback(null).free();

		System.out.println("Goodbye");
	}

	private static void initialize() {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_DEPTH_BITS, 24);

		window = glfwCreateWindow(Settings.WINDOW_RESOLUTION.x, Settings.WINDOW_RESOLUTION.y, "Hello World!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true);
		});

		try (MemoryStack stack = stackPush()) {
			final var width = stack.mallocInt(1);
			final var height = stack.mallocInt(1);
			glfwGetWindowSize(window, width, height);

			final var videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(
				window,
				(videoMode.width() - width.get(0)) / 2,
				(videoMode.height() - height.get(0)) / 2
			);
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);

		glfwShowWindow(window);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}

	private static void loop() {
		GL.createCapabilities();
//		GLUtil.setupDebugMessageCallback(System.err);

		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_BLEND);
//		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		var previousTime = glfwGetTime();
		var frameCount = 0;

		while (!glfwWindowShouldClose(window)) {
			var currentTime = glfwGetTime();
			frameCount++;

			if (currentTime - previousTime >= 1.0) {
				glfwSetWindowTitle(window, "FPS: %d".formatted(frameCount));

				frameCount = 0;
				previousTime = currentTime;
			}

			OpenGL.processDeleteActions();

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			final var state = initializeStateIfNecessary();

			state.update();
			state.render();

			glfwSwapBuffers(window);
			glfwPollEvents();

			OpenGL.checkErrors();
		}
	}

	private static GameState initializeStateIfNecessary() {
		synchronized (Game.class) {
			if (oldState != null) {
				oldState.cleanup();
				oldState = null;

				state.initialize();
			}

			return state;
		}
	}

	public static void setState(GameState newState) {
		synchronized (Game.class) {
			oldState = state;
			state = newState;
		}
	}

}
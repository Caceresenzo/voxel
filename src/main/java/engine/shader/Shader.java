package engine.shader;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Cleaner.Cleanable;
import java.util.Objects;

import engine.util.OpenGL;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

public class Shader {

	private final @Getter int id;
	private final Cleanable cleanable;

	private Shader(int id) {
		this.id = id;
		this.cleanable = OpenGL.registerForGarbageCollect(this, new DeleteAction(id));
	}

	public void delete() {
		cleanable.clean();
	}

	public static Shader load(Type type, File file) throws IOException {
		try (final var inputStream = new FileInputStream(file)) {
			return load(type, inputStream);
		}
	}

	public static Shader load(Type type, InputStream inputStream) throws IOException {
		Objects.requireNonNull(inputStream, "inputStream == null");

		final var id = glCreateShader(type.value());

		try {
			final var code = new String(inputStream.readAllBytes());

			glShaderSource(id, code);
			glCompileShader(id);

			final var success = glGetShaderi(id, GL_COMPILE_STATUS) != 0;
			final var logs = glGetShaderInfoLog(id);

			if (!logs.isEmpty()) {
				System.err.println(logs);
			}

			if (!success) {
				throw new IllegalStateException("failed to compile shader: " + logs);
			} else if (!logs.isEmpty()) {
				System.err.println(logs);
			}

			return new Shader(id);
		} catch (Exception exception) {
			glDeleteShader(id);
			throw exception;
		}
	}

	@Getter(AccessLevel.PROTECTED)
	@Accessors(fluent = true)
	@RequiredArgsConstructor
	public enum Type {

		VERTEX(GL_VERTEX_SHADER),
		FRAGMENT(GL_FRAGMENT_SHADER);

		private final int value;

	}

	private record DeleteAction(int shaderId) implements Runnable {

		@Override
		public void run() {
			glDeleteShader(shaderId);
		}

	}

}
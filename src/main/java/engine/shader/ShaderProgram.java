package engine.shader;

import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.lang.ref.Cleaner.Cleanable;
import java.util.Arrays;
import java.util.Collection;

import engine.util.GarbageCollector;
import engine.util.OpenGL;
import lombok.Getter;

public class ShaderProgram {
	
	private final @Getter int id;
	private final Cleanable cleanable;
	
	public ShaderProgram(Shader... shaders) {
		this(Arrays.asList(shaders));
	}
	
	public ShaderProgram(Collection<Shader> shaders) {
		this.id = link(shaders);
		this.cleanable = GarbageCollector.registerGL(this, () -> glDeleteProgram(id));
	}
	
	public void use() {
		glUseProgram(id);
		OpenGL.checkErrors();
	}
	
	public void unuse() {
		glUseProgram(0);
		OpenGL.checkErrors();
	}
	
	public void delete() {
		cleanable.clean();
	}
	
	protected BooleanUniform createBooleanUniform(String name) {
		return new BooleanUniform(this, name);
	}
	
	protected FloatUniform createFloatUniform(String name) {
		return new FloatUniform(this, name);
	}
	
	protected Matrix4fUniform createMatrix4fUniform(String name) {
		return new Matrix4fUniform(this, name);
	}

	protected Sampler2dUniform createSampler2dUniform(String name) {
		return new Sampler2dUniform(this, name);
	}
	
	protected UnsignedByteAttribute createUnsignedByteAttribute(String name) {
		return new UnsignedByteAttribute(this, name);
	}
	
	protected Vector3fAttribute createVector3fAttribute(String name) {
		return new Vector3fAttribute(this, name, false);
	}
	
	protected Vector3ubAttribute createVector3ubAttribute(String name) {
		return new Vector3ubAttribute(this, name);
	}
	
	private static int link(Collection<Shader> shaders) {
		final var id = glCreateProgram();
		OpenGL.checkErrors();
		
		shaders.forEach((shader) -> {
			glAttachShader(id, shader.getId());
			OpenGL.checkErrors();
		});
		
		glLinkProgram(id);
		OpenGL.checkErrors();
		
		shaders.forEach((shader) -> {
			glDetachShader(id, shader.getId());
			OpenGL.checkErrors();
		});
		
		// final var success = glGetProgrami(id, GL_LINK_STATUS) != 0;
		final var logs = glGetProgramInfoLog(id);
		if (!logs.isEmpty()) {
			glDeleteProgram(id);
			throw new IllegalStateException("failed to link program: " + logs);
		}
		
		return id;
	}
	
}
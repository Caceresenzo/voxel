package opengl.shader;

import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.lang.ref.Cleaner.Cleanable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import opengl.OpenGL;
import opengl.shader.variable.attribute.Attribute;
import opengl.shader.variable.attribute.FloatAttribute;
import opengl.shader.variable.attribute.IntegerAttribute;
import opengl.shader.variable.uniform.BooleanUniform;
import opengl.shader.variable.uniform.FloatUniform;
import opengl.shader.variable.uniform.Matrix4fUniform;
import opengl.shader.variable.uniform.SamplerUniform;
import opengl.shader.variable.uniform.Vector3fUniform;
import opengl.vertex.VertexArray;

public class ShaderProgram {

	private final @Getter int id;
	private final Cleanable cleanable;
	private final List<Attribute> attributes;

	public ShaderProgram(Shader... shaders) {
		this(Arrays.asList(shaders));
	}

	public ShaderProgram(Collection<Shader> shaders) {
		this.id = link(shaders);
		this.cleanable = OpenGL.registerForGarbageCollect(this, new DeleteAction(id));
		this.attributes = new ArrayList<>(0);
	}

	public void use() {
		glUseProgram(id);
	}

	public void unuse() {
		glUseProgram(0);
	}

	public void delete() {
		cleanable.clean();
	}

	protected FloatAttribute createFloatAttribute(String name, int size) {
		return createFloatAttribute(name, size, false);
	}

	protected FloatAttribute createFloatAttribute(String name, int size, boolean normalized) {
		final var attribute = FloatAttribute.ofFloat(this, name, size, normalized);

		attributes.add(attribute);

		return attribute;
	}

	protected IntegerAttribute createIntegerAttribute(String name, int size, boolean unsigned) {
		final var attribute = IntegerAttribute.ofInteger(this, name, size, unsigned);

		attributes.add(attribute);

		return attribute;
	}

	protected IntegerAttribute createIntegerAttribute(String name, DataType dataType, int size) {
		final var attribute = new IntegerAttribute(this, name, size, dataType);

		attributes.add(attribute);

		return attribute;
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

	protected Vector3fUniform createVector3fUniform(String name) {
		return new Vector3fUniform(this, name);
	}

	protected SamplerUniform createSamplerUniform(String name) {
		return new SamplerUniform(this, name);
	}

	public void linkAttributes(VertexArray array) {
		var stride = 0;
		for (final var attribute : attributes) {
			stride += attribute.getStride();
		}

		linkAttributes(array, stride);
	}

	public void linkAttributes(VertexArray array, int stride) {
		array.bind();
		use();

		var offset = 0;
		for (final var attribute : attributes) {
			attribute.link(stride, offset);

			offset += attribute.getStride();
		}

		unuse();
		array.unbind();
	}

	public int getAttributeSizes() {
		var total = 0;
		for (final var attribute : attributes) {
			total += attribute.getSize();
		}

		return total;
	}

	public List<Attribute> getAttributes() {
		return Collections.unmodifiableList(attributes);
	}

	private static int link(Collection<Shader> shaders) {
		final var id = glCreateProgram();

		shaders.forEach((shader) -> glAttachShader(id, shader.getId()));

		glLinkProgram(id);

		shaders.forEach((shader) -> glDetachShader(id, shader.getId()));

		// final var success = glGetProgrami(id, GL_LINK_STATUS) != 0;
		final var logs = glGetProgramInfoLog(id);
		if (!logs.isEmpty()) {
			glDeleteProgram(id);
			throw new IllegalStateException("failed to link program: " + logs);
		}

		return id;
	}

	private record DeleteAction(int programId) implements Runnable {

		@Override
		public void run() {
			glDeleteProgram(programId);
		}

	}

}
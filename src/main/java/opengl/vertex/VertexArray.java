package opengl.vertex;

import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.lang.ref.Cleaner.Cleanable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import opengl.OpenGL;
import opengl.draw.BeginMode;
import opengl.shader.DataType;
import opengl.shader.ShaderProgram;
import opengl.shader.variable.attribute.Attribute;

public class VertexArray {

	private final @Getter int id;
	private final ShaderProgram shaderProgram;
	private final @Getter BeginMode beginMode;
	private final List<VertexBuffer> buffers;
	private final Cleanable cleanable;
	private @Getter int verticesCount;
	private IndiceVertexBuffer indicesBuffer;

	public VertexArray(ShaderProgram shaderProgram) {
		this(shaderProgram, BeginMode.TRIANGLES);
	}

	public VertexArray(ShaderProgram shaderProgram, BeginMode beginMode) {
		this.id = glGenVertexArrays();
		OpenGL.checkErrors();

		this.shaderProgram = shaderProgram;
		this.beginMode = beginMode;
		this.buffers = new ArrayList<>();

		this.cleanable = OpenGL.registerForGarbageCollect(this, new DeleteAction(id));
	}

	public VertexArray bind() {
		glBindVertexArray(id);
		return this;
	}

	public VertexArray unbind() {
		glBindVertexArray(0);
		return this;
	}

	public VertexArray add(ArrayVertexBuffer buffer) {
		return add(buffer, shaderProgram.getAttributes());
	}

	public VertexArray add(ArrayVertexBuffer buffer, List<Attribute> attributes) {
		bind();
		buffer.bind();

		{
			final var stride = Attribute.sumStride(attributes);
			final var sizeInBytes = buffer.getSizeInBytes();

			if (sizeInBytes % stride != 0) {
				throw new IllegalArgumentException("invalid stride of %s for buffer size (in bytes) of %s, buffer size (in array length) %s".formatted(stride, sizeInBytes, buffer.getSize()));
			}

			shaderProgram.use();

			var offset = 0;
			for (final var attribute : attributes) {
				attribute.link(stride, offset);
				offset += attribute.getStride();
			}

			shaderProgram.unuse();

			if (indicesBuffer == null) {
				verticesCount += sizeInBytes / stride;
			}

			buffers.add(buffer);
		}

		buffer.unbind();
		unbind();

		return this;
	}

	public VertexArray add(IndiceVertexBuffer buffer) {
		bind();
		buffer.bind();

		{
			indicesBuffer = buffer;
			verticesCount = buffer.getSize();

			buffers.add(buffer);
		}

		buffer.unbind();
		unbind();

		return this;
	}

	public void render() {
		shaderProgram.use();
		bind();

		if (indicesBuffer != null) {
			indicesBuffer.bind();
			glDrawElements(beginMode.getValue(), verticesCount, DataType.UNSIGNED_INTEGER.value(), 0);
			indicesBuffer.unbind();
		} else {
			glDrawArrays(beginMode.getValue(), 0, verticesCount);
		}

		unbind();
		shaderProgram.unuse();
	}

	public void delete(boolean includeBuffers) {
		cleanable.clean();

		if (includeBuffers) {
			buffers.forEach(VertexBuffer::delete);
		}
	}

	private record DeleteAction(int arrayId) implements Runnable {

		@Override
		public void run() {
			glDeleteVertexArrays(arrayId);
		}

	}

}
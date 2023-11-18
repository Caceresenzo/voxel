package voxel.client.graphics.opengl.vertex;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.lang.ref.Cleaner.Cleanable;

import org.lwjgl.system.MemoryUtil;

import lombok.Getter;
import voxel.client.graphics.opengl.util.OpenGL;

public class VertexBuffer {

	static int[] buffers;
	static int bufferIndex = 0;

	private final @Getter int id;
	private final @Getter BufferType type;
	private final @Getter UsageType usage;
	private final Cleanable cleanable;
	private @Getter int sizeInBytes;

	public VertexBuffer(BufferType type, UsageType usage) {
		this.id = glGenBuffers();
		OpenGL.checkErrors();

		this.type = type;
		this.usage = usage;

		this.cleanable = OpenGL.registerForGarbageCollect(this, new DeleteAction(id));

		this.sizeInBytes = 0;
	}

	public VertexBuffer bind() {
		glBindBuffer(type.value(), id);
		return this;
	}

	public VertexBuffer store(float[] data) {
		bind();
		glBufferData(type.value(), data, usage.value());
		sizeInBytes = data.length * Float.BYTES;
		return this;
	}

	public VertexBuffer store(int[] data) {
		bind();
		glBufferData(type.value(), data, usage.value());
		sizeInBytes = data.length * Integer.BYTES;
		return this;
	}

	@Deprecated
	public VertexBuffer store(byte[] data) {
		bind();

		final var buffer = MemoryUtil.memAlloc(data.length);
		buffer.put(data).flip();

		glBufferData(type.value(), buffer, usage.value());
		sizeInBytes = data.length;

		MemoryUtil.memFree(buffer);
		return this;
	}

	public VertexBuffer unbind() {
		glBindBuffer(type.value(), 0);
		return this;
	}

	public void delete() {
		cleanable.clean();
	}

	private record DeleteAction(int bufferId) implements Runnable {

		@Override
		public void run() {
			glDeleteBuffers(bufferId);
		}

	}

}
package opengl.vertex;

import static org.lwjgl.opengl.GL15.glBufferData;

import org.lwjgl.system.MemoryUtil;

import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ArrayVertexBuffer extends AbstractVertexBuffer {

	public ArrayVertexBuffer(BufferUsage usage) {
		super(BufferBindTarget.ARRAY, usage);
	}

	@Override
	public ArrayVertexBuffer bind() {
		return (ArrayVertexBuffer) super.bind();
	}

	@Override
	public ArrayVertexBuffer unbind() {
		return (ArrayVertexBuffer) super.unbind();
	}

	public ArrayVertexBuffer store(float[] data) {
		bind();
		glBufferData(target.getValue(), data, usage.getValue());
		setSize(data.length, Float.BYTES);
		return this;
	}

	public ArrayVertexBuffer store(FloatList data) {
		return store(data.toFloatArray());
	}

	public ArrayVertexBuffer store(int[] data) {
		bind();
		glBufferData(target.getValue(), data, usage.getValue());
		setSize(data.length, Integer.BYTES);
		return this;
	}

	public ArrayVertexBuffer store(IntList data) {
		return store(data.toIntArray());
	}

	public ArrayVertexBuffer store(byte[] data) {
		bind();

		final var buffer = MemoryUtil.memAlloc(data.length);
		buffer.put(data).flip();

		glBufferData(target.getValue(), buffer, usage.getValue());
		setSize(data.length, Byte.BYTES);

		MemoryUtil.memFree(buffer);
		return this;
	}

}
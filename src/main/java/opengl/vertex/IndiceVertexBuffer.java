package opengl.vertex;

import static org.lwjgl.opengl.GL15.glBufferData;

import it.unimi.dsi.fastutil.ints.IntList;

public class IndiceVertexBuffer extends AbstractVertexBuffer {

	public IndiceVertexBuffer(BufferUsage usage) {
		super(BufferBindTarget.ELEMENT_ARRAY, usage);
	}

	@Override
	public IndiceVertexBuffer bind() {
		return (IndiceVertexBuffer) super.bind();
	}

	@Override
	public IndiceVertexBuffer unbind() {
		return (IndiceVertexBuffer) super.unbind();
	}

	public IndiceVertexBuffer store(int[] data) {
		bind();
		glBufferData(target.getValue(), data, usage.getValue());
		setSize(data.length, Integer.BYTES);
		return this;
	}

	public IndiceVertexBuffer store(IntList data) {
		return store(data.toIntArray());
	}

}
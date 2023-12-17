package opengl.vertex;

public interface VertexBuffer {

	BufferBindTarget getTarget();

	BufferUsage getUsage();

	int getSize();

	long getSizeInBytes();

	VertexBuffer bind();

	VertexBuffer unbind();

	void delete();

	static ArrayVertexBuffer ofArray(BufferUsage usage) {
		return new ArrayVertexBuffer(usage);
	}

	static IndiceVertexBuffer ofIndice(BufferUsage usage) {
		return new IndiceVertexBuffer(usage);
	}

}
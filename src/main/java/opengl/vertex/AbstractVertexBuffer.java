package opengl.vertex;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.lang.ref.Cleaner.Cleanable;

import lombok.Getter;
import opengl.OpenGL;

public abstract class AbstractVertexBuffer implements VertexBuffer {

	protected final @Getter int id;
	protected final @Getter BufferBindTarget target;
	protected final @Getter BufferUsage usage;
	private final Cleanable cleanable;

	protected @Getter int size;
	protected @Getter long sizeInBytes;

	public AbstractVertexBuffer(BufferBindTarget target, BufferUsage usage) {
		this.id = glGenBuffers();
		OpenGL.checkErrors();

		this.target = target;
		this.usage = usage;

		this.cleanable = OpenGL.registerForGarbageCollect(this, new DeleteAction(id));

		this.sizeInBytes = 0;
	}

	public AbstractVertexBuffer bind() {
		glBindBuffer(target.getValue(), id);
		return this;
	}

	public AbstractVertexBuffer unbind() {
		glBindBuffer(target.getValue(), 0);
		return this;
	}

	public void delete() {
		cleanable.clean();
	}

	protected void setSize(int bufferLength, int dataSize) {
		size = bufferLength;
		sizeInBytes = size * dataSize;
	}

	private record DeleteAction(int bufferId) implements Runnable {

		@Override
		public void run() {
			glDeleteBuffers(bufferId);
		}

	}

}
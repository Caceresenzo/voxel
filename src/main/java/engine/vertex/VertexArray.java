package engine.vertex;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.lang.ref.Cleaner.Cleanable;
import java.util.ArrayList;
import java.util.List;

import engine.util.OpenGL;
import lombok.Getter;

public class VertexArray {

	private final @Getter int id;
	private final List<VertexBuffer> buffers;
	private final Cleanable cleanable;

	public VertexArray() {
		this.id = glGenVertexArrays();
		OpenGL.checkErrors();

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

	public VertexArray add(VertexBuffer buffer) {
		bind();
		buffer.bind();

		buffers.add(buffer);

		return this;
	}

	public void delete() {
		cleanable.clean();
	}

	private record DeleteAction(int arrayId) implements Runnable {

		@Override
		public void run() {
			glDeleteVertexArrays(arrayId);
		}

	}

}
package engine.vertex;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.lang.ref.Cleaner.Cleanable;

import org.lwjgl.system.MemoryUtil;

import engine.util.GarbageCollector;
import engine.util.OpenGL;
import lombok.Getter;

public class VertexBuffer {
	
	private final @Getter int id;
	private final @Getter BufferType type;
	private final @Getter UsageType usage;
	private final Cleanable cleanable;
	
	public VertexBuffer(BufferType type, UsageType usage) {
		this.id = glGenBuffers();
		OpenGL.checkErrors();
		
		this.type = type;
		this.usage = usage;
		
		this.cleanable = GarbageCollector.registerGL(this, () -> glDeleteBuffers(id));
	}
	
	public VertexBuffer bind() {
		glBindBuffer(type.value(), id);
		OpenGL.checkErrors();
		return this;
	}
	
	public VertexBuffer unbind() {
		glBindBuffer(type.value(), 0);
		OpenGL.checkErrors();
		return this;
	}
	
	public VertexBuffer store(float[] data) {
		bind();
		glBufferData(type.value(), data, usage.value());
		OpenGL.checkErrors();
		return this;
	}
	
	public VertexBuffer store(int[] data) {
		bind();
		glBufferData(type.value(), data, usage.value());
		OpenGL.checkErrors();
		return this;
	}
	
	public VertexBuffer store(byte[] data) {
		bind();
		
		final var buffer = MemoryUtil.memAlloc(data.length);
		buffer.put(data).flip();
		System.out.println(buffer);
		
		glBufferData(type.value(), buffer, usage.value());
		MemoryUtil.memFree(buffer);
		
		OpenGL.checkErrors();
		return this;
	}
	
	public void delete() {
		cleanable.clean();
	}
	
}
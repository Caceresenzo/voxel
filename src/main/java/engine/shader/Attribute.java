package engine.shader;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import engine.util.OpenGL;

public class Attribute extends Variable {
	
	protected final int size;
	protected final int dataSize;
	protected final boolean normalized;
	
	protected Attribute(ShaderProgram program, String name, int size, int dataSize, boolean normalized) {
		super(program, name);
		
		this.size = size;
		this.dataSize = dataSize;
		this.normalized = normalized;
	}
	
	@Override
	protected int findLocation(ShaderProgram program) {
		return glGetAttribLocation(program.getId(), name);
	}
	
	public void enable() {
		glEnableVertexAttribArray(location);
		OpenGL.checkErrors();
	}
	
	public void link(int stride, int offset) {
		enable();
		
		glVertexAttribPointer(location, size, dataSize, normalized, stride, offset);
		OpenGL.checkErrors();
	}
	
	public void disable() {
		glDisableVertexAttribArray(location);
		OpenGL.checkErrors();
	}
	
}
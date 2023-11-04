package engine.shader;

import static org.lwjgl.opengl.GL20.GL_ACTIVE_ATTRIBUTES;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetActiveAttrib;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.List;
import java.util.stream.IntStream;

import engine.util.OpenGL;

public class Attribute extends Variable {
	
	protected final int size;
	protected final int dataType;
	protected final boolean normalized;
	
	protected Attribute(ShaderProgram program, String name, int size, int dataType, boolean normalized) {
		super(program, name);
		
		this.size = size;
		this.dataType = dataType;
		this.normalized = normalized;
	}
	
	@Override
	protected int findLocation(ShaderProgram program) {
		return glGetAttribLocation(program.getId(), name);
	}
	
	@Override
	protected List<String> listNames(ShaderProgram program) {
		final var count = glGetProgrami(program.getId(), GL_ACTIVE_ATTRIBUTES);

		try (final var stack = stackPush()) {
			final var ignored = stack.mallocInt(1);
			
			return IntStream.range(0, count)
				.mapToObj((index) -> glGetActiveAttrib(program.getId(), index, ignored, ignored))
				.toList();
		}
	}
	
	public void enable() {
		glEnableVertexAttribArray(location);
		OpenGL.checkErrors();
	}
	
	public void link(int stride, int offset) {
		if (location == -1) {
			return;
		}
		
		enable();
		doLink(stride, offset);
		
		OpenGL.checkErrors();
	}
	
	protected void doLink(int stride, int offset) {
		glVertexAttribPointer(location, size, dataType, normalized, stride, offset);
	}
	
	public void disable() {
		glDisableVertexAttribArray(location);
		OpenGL.checkErrors();
	}
	
}
package engine.shader.variable.attribute;

import static org.lwjgl.opengl.GL20.GL_ACTIVE_ATTRIBUTES;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetActiveAttrib;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.List;
import java.util.stream.IntStream;

import engine.shader.DataType;
import engine.shader.ShaderProgram;
import engine.shader.variable.Variable;
import lombok.Getter;

public abstract class Attribute extends Variable {

	protected final @Getter int size;
	protected final @Getter DataType dataType;

	protected Attribute(ShaderProgram program, String name, int size, DataType dataType) {
		super(program, name);

		this.size = size;
		this.dataType = dataType;
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
	}

	public void link(int stride, int offset) {
		if (location == -1) {
			return;
		}

		enable();
		doLink(stride, offset);
	}

	protected abstract void doLink(int stride, int offset);

	public void disable() {
		glDisableVertexAttribArray(location);
	}

}
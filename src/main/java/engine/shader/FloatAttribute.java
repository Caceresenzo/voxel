package engine.shader;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class FloatAttribute extends Attribute {

	private final boolean normalized;

	protected FloatAttribute(ShaderProgram program, String name, int size, int dataType, boolean normalized) {
		super(program, name, size, dataType);

		this.normalized = normalized;
	}

	protected void doLink(int stride, int offset) {
		glVertexAttribPointer(location, size, dataType, normalized, stride, offset);
	}

	public static FloatAttribute ofFloat(ShaderProgram shaderProgram, String name, int size, boolean normalized) {
		return new FloatAttribute(shaderProgram, name, size, GL_FLOAT, normalized);
	}

}
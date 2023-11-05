package engine.shader.variable.attribute;

import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import engine.shader.DataType;
import engine.shader.ShaderProgram;

public class FloatAttribute extends Attribute {

	private final boolean normalized;

	protected FloatAttribute(ShaderProgram program, String name, int size, DataType dataType, boolean normalized) {
		super(program, name, size, dataType);

		this.normalized = normalized;
	}

	protected void doLink(int stride, int offset) {
		glVertexAttribPointer(location, size, dataType.value(), normalized, stride, offset);
	}

	public static FloatAttribute ofFloat(ShaderProgram shaderProgram, String name, int size, boolean normalized) {
		return new FloatAttribute(shaderProgram, name, size, DataType.FLOAT, normalized);
	}

}
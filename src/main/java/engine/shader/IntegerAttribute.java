package engine.shader;

import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

public class IntegerAttribute extends Attribute {

	protected IntegerAttribute(ShaderProgram program, String name, int size, int dataType) {
		super(program, name, size, dataType);
	}

	protected void doLink(int stride, int offset) {
		glVertexAttribIPointer(location, size, dataType, stride, offset);
	}

	public static IntegerAttribute ofInteger(ShaderProgram program, String name, int size, boolean unsigned) {
		final var dataType = unsigned
			? GL_UNSIGNED_INT
			: GL_INT;

		return new IntegerAttribute(program, name, size, dataType);
	}

}
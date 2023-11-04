package engine.shader;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

public class UnsignedIntegerAttribute extends Attribute {
	
	protected UnsignedIntegerAttribute(ShaderProgram program, String name) {
		super(program, name, 1, GL_UNSIGNED_INT, false);
	}
	
	@Override
	protected void doLink(int stride, int offset) {
		glVertexAttribIPointer(location, size, dataType, stride, offset);
	}
	
}
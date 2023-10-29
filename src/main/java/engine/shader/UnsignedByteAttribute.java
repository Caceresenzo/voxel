package engine.shader;

import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

public class UnsignedByteAttribute extends Attribute {

	protected UnsignedByteAttribute(ShaderProgram program, String name) {
		super(program, name, 1, GL_UNSIGNED_BYTE, false);
	}
	
	@Override
	protected void doLink(int stride, int offset) {
		glVertexAttribIPointer(location, size, dataType, stride, offset);
	}
	
}
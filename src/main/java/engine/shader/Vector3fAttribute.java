package engine.shader;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public class Vector3fAttribute extends Attribute {

	protected Vector3fAttribute(ShaderProgram program, String name, boolean normalized) {
		super(program, name, 3, GL_FLOAT, normalized);
	}
	
}
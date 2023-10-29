package engine.shader;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class BooleanUniform extends Uniform {
	
	public BooleanUniform(ShaderProgram program, String name) {
		super(program, name);
	}
	
	public void load(boolean value) {
		glUniform1i(location, value ? 1 : 0);
	}
	
}
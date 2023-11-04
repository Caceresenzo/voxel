package engine.shader;

import static org.lwjgl.opengl.GL20.glUniform1f;

public class FloatUniform extends Uniform {
	
	protected FloatUniform(ShaderProgram program, String name) {
		super(program, name);
	}
	
	public void load(float value) {
		glUniform1f(location, value);
	}
	
}
package opengl.shader.variable.uniform;

import static org.lwjgl.opengl.GL20.glUniform1f;

import opengl.shader.ShaderProgram;

public class FloatUniform extends Uniform {
	
	public FloatUniform(ShaderProgram program, String name) {
		super(program, name);
	}
	
	public void load(float value) {
		glUniform1f(location, value);
	}
	
}
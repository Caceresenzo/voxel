package opengl.shader.variable.uniform;

import static org.lwjgl.opengl.GL20.glUniform1i;

import opengl.shader.ShaderProgram;

public class SamplerUniform extends Uniform {
	
	public SamplerUniform(ShaderProgram program, String name) {
		super(program, name);
	}
	
	public void load(int unit) {
		glUniform1i(location, unit);
	}
	
}
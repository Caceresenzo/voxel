package voxel.client.graphics.opengl.shader.variable.uniform;

import static org.lwjgl.opengl.GL20.glUniform1i;

import voxel.client.graphics.opengl.shader.ShaderProgram;

public class SamplerUniform extends Uniform {
	
	public SamplerUniform(ShaderProgram program, String name) {
		super(program, name);
	}
	
	public void load(int unit) {
		glUniform1i(location, unit);
	}
	
}
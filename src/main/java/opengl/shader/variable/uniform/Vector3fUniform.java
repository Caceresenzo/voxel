package opengl.shader.variable.uniform;

import static org.lwjgl.opengl.GL20.glUniform3f;

import org.joml.Vector3fc;

import opengl.shader.ShaderProgram;

public class Vector3fUniform extends Uniform {

	public Vector3fUniform(ShaderProgram program, String name) {
		super(program, name);
	}

	public void load(float x, float y, float z) {
		glUniform3f(location, x, y, z);
	}
	
	public void load(Vector3fc value) {
		glUniform3f(location, value.x(), value.y(), value.z());
	}

}
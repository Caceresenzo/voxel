package engine.shader;

import static org.lwjgl.opengl.GL20.glUniform1i;

import engine.texture.Texture;

public class Sampler2dUniform extends Uniform {
	
	public Sampler2dUniform(ShaderProgram program, String name) {
		super(program, name);
	}
	
	public void load(Texture texture) {
		glUniform1i(location, texture.getId());
	}
	
}
package engine.shader;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

import lombok.Getter;

@Getter
public class Uniform extends Variable {
	
	protected Uniform(ShaderProgram program, String name) {
		super(program, name);
	}
	
	@Override
	protected int findLocation(ShaderProgram program) {
		return glGetUniformLocation(program.getId(), name);
	}
	
}
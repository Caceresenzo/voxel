package opengl.shader.variable.uniform;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import opengl.shader.ShaderProgram;

public class Matrix4fUniform extends Uniform {

	public Matrix4fUniform(ShaderProgram program, String name) {
		super(program, name);
	}

	public void load(Matrix4f value) {
		try (final var stack = MemoryStack.stackPush()) {
			final var buffer = stack.mallocFloat(4 * 4);
			value.get(buffer);

			glUniformMatrix4fv(location, false, buffer);
		}
	}

}
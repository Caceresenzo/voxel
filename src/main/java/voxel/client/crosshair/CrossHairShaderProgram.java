package voxel.client.crosshair;

import java.io.IOException;

import opengl.shader.Shader;
import opengl.shader.ShaderProgram;
import opengl.shader.variable.attribute.FloatAttribute;

public class CrossHairShaderProgram extends ShaderProgram {

	public final FloatAttribute position;

	public CrossHairShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);

		this.position = createFloatAttribute("aPos", 2);
	}

	public static CrossHairShaderProgram create() throws IOException {
		try (
			final var vertexInputStream = Shader.class.getResourceAsStream("/shaders/crosshair.vert");
			final var fragmentInputStream = Shader.class.getResourceAsStream("/shaders/crosshair.frag");
		) {
			return new CrossHairShaderProgram(
				Shader.load(Shader.Type.VERTEX, vertexInputStream),
				Shader.load(Shader.Type.FRAGMENT, fragmentInputStream)
			);
		}
	}

}
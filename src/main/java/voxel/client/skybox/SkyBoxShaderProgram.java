package voxel.client.skybox;

import java.io.IOException;

import opengl.shader.Shader;
import opengl.shader.ShaderProgram;
import opengl.shader.variable.attribute.FloatAttribute;
import opengl.shader.variable.uniform.Matrix4fUniform;
import opengl.shader.variable.uniform.Vector3fUniform;

public class SkyBoxShaderProgram extends ShaderProgram {
	
	public final Matrix4fUniform projection;
	public final Matrix4fUniform view;
	public final Vector3fUniform color;
	public final FloatAttribute position;
	
	private SkyBoxShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);
		
		this.projection = createMatrix4fUniform("m_projection");
		this.view = createMatrix4fUniform("m_view");
		this.color = createVector3fUniform("color");
		this.position = createFloatAttribute("in_position", 3, false);
	}

	public static SkyBoxShaderProgram create() throws IOException {
		try (
			final var vertexInputStream = Shader.class.getResourceAsStream("/shaders/skybox.vert");
			final var fragmentInputStream = Shader.class.getResourceAsStream("/shaders/skybox.frag");
		) {
			return new SkyBoxShaderProgram(
				Shader.load(Shader.Type.VERTEX, vertexInputStream),
				Shader.load(Shader.Type.FRAGMENT, fragmentInputStream)
			);
		}
	}
	
}
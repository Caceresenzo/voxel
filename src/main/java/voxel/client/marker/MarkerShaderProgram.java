package voxel.client.marker;

import java.io.IOException;

import opengl.shader.Shader;
import opengl.shader.ShaderProgram;
import opengl.shader.variable.attribute.FloatAttribute;
import opengl.shader.variable.uniform.Matrix4fUniform;
import opengl.shader.variable.uniform.SamplerUniform;

public class MarkerShaderProgram extends ShaderProgram {

	public final Matrix4fUniform projection;
	public final Matrix4fUniform model;
	public final Matrix4fUniform view;
	public final SamplerUniform texture;
	public final FloatAttribute textureCoordinate;
	public final FloatAttribute position;

	public MarkerShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);

		this.projection = createMatrix4fUniform("m_projection");
		this.model = createMatrix4fUniform("m_model");
		this.view = createMatrix4fUniform("m_view");
		this.texture = createSamplerUniform("u_texture");
		this.textureCoordinate = createFloatAttribute("in_tex_coord", 2);
		this.position = createFloatAttribute("in_position", 3);
	}

	public static MarkerShaderProgram create() throws IOException {
		try (
			final var vertexInputStream = Shader.class.getResourceAsStream("/shaders/marker.vert");
			final var fragmentInputStream = Shader.class.getResourceAsStream("/shaders/marker.frag");
		) {
			return new MarkerShaderProgram(
				Shader.load(Shader.Type.VERTEX, vertexInputStream),
				Shader.load(Shader.Type.FRAGMENT, fragmentInputStream)
			);
		}
	}

}
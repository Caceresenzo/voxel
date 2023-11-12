package voxel.client.graphics.mesh.marker;

import java.io.IOException;

import voxel.client.graphics.opengl.shader.Shader;
import voxel.client.graphics.opengl.shader.ShaderProgram;
import voxel.client.graphics.opengl.shader.variable.attribute.FloatAttribute;
import voxel.client.graphics.opengl.shader.variable.uniform.Matrix4fUniform;
import voxel.client.graphics.opengl.shader.variable.uniform.SamplerUniform;

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
		this.textureCoordinate = createFloatAttribute("in_tex_coord", 2, false);
		this.position = createFloatAttribute("in_position", 3, false);
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
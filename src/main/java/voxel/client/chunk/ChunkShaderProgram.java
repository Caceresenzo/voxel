package voxel.client.chunk;

import java.io.IOException;

import opengl.shader.Shader;
import opengl.shader.ShaderProgram;
import opengl.shader.variable.attribute.IntegerAttribute;
import opengl.shader.variable.uniform.Matrix4fUniform;
import opengl.shader.variable.uniform.SamplerUniform;

public class ChunkShaderProgram extends ShaderProgram {
	
	public final Matrix4fUniform projection;
	public final Matrix4fUniform model;
	public final Matrix4fUniform view;
	public final SamplerUniform atlas;
	public final IntegerAttribute packedData;
	
	public ChunkShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);
		
		this.projection = createMatrix4fUniform("m_projection");
		this.model = createMatrix4fUniform("m_model");
		this.view = createMatrix4fUniform("m_view");
		this.atlas = createSamplerUniform("u_atlas");
		this.packedData = createIntegerAttribute("packed_data", 1, true);
	}

	public static ChunkShaderProgram create() throws IOException {
		try (
			final var vertexInputStream = Shader.class.getResourceAsStream("/shaders/chunk.vert");
			final var fragmentInputStream = Shader.class.getResourceAsStream("/shaders/chunk.frag");
		) {
			return new ChunkShaderProgram(
				Shader.load(Shader.Type.VERTEX, vertexInputStream),
				Shader.load(Shader.Type.FRAGMENT, fragmentInputStream)
			);
		}
	}
	
}
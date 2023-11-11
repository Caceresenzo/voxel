package voxel.mesh;

import engine.shader.Shader;
import engine.shader.ShaderProgram;
import engine.shader.variable.attribute.IntegerAttribute;
import engine.shader.variable.uniform.Matrix4fUniform;
import engine.shader.variable.uniform.SamplerUniform;

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
	
}
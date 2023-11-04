package voxel.mesh;

import engine.shader.IntegerAttribute;
import engine.shader.Matrix4fUniform;
import engine.shader.Sampler2dUniform;
import engine.shader.Shader;
import engine.shader.ShaderProgram;

public class ChunkShaderProgram extends ShaderProgram {
	
	public final Matrix4fUniform projection;
	public final Matrix4fUniform model;
	public final Matrix4fUniform view;
	public final Sampler2dUniform texture;
	public final IntegerAttribute packedData;
	
	public ChunkShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);
		
		this.projection = createMatrix4fUniform("m_projection");
		this.model = createMatrix4fUniform("m_model");
		this.view = createMatrix4fUniform("m_view");
		this.texture = createSampler2dUniform("texture");
		this.packedData = createIntegerAttribute("packed_data", 1, true);
	}
	
}
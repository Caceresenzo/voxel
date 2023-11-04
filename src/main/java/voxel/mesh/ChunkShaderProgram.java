package voxel.mesh;

import engine.shader.Matrix4fUniform;
import engine.shader.Sampler2dUniform;
import engine.shader.Shader;
import engine.shader.ShaderProgram;
import engine.shader.UnsignedIntegerAttribute;

public class ChunkShaderProgram extends ShaderProgram {
	
	public final Matrix4fUniform projection;
	public final Matrix4fUniform model;
	public final Matrix4fUniform view;
	public final Sampler2dUniform texture;
	public final UnsignedIntegerAttribute packedData;
	
	public ChunkShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);
		
		this.projection = createMatrix4fUniform("m_projection");
		this.model = createMatrix4fUniform("m_model");
		this.view = createMatrix4fUniform("m_view");
		this.texture = createSampler2dUniform("texture");
		this.packedData = createUnsignedIntegerAttribute("packed_data");
	}
	
}
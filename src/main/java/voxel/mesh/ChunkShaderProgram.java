package voxel.mesh;

import engine.shader.Matrix4fUniform;
import engine.shader.Sampler2dUniform;
import engine.shader.Shader;
import engine.shader.ShaderProgram;
import engine.shader.UnsignedByteAttribute;
import engine.shader.Vector3ubAttribute;

public class ChunkShaderProgram extends ShaderProgram {
	
	public final Matrix4fUniform projection;
	public final Matrix4fUniform model;
	public final Matrix4fUniform view;
	public final Sampler2dUniform texture;
	public final Vector3ubAttribute position;
	public final UnsignedByteAttribute voxelId;
	public final UnsignedByteAttribute faceId;
	
	public ChunkShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);
		
		this.projection = createMatrix4fUniform("m_projection");
		this.model = createMatrix4fUniform("m_model");
		this.view = createMatrix4fUniform("m_view");
		this.texture = createSampler2dUniform("texture");
		this.position = createVector3ubAttribute("in_position");
		this.voxelId = createUnsignedByteAttribute("voxel_id");
		this.faceId = createUnsignedByteAttribute("face_id");
	}
	
}
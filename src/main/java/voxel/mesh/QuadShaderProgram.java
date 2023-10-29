package voxel.mesh;

import engine.shader.Matrix4fUniform;
import engine.shader.Shader;
import engine.shader.ShaderProgram;
import engine.shader.Vector3fAttribute;

public class QuadShaderProgram extends ShaderProgram {
	
	public final Matrix4fUniform projection;
	public final Matrix4fUniform model;
	public final Matrix4fUniform view;
	public final Vector3fAttribute position;
	public final Vector3fAttribute color;
	
	public QuadShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);
		
		this.projection = createMatrix4fUniform("m_projection");
		this.model = createMatrix4fUniform("m_model");
		this.view = createMatrix4fUniform("m_view");
		this.position = createVector3fAttribute("in_position");
		this.color = createVector3fAttribute("in_color");
	}
	
}
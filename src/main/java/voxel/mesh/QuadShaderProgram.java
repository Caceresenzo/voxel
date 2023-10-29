package voxel.mesh;

import engine.shader.FloatUniform;
import engine.shader.Shader;
import engine.shader.ShaderProgram;
import engine.shader.Vector3fAttribute;

public class QuadShaderProgram extends ShaderProgram {
	
	public final FloatUniform opacity;
	public final Vector3fAttribute position;
	public final Vector3fAttribute color;
	
	public QuadShaderProgram(Shader vertexShader, Shader fragmentShader) {
		super(vertexShader, fragmentShader);
		
		this.opacity = createFloatUniform("opacity");
		this.position = createVector3fAttribute("in_position");
		this.color = createVector3fAttribute("in_color");
	}
	
}
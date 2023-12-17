package voxel.client.crosshair;

import opengl.draw.BeginMode;
import opengl.vertex.BufferUsage;
import opengl.vertex.VertexArray;
import opengl.vertex.VertexBuffer;

public class CrossHairMesh {

	private final CrossHairShaderProgram shaderProgram;
	private VertexArray vertexArray;

	public CrossHairMesh(CrossHairShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;

		createVertexArray();
	}

	private void createVertexArray() {
		final var vertexData = getVertexData();

		final var buffer = VertexBuffer.ofArray(BufferUsage.STATIC_DRAW);
		buffer.store(vertexData);

		final var array = new VertexArray(shaderProgram, BeginMode.LINES);
		array.add(buffer);

		this.vertexArray = array;
	}

	public void render() {
		shaderProgram.use();
		vertexArray.render();
	}

	private float[] getVertexData() {
		return new float[] {
			-0.02f, +0.00f,
			+0.02f, +0.00f,
			+0.00f, -0.02f,
			+0.00f, +0.02f
		};
	}

}
package voxel.client.chunk;

import org.joml.Matrix4f;

import opengl.vertex.VertexArray;

public class ChunkMesh {

	private final ClientChunk chunk;
	private VertexArray vertexArray;
	private Matrix4f modelMatrix;

	public ChunkMesh(ClientChunk chunk, VertexArray vertexArray) {
		this.chunk = chunk;
		this.vertexArray = vertexArray;
		this.modelMatrix = new Matrix4f().translate(chunk.getPosition().toBlockPosition().toFloatVector());
	}

	public void render() {
		final var shaderProgram = chunk.getShaderProgram();

		shaderProgram.use();
		shaderProgram.model.load(modelMatrix);

		vertexArray.render();
	}

	public void delete() {
		vertexArray.delete(true);
	}

}
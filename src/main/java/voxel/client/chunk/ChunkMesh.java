package voxel.client.chunk;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import opengl.vertex.VertexArray;

public class ChunkMesh {

	private final Chunk chunk;
	private VertexArray vertexArray;
	private Matrix4f modelMatrix;

	public ChunkMesh(Chunk chunk, VertexArray vertexArray) {
		this.chunk = chunk;
		this.vertexArray = vertexArray;
		this.modelMatrix = new Matrix4f().translate(new Vector3f(chunk.getWorldPosition()));
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
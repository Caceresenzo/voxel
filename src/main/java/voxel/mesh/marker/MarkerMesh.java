package voxel.mesh.marker;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.joml.Matrix4f;
import org.joml.Vector3i;

import engine.util.MathUtils;
import engine.vertex.BufferType;
import engine.vertex.UsageType;
import engine.vertex.VertexArray;
import engine.vertex.VertexBuffer;
import voxel.Camera;

public class MarkerMesh {

	private final MarkerShaderProgram shaderProgram;
	private VertexArray vertexArray;
	private int triangleCount;
	private Matrix4f modelMatrix;

	public MarkerMesh(MarkerShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
		this.modelMatrix = new Matrix4f();

		createVertexArray();
	}

	public void move(Vector3i position) {
		modelMatrix.identity().translate(position.x, position.y, position.z);
	}

	private void createVertexArray() {
		final var vertexData = getVertexData();

		final var buffer = new VertexBuffer(BufferType.ARRAY, UsageType.STATIC_DRAW);
		buffer.store(vertexData);

		final var array = new VertexArray().add(buffer);

		shaderProgram.use();
		shaderProgram.linkAttributes(array);

		if (vertexArray != null) {
			vertexArray.delete(true);
		}

		this.triangleCount = vertexData.length / shaderProgram.getAttributeSizes();
		this.vertexArray = array;
	}

	public void render(Camera camera) {
		shaderProgram.use();
		shaderProgram.view.load(camera.getView());
		shaderProgram.model.load(modelMatrix);
		shaderProgram.texture.load(0);
		vertexArray.bind();

		glDrawArrays(GL_TRIANGLES, 0, triangleCount);
	}

	private float[] getVertexData() {
		final var positions = pack(
			new byte[][] {
				{ 0, 0, 1 }, { 1, 0, 1 }, { 1, 1, 1 }, { 0, 1, 1 },
				{ 0, 1, 0 }, { 0, 0, 0 }, { 1, 0, 0 }, { 1, 1, 0 }
			},
			new byte[][] {
				{ 0, 2, 3 }, { 0, 1, 2 },
				{ 1, 7, 2 }, { 1, 6, 7 },
				{ 6, 5, 4 }, { 4, 7, 6 },
				{ 3, 4, 5 }, { 3, 5, 0 },
				{ 3, 7, 4 }, { 3, 2, 7 },
				{ 0, 6, 1 }, { 0, 5, 6 }
			}
		);

		final var textureCoordinates = pack(
			new byte[][] {
				{ 0, 0 }, { 1, 0 },
				{ 1, 1 }, { 0, 1 },
			},
			new byte[][] {
				{ 0, 2, 3 }, { 0, 1, 2 },
				{ 0, 2, 3 }, { 0, 1, 2 },
				{ 0, 1, 2 }, { 2, 3, 0 },
				{ 2, 3, 0 }, { 2, 0, 1 },
				{ 0, 2, 3 }, { 0, 1, 2 },
				{ 3, 1, 2 }, { 3, 0, 1 },
			}
		);
		
		return MathUtils.horizontalStack(
			textureCoordinates, 2,
			positions, 3
		);
	}

	public static float[] pack(byte[][] vertices, byte[][] indices) {
		final var verticeLength = vertices[0].length;
		final var size = indices.length * indices[0].length * verticeLength;
		final var result = new float[size];

		var index = 0;
		for (var triangle : indices) {
			for (var indice : triangle) {
				for (var vertice : vertices[indice]) {
					result[index++] = vertice;
				}
			}
		}

		return result;
	}

}
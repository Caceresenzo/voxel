package voxel.client.graphics.mesh.marker;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import voxel.client.Camera;
import voxel.client.graphics.opengl.vertex.BufferType;
import voxel.client.graphics.opengl.vertex.UsageType;
import voxel.client.graphics.opengl.vertex.VertexArray;
import voxel.client.graphics.opengl.vertex.VertexBuffer;

public class MarkerMesh {

	private final MarkerShaderProgram shaderProgram;
	private VertexArray vertexArray;
	private Matrix4f modelMatrix;

	public MarkerMesh(MarkerShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
		this.modelMatrix = new Matrix4f();

		createVertexArray();
	}

	public void move(int x, int y, int z) {
		modelMatrix.identity().translate(x, y, z);
	}

	public void move(Vector3i position) {
		modelMatrix.identity().translate(position.x, position.y, position.z);
	}

	private void createVertexArray() {
		final var vertexData = getVertexData();

		final var textureCoordinatesBuffer = new VertexBuffer(BufferType.ARRAY, UsageType.STATIC_DRAW);
		textureCoordinatesBuffer.store(vertexData.getLeft());

		final var positionsBuffer = new VertexBuffer(BufferType.ARRAY, UsageType.STATIC_DRAW);
		positionsBuffer.store(vertexData.getRight());

		final var array = new VertexArray(shaderProgram);
		array.add(textureCoordinatesBuffer, List.of(shaderProgram.textureCoordinate));
		array.add(positionsBuffer, List.of(shaderProgram.position));

		if (vertexArray != null) {
			vertexArray.delete(true);
		}

		this.vertexArray = array;
	}

	public void render(Camera camera) {
		shaderProgram.use();
		shaderProgram.view.load(camera.getView());
		shaderProgram.model.load(modelMatrix);
		shaderProgram.texture.load(0);
		vertexArray.render();
	}

	private Pair<float[], float[]> getVertexData() {
		// @formatter:off
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
		
		return Pair.of(textureCoordinates, positions);
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
package voxel.mesh;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.joml.Vector3i;

import engine.util.OpenGL;
import engine.vertex.BufferType;
import engine.vertex.UsageType;
import engine.vertex.VertexArray;
import engine.vertex.VertexBuffer;
import voxel.Chunk;
import voxel.Face;
import voxel.Settings;

public class ChunkMesh {
	
	private final Chunk chunk;
	private final ChunkShaderProgram program;
	private VertexArray vertexArray;
	private int triangleCount;
	
	public ChunkMesh(Chunk chunk, ChunkShaderProgram shaderProgram) {
		this.chunk = chunk;
		this.program = shaderProgram;
		
		this.vertexArray = createVertexArray();
	}
	
	private VertexArray createVertexArray() {
		final var vertexData = getVertexData();
		
		final var buffer = new VertexBuffer(BufferType.ARRAY, UsageType.STATIC_DRAW);
		buffer.store(vertexData);
		
		final var array = new VertexArray().add(buffer);
		
		program.use();
		
		final var stride = 5;
		if (triangleCount % stride != 0) {
			throw new IllegalStateException("invalid stride");
		}
		
		program.position.link(stride, 0);
		program.voxelId.link(stride, 3);
		program.faceId.link(stride, 4);
		
		triangleCount = vertexData.length / stride;
		
		return array;
	}
	
	public void render() {
		program.use();
		vertexArray.bind();
		
		glDrawArrays(GL_TRIANGLES, 0, triangleCount);
		OpenGL.checkErrors();
	}
	
	private boolean isVoid(int x, int y, int z, byte[] chunkVoxels) {
		if (isInChunk(x) && isInChunkHeight(y) && isInChunk(z)) {
			if (chunkVoxels[toVoxelIndex(x, y, z)] != 0) {
				return false;
			}
		}
		
		return true;
	}
	
	private byte[] getVertexData() {
		final var voxels = chunk.getVoxels();
		final var vertices = new byte[Settings.CHUNK_VOLUME * 36 * 5];
		var index = 0;
		
		final var vectors = new Vector3i[] {
			new Vector3i(),
			new Vector3i(),
			new Vector3i(),
			new Vector3i(),
		};
		
		final var orderedVectors = new Vector3i[6];
		
		for (var x = 0; x < Settings.CHUNK_SIZE; ++x) {
			for (var y = 0; y < Settings.CHUNK_HEIGHT; ++y) {
				for (var z = 0; z < Settings.CHUNK_SIZE; ++z) {
					// System.out.println("x=%s y=%s z=%s index=%d".formatted(x, y, z, index));
					final var voxelId = voxels[toVoxelIndex(x, y, z)];
					
					if (voxelId == 0) {
						continue;
					}
					
					/* top face */
					if (isVoid(x, y + 1, z, voxels)) {
						// @formatter:off
						vectors[0].set(x    , y + 1, z    );
						vectors[1].set(x + 1, y + 1, z    );
						vectors[2].set(x + 1, y + 1, z + 1);
						vectors[3].set(x    , y + 1, z + 1);
						// @formatter:on
						
						orderedVectors[0] = vectors[0];
						orderedVectors[1] = vectors[3];
						orderedVectors[2] = vectors[2];
						orderedVectors[3] = vectors[0];
						orderedVectors[4] = vectors[2];
						orderedVectors[5] = vectors[1];
						
						index = addData(vertices, index, voxelId, Face.TOP, orderedVectors);
						// System.out.println("top index=%d".formatted(index));
					}
					
					/* bottom face */
					if (isVoid(x, y - 1, z, voxels)) {
						// @formatter:off
						vectors[0].set(x    , y    , z    );
						vectors[1].set(x + 1, y    , z    );
						vectors[2].set(x + 1, y    , z + 1);
						vectors[3].set(x    , y    , z + 1);
						// @formatter:on
						
						orderedVectors[0] = vectors[0];
						orderedVectors[1] = vectors[2];
						orderedVectors[2] = vectors[3];
						orderedVectors[3] = vectors[0];
						orderedVectors[4] = vectors[1];
						orderedVectors[5] = vectors[2];
						
						index = addData(vertices, index, voxelId, Face.BOTTOM, orderedVectors);
						// System.out.println("bottom index=%d".formatted(index));
					}
					
					/* right face */
					if (isVoid(x + 1, y, z, voxels)) {
						// @formatter:off
						vectors[0].set(x + 1, y    , z    );
						vectors[1].set(x + 1, y + 1, z    );
						vectors[2].set(x + 1, y + 1, z + 1);
						vectors[3].set(x + 1, y    , z + 1);
						// @formatter:on
						
						orderedVectors[0] = vectors[0];
						orderedVectors[1] = vectors[1];
						orderedVectors[2] = vectors[2];
						orderedVectors[3] = vectors[0];
						orderedVectors[4] = vectors[2];
						orderedVectors[5] = vectors[3];
						
						index = addData(vertices, index, voxelId, Face.RIGHT, orderedVectors);
						// System.out.println("right index=%d".formatted(index));
					}
					
					/* left face */
					if (isVoid(x - 1, y, z, voxels)) {
						// @formatter:off
						vectors[0].set(x    , y    , z    );
						vectors[1].set(x    , y + 1, z    );
						vectors[2].set(x    , y + 1, z + 1);
						vectors[3].set(x    , y    , z + 1);
						// @formatter:on
						
						orderedVectors[0] = vectors[0];
						orderedVectors[1] = vectors[2];
						orderedVectors[2] = vectors[1];
						orderedVectors[3] = vectors[0];
						orderedVectors[4] = vectors[3];
						orderedVectors[5] = vectors[2];
						
						index = addData(vertices, index, voxelId, Face.LEFT, orderedVectors);
						// System.out.println("left index=%d".formatted(index));
					}
					
					/* back face */
					if (isVoid(x, y, z - 1, voxels)) {
						// @formatter:off
						vectors[0].set(x    , y    , z    );
						vectors[1].set(x    , y + 1, z    );
						vectors[2].set(x + 1, y + 1, z    );
						vectors[3].set(x + 1, y    , z    );
						// @formatter:on
						
						orderedVectors[0] = vectors[0];
						orderedVectors[1] = vectors[1];
						orderedVectors[2] = vectors[2];
						orderedVectors[3] = vectors[0];
						orderedVectors[4] = vectors[2];
						orderedVectors[5] = vectors[3];
						
						index = addData(vertices, index, voxelId, Face.BACK, orderedVectors);
						// System.out.println("back index=%d".formatted(index));
					}
					
					/* front face */
					if (isVoid(x, y, z + 1, voxels)) {
						// @formatter:off
						vectors[0].set(x    , y    , z + 1);
						vectors[1].set(x    , y + 1, z + 1);
						vectors[2].set(x + 1, y + 1, z + 1);
						vectors[3].set(x + 1, y    , z + 1);
						// @formatter:on
						
						orderedVectors[0] = vectors[0];
						orderedVectors[1] = vectors[2];
						orderedVectors[2] = vectors[1];
						orderedVectors[3] = vectors[0];
						orderedVectors[4] = vectors[3];
						orderedVectors[5] = vectors[2];
						
						index = addData(vertices, index, voxelId, Face.FRONT, orderedVectors);
						// System.out.println("front index=%d".formatted(index));
					}
				}
			}
		}
		
		final var trimmed = new byte[index];
		System.arraycopy(vertices, 0, trimmed, 0, trimmed.length);
		
		return trimmed;
	}
	
	private int addData(byte[] vertices, int index, byte voxelId, Face face, Vector3i[] vectors) {
		final var faceId = face.ordinal();
		
		for (final var vector : vectors) {
			vertices[index++] = (byte) vector.x;
			vertices[index++] = (byte) vector.y;
			vertices[index++] = (byte) vector.z;
			vertices[index++] = voxelId;
			vertices[index++] = (byte) faceId;
		}
		
		return index;
	}
	
	private boolean isInChunk(int value) {
		return 0 <= value && value < Settings.CHUNK_SIZE;
	}
	
	private boolean isInChunkHeight(int value) {
		return 0 <= value && value < Settings.CHUNK_HEIGHT;
	}

	private int toVoxelIndex(int x, int y, int z) {
		return (z * Settings.CHUNK_SIZE * Settings.CHUNK_HEIGHT) + (y * Settings.CHUNK_SIZE) + x;
	}
	
}
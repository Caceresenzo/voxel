package voxel.mesh;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import org.joml.Vector4i;

import engine.util.OpenGL;
import engine.vertex.BufferType;
import engine.vertex.UsageType;
import engine.vertex.VertexArray;
import engine.vertex.VertexBuffer;
import voxel.Chunk;
import voxel.Face;
import voxel.Settings;
import voxel.World;

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
		
		final var stride = 6;
		if (triangleCount % stride != 0) {
			throw new IllegalStateException("invalid stride");
		}
		
		program.position.link(stride, 0);
		program.voxelId.link(stride, 3);
		program.faceId.link(stride, 4);
		program.ambiantOcclusionId.link(stride, 5);
		
		triangleCount = vertexData.length / stride;
		
		return array;
	}
	
	public void render() {
		program.use();
		vertexArray.bind();
		
		glDrawArrays(GL_TRIANGLES, 0, triangleCount);
		OpenGL.checkErrors();
	}
	
	private int getChunkIndex(int worldX, int worldY, int worldZ) {
		final var chunkX = worldX / Settings.CHUNK_SIZE;
		final var chunkY = worldY / Settings.CHUNK_SIZE;
		final var chunkZ = worldZ / Settings.CHUNK_SIZE;
		
		if (!((0 <= chunkX && chunkX < Settings.WORLD_WIDTH)
			&& (0 <= chunkY && chunkY < Settings.WORLD_HEIGHT)
			&& (0 <= chunkZ && chunkZ < Settings.WORLD_DEPTH))) {
			return -1;
		}
		
		return World.toChunkIndex(chunkX, chunkY, chunkZ);
	}
	
	private boolean isVoid(int x, int y, int z, int worldX, int worldY, int worldZ, byte[][] worldVoxels) {
		final var chunkIndex = getChunkIndex(worldX, worldY, worldZ);
		if (chunkIndex == -1) {
			return false;
		}
		
		final var chunkVoxels = worldVoxels[chunkIndex];
		
		final var index = toVoxelIndex(
			positiveMod(x, Settings.CHUNK_SIZE),
			positiveMod(y, Settings.CHUNK_SIZE),
			positiveMod(z, Settings.CHUNK_SIZE)
		);
		
		if (chunkVoxels[index] != 0) {
			return false;
		}
		
		return true;
	}
	
	public record AmbiantOcclusion(
		int _0,
		int _1,
		int _2,
		int _3
	) {
		
		public AmbiantOcclusion(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h) {
			this(add(a, b, c), add(g, h, a), add(e, f, g), add(c, d, e));
		}
		
		private static int add(boolean a, boolean b, boolean c) {
			int x = 0;
			
			if (a) {
				++x;
			}
			
			if (b) {
				++x;
			}
			
			if (c) {
				++x;
			}
			
			return x;
		}
		
		public boolean flipId() {
			return _1 + _3 > _0 + _2;
		}
		
	}
	
	private AmbiantOcclusion getAmbiantOcclusion(int x, int y, int z, int worldX, int worldY, int worldZ, byte[][] worldVoxels, Plane plane) {
		final boolean a, b, c, d, e, f, g, h;
		
		switch (plane) {
			case X: {
				// @formatter:off
				a = isVoid(x    , y    , z - 1, worldX    , worldY    , worldZ - 1, worldVoxels);
				b = isVoid(x    , y - 1, z - 1, worldX    , worldY - 1, worldZ - 1, worldVoxels);
				c = isVoid(x    , y - 1, z    , worldX    , worldY - 1, worldZ    , worldVoxels);
				d = isVoid(x    , y - 1, z + 1, worldX    , worldY - 1, worldZ + 1, worldVoxels);
				e = isVoid(x    , y    , z + 1, worldX    , worldY    , worldZ + 1, worldVoxels);
				f = isVoid(x    , y + 1, z + 1, worldX    , worldY + 1, worldZ + 1, worldVoxels);
				g = isVoid(x    , y + 1, z    , worldX    , worldY + 1, worldZ    , worldVoxels);
				h = isVoid(x    , y + 1, z - 1, worldX    , worldY + 1, worldZ - 1, worldVoxels);
				// @formatter:on
				break;
			}
			
			case Y: {
				// @formatter:off
				a = isVoid(x    , y    , z - 1, worldX    , worldY    , worldZ - 1, worldVoxels);
				b = isVoid(x - 1, y    , z - 1, worldX - 1, worldY    , worldZ - 1, worldVoxels);
				c = isVoid(x - 1, y    , z    , worldX - 1, worldY    , worldZ    , worldVoxels);
				d = isVoid(x - 1, y    , z + 1, worldX - 1, worldY    , worldZ + 1, worldVoxels);
				e = isVoid(x    , y    , z + 1, worldX    , worldY    , worldZ + 1, worldVoxels);
				f = isVoid(x + 1, y    , z + 1, worldX + 1, worldY    , worldZ + 1, worldVoxels);
				g = isVoid(x + 1, y    , z    , worldX + 1, worldY    , worldZ    , worldVoxels);
				h = isVoid(x + 1, y    , z - 1, worldX + 1, worldY    , worldZ - 1, worldVoxels);
				// @formatter:on
				break;
			}
			
			case Z: {
				// @formatter:off
				a = isVoid(x - 1, y    , z    , worldX - 1, worldY    , worldZ    , worldVoxels);
				b = isVoid(x - 1, y - 1, z    , worldX - 1, worldY - 1, worldZ    , worldVoxels);
				c = isVoid(x    , y - 1, z    , worldX    , worldY - 1, worldZ    , worldVoxels);
				d = isVoid(x + 1, y - 1, z    , worldX + 1, worldY - 1, worldZ    , worldVoxels);
				e = isVoid(x + 1, y    , z    , worldX + 1, worldY    , worldZ    , worldVoxels);
				f = isVoid(x + 1, y + 1, z    , worldX + 1, worldY + 1, worldZ    , worldVoxels);
				g = isVoid(x    , y + 1, z    , worldX    , worldY + 1, worldZ    , worldVoxels);
				h = isVoid(x - 1, y + 1, z    , worldX - 1, worldY + 1, worldZ    , worldVoxels);
				// @formatter:on
				break;
			}
			
			default: {
				throw new IllegalStateException("unknown plane: " + plane);
			}
		}
		
		return new AmbiantOcclusion(a, b, c, d, e, f, g, h);
	}
	
	private byte[] getVertexData() {
		final var voxels = chunk.getVoxels();
		final var vertices = new byte[Settings.CHUNK_VOLUME * 36 * 5];
		var index = 0;
		
		final var vectors = new Vector4i[] {
			new Vector4i(),
			new Vector4i(),
			new Vector4i(),
			new Vector4i(),
		};
		
		final var orderedVectors = new Vector4i[6];
		
		final var chunkX = chunk.getPosition().x;
		final var chunkY = chunk.getPosition().y;
		final var chunkZ = chunk.getPosition().z;
		final var worldVoxels = chunk.getWorld().getVoxels();
		
		for (var x = 0; x < Settings.CHUNK_SIZE; ++x) {
			for (var y = 0; y < Settings.CHUNK_HEIGHT; ++y) {
				for (var z = 0; z < Settings.CHUNK_SIZE; ++z) {
					// System.out.println("x=%s y=%s z=%s index=%d".formatted(x, y, z, index));
					final var voxelId = voxels[toVoxelIndex(x, y, z)];
					
					if (voxelId == 0) {
						continue;
					}
					
					final var worldX = x + chunkX * Settings.CHUNK_SIZE;
					final var worldY = y + chunkY * Settings.CHUNK_SIZE;
					final var worldZ = z + chunkZ * Settings.CHUNK_SIZE;
					
					/* top face */
					if (isVoid(x, y + 1, z, worldX, worldY + 1, worldZ, worldVoxels)) {
						final var ambiantOcclusion = getAmbiantOcclusion(x, y + 1, z, worldX, worldY + 1, worldZ, worldVoxels, Plane.Y);
						
						// @formatter:off
						vectors[0].set(x    , y + 1, z    , ambiantOcclusion._0);
						vectors[1].set(x + 1, y + 1, z    , ambiantOcclusion._1);
						vectors[2].set(x + 1, y + 1, z + 1, ambiantOcclusion._2);
						vectors[3].set(x    , y + 1, z + 1, ambiantOcclusion._3);
						// @formatter:on
						
						if (ambiantOcclusion.flipId()) {
							orderedVectors[0] = vectors[1];
							orderedVectors[1] = vectors[0];
							orderedVectors[2] = vectors[3];
							orderedVectors[3] = vectors[1];
							orderedVectors[4] = vectors[3];
							orderedVectors[5] = vectors[2];
						} else {
							orderedVectors[0] = vectors[0];
							orderedVectors[1] = vectors[3];
							orderedVectors[2] = vectors[2];
							orderedVectors[3] = vectors[0];
							orderedVectors[4] = vectors[2];
							orderedVectors[5] = vectors[1];
						}
						
						index = addData(vertices, index, voxelId, Face.TOP, orderedVectors);
						// System.out.println("top index=%d".formatted(index));
					}
					
					/* bottom face */
					if (isVoid(x, y - 1, z, worldX, worldY - 1, worldZ, worldVoxels)) {
						final var ambiantOcclusion = getAmbiantOcclusion(x, y - 1, z, worldX, worldY - 1, worldZ, worldVoxels, Plane.Y);
						
						// @formatter:off
						vectors[0].set(x    , y    , z    , ambiantOcclusion._0);
						vectors[1].set(x + 1, y    , z    , ambiantOcclusion._1);
						vectors[2].set(x + 1, y    , z + 1, ambiantOcclusion._2);
						vectors[3].set(x    , y    , z + 1, ambiantOcclusion._3);
						// @formatter:on
						
						if (ambiantOcclusion.flipId()) {
							orderedVectors[0] = vectors[1];
							orderedVectors[1] = vectors[3];
							orderedVectors[2] = vectors[0];
							orderedVectors[3] = vectors[1];
							orderedVectors[4] = vectors[2];
							orderedVectors[5] = vectors[3];
						} else {
							orderedVectors[0] = vectors[0];
							orderedVectors[1] = vectors[2];
							orderedVectors[2] = vectors[3];
							orderedVectors[3] = vectors[0];
							orderedVectors[4] = vectors[1];
							orderedVectors[5] = vectors[2];
						}
						
						index = addData(vertices, index, voxelId, Face.BOTTOM, orderedVectors);
						// System.out.println("bottom index=%d".formatted(index));
					}
					
					/* right face */
					if (isVoid(x + 1, y, z, worldX + 1, worldY, worldZ, worldVoxels)) {
						final var ambiantOcclusion = getAmbiantOcclusion(x + 1, y, z, worldX + 1, worldY, worldZ, worldVoxels, Plane.X);
						
						// @formatter:off
						vectors[0].set(x + 1, y    , z    , ambiantOcclusion._0);
						vectors[1].set(x + 1, y + 1, z    , ambiantOcclusion._1);
						vectors[2].set(x + 1, y + 1, z + 1, ambiantOcclusion._2);
						vectors[3].set(x + 1, y    , z + 1, ambiantOcclusion._3);
						// @formatter:on
						
						if (ambiantOcclusion.flipId()) {
							orderedVectors[0] = vectors[3];
							orderedVectors[1] = vectors[0];
							orderedVectors[2] = vectors[1];
							orderedVectors[3] = vectors[3];
							orderedVectors[4] = vectors[1];
							orderedVectors[5] = vectors[2];
						} else {
							orderedVectors[0] = vectors[0];
							orderedVectors[1] = vectors[1];
							orderedVectors[2] = vectors[2];
							orderedVectors[3] = vectors[0];
							orderedVectors[4] = vectors[2];
							orderedVectors[5] = vectors[3];
						}
						
						index = addData(vertices, index, voxelId, Face.RIGHT, orderedVectors);
						// System.out.println("right index=%d".formatted(index));
					}
					
					/* left face */
					if (isVoid(x - 1, y, z, worldX - 1, worldY, worldZ, worldVoxels)) {
						final var ambiantOcclusion = getAmbiantOcclusion(x - 1, y, z, worldX - 1, worldY, worldZ, worldVoxels, Plane.X);
						
						// @formatter:off
						vectors[0].set(x    , y    , z    , ambiantOcclusion._0);
						vectors[1].set(x    , y + 1, z    , ambiantOcclusion._1);
						vectors[2].set(x    , y + 1, z + 1, ambiantOcclusion._2);
						vectors[3].set(x    , y    , z + 1, ambiantOcclusion._3);
						// @formatter:on
						
						if (ambiantOcclusion.flipId()) {
							orderedVectors[0] = vectors[3];
							orderedVectors[1] = vectors[1];
							orderedVectors[2] = vectors[0];
							orderedVectors[3] = vectors[3];
							orderedVectors[4] = vectors[2];
							orderedVectors[5] = vectors[1];
						} else {
							orderedVectors[0] = vectors[0];
							orderedVectors[1] = vectors[2];
							orderedVectors[2] = vectors[1];
							orderedVectors[3] = vectors[0];
							orderedVectors[4] = vectors[3];
							orderedVectors[5] = vectors[2];
						}
						
						index = addData(vertices, index, voxelId, Face.LEFT, orderedVectors);
						// System.out.println("left index=%d".formatted(index));
					}
					
					/* back face */
					if (isVoid(x, y, z - 1, worldX, worldY, worldZ - 1, worldVoxels)) {
						final var ambiantOcclusion = getAmbiantOcclusion(x, y, z - 1, worldX, worldY, worldZ - 1, worldVoxels, Plane.Z);
						
						// @formatter:off
						vectors[0].set(x    , y    , z    , ambiantOcclusion._0);
						vectors[1].set(x    , y + 1, z    , ambiantOcclusion._1);
						vectors[2].set(x + 1, y + 1, z    , ambiantOcclusion._2);
						vectors[3].set(x + 1, y    , z    , ambiantOcclusion._3);
						// @formatter:on
						
						if (ambiantOcclusion.flipId()) {
							orderedVectors[0] = vectors[3];
							orderedVectors[1] = vectors[0];
							orderedVectors[2] = vectors[1];
							orderedVectors[3] = vectors[3];
							orderedVectors[4] = vectors[1];
							orderedVectors[5] = vectors[2];
						} else {
							orderedVectors[0] = vectors[0];
							orderedVectors[1] = vectors[1];
							orderedVectors[2] = vectors[2];
							orderedVectors[3] = vectors[0];
							orderedVectors[4] = vectors[2];
							orderedVectors[5] = vectors[3];
						}
						
						index = addData(vertices, index, voxelId, Face.BACK, orderedVectors);
						// System.out.println("back index=%d".formatted(index));
					}
					
					/* front face */
					if (isVoid(x, y, z + 1, worldX, worldY, worldZ + 1, worldVoxels)) {
						final var ambiantOcclusion = getAmbiantOcclusion(x, y, z + 1, worldX, worldY, worldZ + 1, worldVoxels, Plane.Z);
						
						// @formatter:off
						vectors[0].set(x    , y    , z + 1, ambiantOcclusion._0);
						vectors[1].set(x    , y + 1, z + 1, ambiantOcclusion._1);
						vectors[2].set(x + 1, y + 1, z + 1, ambiantOcclusion._2);
						vectors[3].set(x + 1, y    , z + 1, ambiantOcclusion._3);
						// @formatter:on
						
						if (ambiantOcclusion.flipId()) {
							orderedVectors[0] = vectors[3];
							orderedVectors[1] = vectors[1];
							orderedVectors[2] = vectors[0];
							orderedVectors[3] = vectors[3];
							orderedVectors[4] = vectors[2];
							orderedVectors[5] = vectors[1];
						} else {
							orderedVectors[0] = vectors[0];
							orderedVectors[1] = vectors[2];
							orderedVectors[2] = vectors[1];
							orderedVectors[3] = vectors[0];
							orderedVectors[4] = vectors[3];
							orderedVectors[5] = vectors[2];
						}
						
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
	
	private int addData(byte[] vertices, int index, byte voxelId, Face face, Vector4i[] vectors) {
		final var faceId = face.ordinal();
		
		for (final var vector : vectors) {
			vertices[index++] = (byte) vector.x;
			vertices[index++] = (byte) vector.y;
			vertices[index++] = (byte) vector.z;
			vertices[index++] = voxelId;
			vertices[index++] = (byte) faceId;
			vertices[index++] = (byte) vector.w;
		}
		
		return index;
	}
	
	private int toVoxelIndex(int x, int y, int z) {
		return (z * Settings.CHUNK_SIZE * Settings.CHUNK_HEIGHT) + (y * Settings.CHUNK_SIZE) + x;
	}
	
	public static int positiveMod(int dividend, int divisor) {
		return (dividend % divisor + divisor) % divisor;
	}
	
}
package voxel.mesh;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import engine.vertex.BufferType;
import engine.vertex.UsageType;
import engine.vertex.VertexArray;
import engine.vertex.VertexBuffer;
import voxel.Chunk;
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

		createVertexArray();
	}

	private void createVertexArray() {
		final var vertexData = getVertexData();

		final var buffer = new VertexBuffer(BufferType.ARRAY, UsageType.STATIC_DRAW);
		buffer.store(vertexData);

		final var array = new VertexArray().add(buffer);

		program.linkAttributes(array);
		
		this.triangleCount = vertexData.length;
		this.vertexArray = array;
	}

	public void render() {
		program.use();
		vertexArray.bind();

		glDrawArrays(GL_TRIANGLES, 0, triangleCount);
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

	public static final int ambiantOcclusionId_shift = 1;
	public static final int faceId_shift = ambiantOcclusionId_shift + 2;
	public static final int voxelId_shift = faceId_shift + 3;
	public static final int z_shift = voxelId_shift + 8;
	public static final int y_shift = z_shift + 6;
	public static final int x_shift = y_shift + 6;

	public static int packData(int x, int y, int z, int voxelId, int faceId, int ambiantOcclusionId, int flipId) {
		return ((x << x_shift)
			| (y << y_shift)
			| (z << z_shift)
			| (voxelId << voxelId_shift)
			| (faceId << faceId_shift)
			| (ambiantOcclusionId << ambiantOcclusionId_shift)
			| (flipId));
	}
	
//	static int[] vertices = new int[Settings.CHUNK_VOLUME * 36];

	private int[] getVertexData() {
		var vertices = new int[Settings.CHUNK_VOLUME * 36];
		final var voxels = chunk.getVoxels();
		var index = 0;

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
						final var faceId = Face.TOP.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y + 1, z, worldX, worldY + 1, worldZ, worldVoxels, Plane.Y);
						final var flipId = ambiantOcclusion.flipId();

						// @formatter:off
						int v0 = packData(x    , y + 1, z    , voxelId, faceId, ambiantOcclusion._0(), flipId);
						int v1 = packData(x + 1, y + 1, z    , voxelId, faceId, ambiantOcclusion._1(), flipId);
						int v2 = packData(x + 1, y + 1, z + 1, voxelId, faceId, ambiantOcclusion._2(), flipId);
						int v3 = packData(x    , y + 1, z + 1, voxelId, faceId, ambiantOcclusion._3(), flipId);
						// @formatter:on

						if (ambiantOcclusion.flip()) {
							vertices[index++] = v1;
							vertices[index++] = v0;
							vertices[index++] = v3;
							vertices[index++] = v1;
							vertices[index++] = v3;
							vertices[index++] = v2;
						} else {
							vertices[index++] = v0;
							vertices[index++] = v3;
							vertices[index++] = v2;
							vertices[index++] = v0;
							vertices[index++] = v2;
							vertices[index++] = v1;
						}
					}

					/* bottom face */
					if (isVoid(x, y - 1, z, worldX, worldY - 1, worldZ, worldVoxels)) {
						final var faceId = Face.BOTTOM.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y - 1, z, worldX, worldY - 1, worldZ, worldVoxels, Plane.Y);

						// @formatter:off
						final var flipId = ambiantOcclusion.flipId();
						int v0 = packData(x    , y    , z    , voxelId, faceId, ambiantOcclusion._0(), flipId);
						int v1 = packData(x + 1, y    , z    , voxelId, faceId, ambiantOcclusion._1(), flipId);
						int v2 = packData(x + 1, y    , z + 1, voxelId, faceId, ambiantOcclusion._2(), flipId);
						int v3 = packData(x    , y    , z + 1, voxelId, faceId, ambiantOcclusion._3(), flipId);
						// @formatter:on

						if (ambiantOcclusion.flip()) {
							vertices[index++] = v1;
							vertices[index++] = v3;
							vertices[index++] = v0;
							vertices[index++] = v1;
							vertices[index++] = v2;
							vertices[index++] = v3;
						} else {
							vertices[index++] = v0;
							vertices[index++] = v2;
							vertices[index++] = v3;
							vertices[index++] = v0;
							vertices[index++] = v1;
							vertices[index++] = v2;
						}
					}

					/* right face */
					if (isVoid(x + 1, y, z, worldX + 1, worldY, worldZ, worldVoxels)) {
						final var faceId = Face.RIGHT.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x + 1, y, z, worldX + 1, worldY, worldZ, worldVoxels, Plane.X);

						// @formatter:off
						final var flipId = ambiantOcclusion.flipId();
						int v0 = packData(x + 1, y    , z    , voxelId, faceId, ambiantOcclusion._0(), flipId);
						int v1 = packData(x + 1, y + 1, z    , voxelId, faceId, ambiantOcclusion._1(), flipId);
						int v2 = packData(x + 1, y + 1, z + 1, voxelId, faceId, ambiantOcclusion._2(), flipId);
						int v3 = packData(x + 1, y    , z + 1, voxelId, faceId, ambiantOcclusion._3(), flipId);
						// @formatter:on

						if (ambiantOcclusion.flip()) {
							vertices[index++] = v3;
							vertices[index++] = v0;
							vertices[index++] = v1;
							vertices[index++] = v3;
							vertices[index++] = v1;
							vertices[index++] = v2;
						} else {
							vertices[index++] = v0;
							vertices[index++] = v1;
							vertices[index++] = v2;
							vertices[index++] = v0;
							vertices[index++] = v2;
							vertices[index++] = v3;
						}
					}

					/* left face */
					if (isVoid(x - 1, y, z, worldX - 1, worldY, worldZ, worldVoxels)) {
						final var faceId = Face.LEFT.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x - 1, y, z, worldX - 1, worldY, worldZ, worldVoxels, Plane.X);

						// @formatter:off
						final var flipId = ambiantOcclusion.flipId();
						int v0 = packData(x    , y    , z    , voxelId, faceId, ambiantOcclusion._0(), flipId);
						int v1 = packData(x    , y + 1, z    , voxelId, faceId, ambiantOcclusion._1(), flipId);
						int v2 = packData(x    , y + 1, z + 1, voxelId, faceId, ambiantOcclusion._2(), flipId);
						int v3 = packData(x    , y    , z + 1, voxelId, faceId, ambiantOcclusion._3(), flipId);
						// @formatter:on

						if (ambiantOcclusion.flip()) {
							vertices[index++] = v3;
							vertices[index++] = v1;
							vertices[index++] = v0;
							vertices[index++] = v3;
							vertices[index++] = v2;
							vertices[index++] = v1;
						} else {
							vertices[index++] = v0;
							vertices[index++] = v2;
							vertices[index++] = v1;
							vertices[index++] = v0;
							vertices[index++] = v3;
							vertices[index++] = v2;
						}
					}

					/* back face */
					if (isVoid(x, y, z - 1, worldX, worldY, worldZ - 1, worldVoxels)) {
						final var faceId = Face.BACK.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y, z - 1, worldX, worldY, worldZ - 1, worldVoxels, Plane.Z);

						// @formatter:off
						final var flipId = ambiantOcclusion.flipId();
						int v0 = packData(x    , y    , z    , voxelId, faceId, ambiantOcclusion._0(), flipId);
						int v1 = packData(x    , y + 1, z    , voxelId, faceId, ambiantOcclusion._1(), flipId);
						int v2 = packData(x + 1, y + 1, z    , voxelId, faceId, ambiantOcclusion._2(), flipId);
						int v3 = packData(x + 1, y    , z    , voxelId, faceId, ambiantOcclusion._3(), flipId);
						// @formatter:on

						if (ambiantOcclusion.flip()) {
							vertices[index++] = v3;
							vertices[index++] = v0;
							vertices[index++] = v1;
							vertices[index++] = v3;
							vertices[index++] = v1;
							vertices[index++] = v2;
						} else {
							vertices[index++] = v0;
							vertices[index++] = v1;
							vertices[index++] = v2;
							vertices[index++] = v0;
							vertices[index++] = v2;
							vertices[index++] = v3;
						}
					}

					/* front face */
					if (isVoid(x, y, z + 1, worldX, worldY, worldZ + 1, worldVoxels)) {
						final var faceId = Face.FRONT.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y, z + 1, worldX, worldY, worldZ + 1, worldVoxels, Plane.Z);

						// @formatter:off
						final var flipId = ambiantOcclusion.flipId();
						int v0 = packData(x    , y    , z + 1, voxelId, faceId, ambiantOcclusion._0(), flipId);
						int v1 = packData(x    , y + 1, z + 1, voxelId, faceId, ambiantOcclusion._1(), flipId);
						int v2 = packData(x + 1, y + 1, z + 1, voxelId, faceId, ambiantOcclusion._2(), flipId);
						int v3 = packData(x + 1, y    , z + 1, voxelId, faceId, ambiantOcclusion._3(), flipId);
						// @formatter:on

						if (ambiantOcclusion.flip()) {
							vertices[index++] = v3;
							vertices[index++] = v1;
							vertices[index++] = v0;
							vertices[index++] = v3;
							vertices[index++] = v2;
							vertices[index++] = v1;
						} else {
							vertices[index++] = v0;
							vertices[index++] = v2;
							vertices[index++] = v1;
							vertices[index++] = v0;
							vertices[index++] = v3;
							vertices[index++] = v2;
						}
					}
				}
			}
		}

		final var trimmed = new int[index];
		System.arraycopy(vertices, 0, trimmed, 0, trimmed.length);

//		vertices = null;
//		System.gc();

		return trimmed;
	}

	private int toVoxelIndex(int x, int y, int z) {
		return (z * Settings.CHUNK_SIZE * Settings.CHUNK_HEIGHT) + (y * Settings.CHUNK_SIZE) + x;
	}

	public static int positiveMod(int dividend, int divisor) {
		return (dividend % divisor + divisor) % divisor;
	}

}
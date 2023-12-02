package voxel.client.chunk;

import org.joml.Matrix4f;

import opengl.vertex.BufferType;
import opengl.vertex.UsageType;
import opengl.vertex.VertexArray;
import opengl.vertex.VertexBuffer;
import voxel.client.Settings;
import voxel.shared.chunk.ChunkConstants;
import voxel.shared.chunk.ChunkKey;
import voxel.shared.chunk.Face;
import voxel.shared.chunk.Plane;

public class ChunkMesh {

	private final Chunk chunk;
	private final ChunkShaderProgram shaderProgram;
	private final ChunkManager chunkManager;
	private VertexArray vertexArray;
	private Matrix4f modelMatrix;

	public ChunkMesh(Chunk chunk, ChunkShaderProgram shaderProgram) {
		this.chunk = chunk;
		this.shaderProgram = shaderProgram;
		this.chunkManager = chunk.getWorld().getChunkManager();
		this.modelMatrix = computeModelMatrix();

		createVertexArray();
	}

	private Matrix4f computeModelMatrix() {
		return new Matrix4f().translate(
			chunk.getWorldX(),
			0,
			chunk.getWorldZ()
		);
	}

	private void createVertexArray() {
		final var vertexData = getVertexData();

		final var buffer = new VertexBuffer(BufferType.ARRAY, UsageType.STATIC_DRAW);
		buffer.store(vertexData);

		final var array = new VertexArray(shaderProgram);
		array.add(buffer);

		if (vertexArray != null) {
			vertexArray.delete(true);
		}

		this.vertexArray = array;
	}

	public void rebuild() {
		createVertexArray();
	}

	public void render() {
		shaderProgram.use();
		shaderProgram.model.load(modelMatrix);
		vertexArray.render();
	}

	private boolean isVoid(int x, int y, int z, int worldX, int worldY, int worldZ) {
		final var chunk = chunkManager.get(ChunkKey.of(worldX / 16, worldZ / 16));
		if (chunk == null) {
			return true;
		}
		
		final var voxelId = chunk.getVoxelId(
			positiveMod(x, ChunkConstants.WIDTH),
			positiveMod(y, ChunkConstants.DEPTH),
			positiveMod(z, ChunkConstants.HEIGHT)
		);

		return voxelId == 0;
	}

	private AmbiantOcclusion getAmbiantOcclusion(int x, int y, int z, int worldX, int worldY, int worldZ, Plane plane) {
		final boolean a, b, c, d, e, f, g, h;

		switch (plane) {
			case X: {
				// @formatter:off
				a = isVoid(x    , y    , z - 1, worldX    , worldY    , worldZ - 1);
				b = isVoid(x    , y - 1, z - 1, worldX    , worldY - 1, worldZ - 1);
				c = isVoid(x    , y - 1, z    , worldX    , worldY - 1, worldZ    );
				d = isVoid(x    , y - 1, z + 1, worldX    , worldY - 1, worldZ + 1);
				e = isVoid(x    , y    , z + 1, worldX    , worldY    , worldZ + 1);
				f = isVoid(x    , y + 1, z + 1, worldX    , worldY + 1, worldZ + 1);
				g = isVoid(x    , y + 1, z    , worldX    , worldY + 1, worldZ    );
				h = isVoid(x    , y + 1, z - 1, worldX    , worldY + 1, worldZ - 1);
				// @formatter:on
				break;
			}

			case Y: {
				// @formatter:off
				a = isVoid(x    , y    , z - 1, worldX    , worldY    , worldZ - 1);
				b = isVoid(x - 1, y    , z - 1, worldX - 1, worldY    , worldZ - 1);
				c = isVoid(x - 1, y    , z    , worldX - 1, worldY    , worldZ    );
				d = isVoid(x - 1, y    , z + 1, worldX - 1, worldY    , worldZ + 1);
				e = isVoid(x    , y    , z + 1, worldX    , worldY    , worldZ + 1);
				f = isVoid(x + 1, y    , z + 1, worldX + 1, worldY    , worldZ + 1);
				g = isVoid(x + 1, y    , z    , worldX + 1, worldY    , worldZ    );
				h = isVoid(x + 1, y    , z - 1, worldX + 1, worldY    , worldZ - 1);
				// @formatter:on
				break;
			}

			case Z: {
				// @formatter:off
				a = isVoid(x - 1, y    , z    , worldX - 1, worldY    , worldZ    );
				b = isVoid(x - 1, y - 1, z    , worldX - 1, worldY - 1, worldZ    );
				c = isVoid(x    , y - 1, z    , worldX    , worldY - 1, worldZ    );
				d = isVoid(x + 1, y - 1, z    , worldX + 1, worldY - 1, worldZ    );
				e = isVoid(x + 1, y    , z    , worldX + 1, worldY    , worldZ    );
				f = isVoid(x + 1, y + 1, z    , worldX + 1, worldY + 1, worldZ    );
				g = isVoid(x    , y + 1, z    , worldX    , worldY + 1, worldZ    );
				h = isVoid(x - 1, y + 1, z    , worldX - 1, worldY + 1, worldZ    );
				// @formatter:on
				break;
			}

			default: {
				throw new IllegalStateException("unknown plane: " + plane);
			}
		}

		return new AmbiantOcclusion(a, b, c, d, e, f, g, h);
	}

	private int[] getVertexData() {
		var vertices = new int[Settings.CHUNK_VOLUME * 36];
		var index = 0;

		final var chunkX = chunk.getX();
		final var chunkY = 0;
		final var chunkZ = chunk.getZ();

		for (var x = 0; x < Settings.CHUNK_SIZE; ++x) {
			for (var y = 0; y < Settings.CHUNK_HEIGHT; ++y) {
				for (var z = 0; z < Settings.CHUNK_SIZE; ++z) {
					// System.out.println("x=%s y=%s z=%s index=%d".formatted(x, y, z, index));
					final var voxelId = chunk.getVoxelId(x, y, z);
					if (voxelId == 0) {
						continue;
					}

					final var worldX = x + chunkX * Settings.CHUNK_SIZE;
					final var worldY = y + chunkY * Settings.CHUNK_SIZE;
					final var worldZ = z + chunkZ * Settings.CHUNK_SIZE;

					/* top face */
					if (isVoid(x, y + 1, z, worldX, worldY + 1, worldZ)) {
						final var faceId = Face.TOP.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y + 1, z, worldX, worldY + 1, worldZ, Plane.Y);
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
					if (isVoid(x, y - 1, z, worldX, worldY - 1, worldZ)) {
						final var faceId = Face.BOTTOM.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y - 1, z, worldX, worldY - 1, worldZ, Plane.Y);

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
					if (isVoid(x + 1, y, z, worldX + 1, worldY, worldZ)) {
						final var faceId = Face.RIGHT.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x + 1, y, z, worldX + 1, worldY, worldZ, Plane.X);

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
					if (isVoid(x - 1, y, z, worldX - 1, worldY, worldZ)) {
						final var faceId = Face.LEFT.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x - 1, y, z, worldX - 1, worldY, worldZ, Plane.X);

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
					if (isVoid(x, y, z - 1, worldX, worldY, worldZ - 1)) {
						final var faceId = Face.BACK.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y, z - 1, worldX, worldY, worldZ - 1, Plane.Z);

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
					if (isVoid(x, y, z + 1, worldX, worldY, worldZ + 1)) {
						final var faceId = Face.FRONT.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y, z + 1, worldX, worldY, worldZ + 1, Plane.Z);

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

		return trimmed;
	}
	
	public void delete() {
		vertexArray.delete(true);
	}

	public static int positiveMod(int dividend, int divisor) {
		return (dividend % divisor + divisor) % divisor;
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

}
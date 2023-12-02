package voxel.client.chunk;

import org.joml.Vector3i;

import opengl.vertex.BufferType;
import opengl.vertex.UsageType;
import opengl.vertex.VertexArray;
import opengl.vertex.VertexBuffer;
import voxel.client.Settings;
import voxel.shared.chunk.Face;
import voxel.shared.chunk.Plane;

public class ChunkMeshBuilder {

	public static final int AMBIANT_OCCLUSION_ID_SHIFT = 1;
	public static final int FACE_ID_SHIFT = AMBIANT_OCCLUSION_ID_SHIFT + 2;
	public static final int VOXEL_ID_SHIFT = FACE_ID_SHIFT + 3;
	public static final int Z_SHIFT = VOXEL_ID_SHIFT + 8;
	public static final int Y_SHIFT = Z_SHIFT + 6;
	public static final int X_SHIFT = Y_SHIFT + 6;

	private final Chunk chunk;
	private final CubicNeighborChunks neighbors;
	private final Vector3i faceRelative = new Vector3i();
	private final Vector3i adjustedPosition = new Vector3i();

	public ChunkMeshBuilder(Chunk chunk) {
		this.chunk = chunk;
		this.neighbors = CubicNeighborChunks.from(chunk);
	}

	public ChunkMesh build() {
		final var vertexData = getVertexData();

		final var buffer = new VertexBuffer(BufferType.ARRAY, UsageType.STATIC_DRAW);
		buffer.store(vertexData);

		final var array = new VertexArray(chunk.getShaderProgram());
		array.add(buffer);

		return new ChunkMesh(chunk, array);
	}

	public Chunk getChunk(int x, int y, int z) {
		adjustedPosition.set(x, y, z);
		faceRelative.zero();
		var ySign = 0;

		if (x < 0) {
			adjustedPosition.x += Chunk.WIDTH;
			faceRelative.x = -1;
		} else if (x >= Chunk.WIDTH) {
			adjustedPosition.x -= Chunk.WIDTH;
			faceRelative.x = 1;
		}

		if (y < 0) {
			adjustedPosition.y += Chunk.DEPTH;
			ySign = -1;
		} else if (y >= Chunk.DEPTH) {
			adjustedPosition.y -= Chunk.DEPTH;
			ySign = 1;
		}

		if (z < 0) {
			adjustedPosition.z += Chunk.HEIGHT;
			faceRelative.z = -1;
		} else if (z >= Chunk.HEIGHT) {
			adjustedPosition.z -= Chunk.HEIGHT;
			faceRelative.z = 1;
		}

		final var face = Face.from(faceRelative);
		return neighbors.getChunk(face, ySign);
	}

	public boolean isVoid(int x, int y, int z) {
		final var chunk = getChunk(x, y, z);
		if (chunk == null) {
			return true;
		}

		final var voxelId = chunk.getVoxelId(adjustedPosition.x, adjustedPosition.y, adjustedPosition.z);
		return voxelId == 0;
	}

	private AmbiantOcclusion getAmbiantOcclusion(int x, int y, int z, Plane plane) {
		final boolean a, b, c, d, e, f, g, h;

		switch (plane) {
			case X: {
				// @formatter:off
				a = isVoid(x    , y    , z - 1);
				b = isVoid(x    , y - 1, z - 1);
				c = isVoid(x    , y - 1, z    );
				d = isVoid(x    , y - 1, z + 1);
				e = isVoid(x    , y    , z + 1);
				f = isVoid(x    , y + 1, z + 1);
				g = isVoid(x    , y + 1, z    );
				h = isVoid(x    , y + 1, z - 1);
				// @formatter:on
				break;
			}

			case Y: {
				// @formatter:off
				a = isVoid(x    , y    , z - 1);
				b = isVoid(x - 1, y    , z - 1);
				c = isVoid(x - 1, y    , z    );
				d = isVoid(x - 1, y    , z + 1);
				e = isVoid(x    , y    , z + 1);
				f = isVoid(x + 1, y    , z + 1);
				g = isVoid(x + 1, y    , z    );
				h = isVoid(x + 1, y    , z - 1);
				// @formatter:on
				break;
			}

			case Z: {
				// @formatter:off
				a = isVoid(x - 1, y    , z    );
				b = isVoid(x - 1, y - 1, z    );
				c = isVoid(x    , y - 1, z    );
				d = isVoid(x + 1, y - 1, z    );
				e = isVoid(x + 1, y    , z    );
				f = isVoid(x + 1, y + 1, z    );
				g = isVoid(x    , y + 1, z    );
				h = isVoid(x - 1, y + 1, z    );
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

		for (var x = 0; x < Chunk.WIDTH; ++x) {
			for (var y = 0; y < Chunk.DEPTH; ++y) {
				for (var z = 0; z < Chunk.HEIGHT; ++z) {
					final var voxelId = chunk.getVoxelId(x, y, z);
					if (voxelId == 0) {
						continue;
					}

					/* top face */
					if (isVoid(x, y + 1, z)) {
						final var faceId = Face.UP.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y + 1, z, Plane.Y);
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
					if (isVoid(x, y - 1, z)) {
						final var faceId = Face.DOWN.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y - 1, z, Plane.Y);

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
					if (isVoid(x + 1, y, z)) {
						final var faceId = Face.EAST.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x + 1, y, z, Plane.X);

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
					if (isVoid(x - 1, y, z)) {
						final var faceId = Face.WEST.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x - 1, y, z, Plane.X);

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
					if (isVoid(x, y, z - 1)) {
						final var faceId = Face.NORTH.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y, z - 1, Plane.Z);

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
					if (isVoid(x, y, z + 1)) {
						final var faceId = Face.SOUTH.ordinal();
						final var ambiantOcclusion = getAmbiantOcclusion(x, y, z + 1, Plane.Z);

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

	public static int positiveMod(int dividend, int divisor) {
		return (dividend % divisor + divisor) % divisor;
	}

	public static int packData(int x, int y, int z, int voxelId, int faceId, int ambiantOcclusionId, int flipId) {
		return ((x << X_SHIFT)
			| (y << Y_SHIFT)
			| (z << Z_SHIFT)
			| (voxelId << VOXEL_ID_SHIFT)
			| (faceId << FACE_ID_SHIFT)
			| (ambiantOcclusionId << AMBIANT_OCCLUSION_ID_SHIFT)
			| (flipId));
	}

}
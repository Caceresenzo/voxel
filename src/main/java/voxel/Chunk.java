package voxel;

import org.joml.SimplexNoise;
import org.joml.Vector3f;
import org.joml.Vector3i;

import lombok.Getter;
import voxel.mesh.chunk.ChunkMesh;
import voxel.mesh.chunk.ChunkShaderProgram;

public class Chunk {

	private final ChunkShaderProgram shaderProgram;
	private final @Getter Vector3i position;
	private final @Getter World world;
	private @Getter byte[] voxels;
	private @Getter ChunkMesh mesh;
	private boolean isEmpty;
	private @Getter Vector3f center;

	public Chunk(ChunkShaderProgram shaderProgram, Vector3i position, World world) {
		this.shaderProgram = shaderProgram;
		this.position = position;
		this.world = world;
		this.isEmpty = true;

		this.center = new Vector3f(position).add(new Vector3f(0.5f)).mul(Settings.CHUNK_SIZE);
	}

	public void buildMesh() {
		mesh = new ChunkMesh(this, shaderProgram);
	}

	public void render(Camera camera) {
		if (isEmpty || !camera.getFrustum().contains(this)) {
			return;
		}

		mesh.render();
	}

	public byte[] buildVoxels() {
		byte[] voxels = new byte[Settings.CHUNK_VOLUME];

		final var chunkPosition = position.mul(Settings.CHUNK_SIZE, new Vector3i());

		for (var x = 0; x < Settings.CHUNK_SIZE; ++x) {
			for (var z = 0; z < Settings.CHUNK_SIZE; ++z) {
				final var worldX = x + chunkPosition.x;
				final var worldZ = z + chunkPosition.z;

				final var worldHeight = (int) (SimplexNoise.noise(worldX * 0.01f, worldZ * 0.01f) * 32 + 32);
				final var localHeight = Math.min(worldHeight - chunkPosition.y, Settings.CHUNK_SIZE);

				for (var y = 0; y < localHeight; ++y) {
					final var worldY = y + chunkPosition.y;
					final var index = toVoxelIndex(x, y, z);

					voxels[index] = (byte) 2;
//					voxels[index] = (byte) (worldY + 1);
//					voxels[index] = (byte) (position.x + position.y + position.z);
				}
			}
		}

		this.voxels = voxels;
		testIfEmpty();

		return voxels;
	}

	public void testIfEmpty() {
		for (final var voxel : voxels) {
			if (voxel != 0) {
				isEmpty = false;
				break;
			}
		}
	}

	public static int toVoxelIndex(Vector3i position) {
		return (position.z * Settings.CHUNK_SIZE * Settings.CHUNK_HEIGHT) + (position.y * Settings.CHUNK_SIZE) + position.x;
	}

	public static int toVoxelIndex(int x, int y, int z) {
		return (z * Settings.CHUNK_SIZE * Settings.CHUNK_HEIGHT) + (y * Settings.CHUNK_SIZE) + x;
	}

}
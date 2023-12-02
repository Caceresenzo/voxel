package voxel.server.chunk.generator;

import org.joml.SimplexNoise;
import org.joml.Vector3ic;

import voxel.server.chunk.Chunk;
import voxel.server.world.World;

public class SimplexNoiseChunkGenerator implements ChunkGenerator {

	@Override
	public void generate(World world, Vector3ic position, byte[] voxels) {
		final var chunkX = position.x() * Chunk.DEPTH;
		final var chunkY = position.y() * Chunk.DEPTH;
		final var chunkZ = position.z() * Chunk.DEPTH;

		for (var x = 0; x < Chunk.WIDTH; ++x) {
			for (var z = 0; z < Chunk.HEIGHT; ++z) {
				final var worldX = x + chunkX;
				final var worldZ = z + chunkZ;

				final var worldHeight = (int) (SimplexNoise.noise(worldX * 0.01f, worldZ * 0.01f) * 32 + 32);
				final var localHeight = Math.min(worldHeight - chunkY, Chunk.DEPTH);

				for (var y = 0; y < localHeight; ++y) {
					final var index = Chunk.index(x, y, z);

					if (y == localHeight - 1) {
						voxels[index] = (byte) 2;
					} else {
						voxels[index] = (byte) 3;
					}
				}
			}
		}
	}

}
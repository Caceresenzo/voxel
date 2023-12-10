package voxel.server.chunk.generator;

import org.joml.SimplexNoise;

import voxel.server.chunk.ServerChunk;
import voxel.server.world.World;
import voxel.shared.chunk.ChunkPosition;

public class SimplexNoiseChunkGenerator implements ChunkGenerator {

	@Override
	public void generate(World world, ChunkPosition position, byte[] voxels) {
		final var blockPosition = position.toBlockPosition();

		final var startX = blockPosition.x();
		final var startY = blockPosition.y();
		final var startZ = blockPosition.z();

		for (var x = 0; x < ServerChunk.WIDTH; ++x) {
			for (var z = 0; z < ServerChunk.HEIGHT; ++z) {
				final var worldX = x + startX;
				final var worldZ = z + startZ;

				final var worldHeight = (int) (SimplexNoise.noise(worldX * 0.01f, worldZ * 0.01f) * 32 + 32);
				final var localHeight = Math.min(worldHeight - startY, ServerChunk.DEPTH);

				for (var y = 0; y < localHeight; ++y) {
					final var index = ServerChunk.index(x, y, z);

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
package voxel.server.chunk.generator;

import org.joml.SimplexNoise;

import voxel.server.chunk.Chunk;
import voxel.server.world.World;
import voxel.shared.chunk.ChunkSection;

public class SimplexNoiseChunkGenerator implements ChunkGenerator {

	@Override
	public void generate(World world, int chunkX, int chunkZ, ChunkSection[] sections) {
		for (final var section : sections) {
			generate(world, chunkX, chunkZ, section);
		}
	}

	public void generate(World world, int chunkX, int chunkZ, ChunkSection section) {
		final var chunkStartY = section.getY() * Chunk.SECTION_DEPTH;

		for (var x = 0; x < Chunk.WIDTH; ++x) {
			for (var z = 0; z < Chunk.HEIGHT; ++z) {
				final var worldX = x + chunkX;
				final var worldZ = z + chunkZ;

				final var worldHeight = (int) (SimplexNoise.noise(worldX * 0.01f, worldZ * 0.01f) * 32 + 32);
				final var localHeight = Math.min(worldHeight - chunkStartY, Chunk.SECTION_DEPTH);

				for (var y = 0; y < localHeight; ++y) {
					if (y == localHeight - 1) {
						section.setType(x, y, z, (byte) 2);
					} else {
						section.setType(x, y, z, (byte) 3);
					}
				}
			}
		}
	}

}
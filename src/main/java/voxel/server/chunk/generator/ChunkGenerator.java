package voxel.server.chunk.generator;

import voxel.server.world.World;
import voxel.shared.chunk.ChunkSection;

public interface ChunkGenerator {

	void generate(World world, int chunkX, int chunkZ, ChunkSection[] sections);

}
package voxel.server.chunk.generator;

import voxel.server.world.World;
import voxel.shared.chunk.ChunkPosition;

public interface ChunkGenerator {

	void generate(World world, ChunkPosition position, byte[] voxels);

}
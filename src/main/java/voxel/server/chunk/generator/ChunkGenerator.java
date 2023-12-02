package voxel.server.chunk.generator;

import org.joml.Vector3ic;

import voxel.server.world.World;

public interface ChunkGenerator {

	void generate(World world, Vector3ic position, byte[] voxels);

}
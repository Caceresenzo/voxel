package voxel.server.chunk;

import lombok.Getter;
import voxel.server.world.World;
import voxel.shared.chunk.Chunk;
import voxel.shared.chunk.ChunkPosition;

public class ServerChunk extends Chunk {

	@Getter
	private final World world;

	ServerChunk(World world, ChunkPosition position) {
		super(position);

		this.world = world;
	}

	public boolean load() {
		return isLoaded() || world.getChunkManager().load(this);
	}

	public boolean unload() {
		if (!isLoaded()) {
			return true;
		}

		nonAirCount = 0;
		voxels = null;
		return true;
	}

}
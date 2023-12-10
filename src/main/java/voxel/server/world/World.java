package voxel.server.world;

import lombok.Getter;
import voxel.server.chunk.ChunkManager;
import voxel.server.chunk.ServerChunk;
import voxel.shared.chunk.ChunkPosition;

public class World {

	@Getter
	private final String name;

	@Getter
	private final ChunkManager chunkManager;

	public World(WorldCreator creator) {
		this.name = creator.getWorldName();
		this.chunkManager = new ChunkManager(this, creator.getChunkGenerator());
	}

	public ServerChunk getChunk(ChunkPosition key) {
		return chunkManager.get(key);
	}

	public void loadSpawnChunks(int radius) {
//		getChunk(new ChunkPosition(0, 0, 0)).load();
//		getChunk(new ChunkPosition(0, 1, 0)).load();

		if (radius == 0) {
			final var key = ChunkPosition.zero();
			getChunk(key).load();
		} else {
			for (var y = 0; y <= radius; ++y) {
				for (var x = -radius; x <= radius; ++x) {
					for (var z = -radius; z <= radius; ++z) {
						final var key = new ChunkPosition(x, y, z);
						System.out.println("load " + key);
						getChunk(key).load();
					}
				}
			}
		}
	}

}
package voxel.server.world;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import voxel.server.chunk.generator.ChunkGenerator;

@Data
@RequiredArgsConstructor
public class WorldCreator {

	private final String worldName;
	private final ChunkGenerator chunkGenerator;

}
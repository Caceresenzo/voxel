package voxel.client.chunk;

import java.util.EnumMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import voxel.shared.chunk.ChunkKey;
import voxel.shared.chunk.Face;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class CubicNeighborChunks {

	private final Map<Face, Chunk>[] levels;

	public Chunk getChunk(Face face, int ySign) {
		final var level = levels[ySign + 1];
		return level.get(face);
	}

	@SuppressWarnings("unchecked")
	public static CubicNeighborChunks from(ChunkManager chunkManager, ChunkKey chunkKey) {
		final var levels = new Map[3];

		levels[0] = getLevel(chunkManager, chunkKey.at(Face.DOWN));
		levels[1] = getLevel(chunkManager, chunkKey);
		levels[2] = getLevel(chunkManager, chunkKey.at(Face.UP));

		return new CubicNeighborChunks(levels);
	}

	private static Map<Face, Chunk> getLevel(ChunkManager chunkManager, ChunkKey chunkKey) {
		final var map = new EnumMap<Face, Chunk>(Face.class);

		map.put(Face.EAST, chunkManager.get(chunkKey.at(Face.EAST)));
		map.put(Face.WEST, chunkManager.get(chunkKey.at(Face.WEST)));
		map.put(Face.NORTH, chunkManager.get(chunkKey.at(Face.NORTH)));
		map.put(Face.SOUTH, chunkManager.get(chunkKey.at(Face.SOUTH)));
		map.put(Face.NORTH_EAST, chunkManager.get(chunkKey.at(Face.NORTH_EAST)));
		map.put(Face.NORTH_WEST, chunkManager.get(chunkKey.at(Face.NORTH_WEST)));
		map.put(Face.SOUTH_EAST, chunkManager.get(chunkKey.at(Face.SOUTH_EAST)));
		map.put(Face.SOUTH_WEST, chunkManager.get(chunkKey.at(Face.SOUTH_WEST)));
		map.put(Face.SELF, chunkManager.get(chunkKey));

		return map;
	}

	public static CubicNeighborChunks from(Chunk chunk) {
		return from(chunk.getWorld().getChunkManager(), chunk.asKey());
	}

}
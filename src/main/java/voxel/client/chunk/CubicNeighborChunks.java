package voxel.client.chunk;

import java.util.EnumMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import voxel.shared.chunk.ChunkPosition;
import voxel.shared.chunk.Face;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class CubicNeighborChunks {

	private final Map<Face, ClientChunk>[] levels;

	public ClientChunk getChunk(Face face, int ySign) {
		final var level = levels[ySign + 1];
		return level.get(face);
	}

	@SuppressWarnings("unchecked")
	public static CubicNeighborChunks from(ChunkManager chunkManager, ChunkPosition chunkPosition) {
		final var levels = new Map[3];

		levels[0] = getLevel(chunkManager, chunkPosition.add(Face.DOWN));
		levels[1] = getLevel(chunkManager, chunkPosition);
		levels[2] = getLevel(chunkManager, chunkPosition.add(Face.UP));

		return new CubicNeighborChunks(levels);
	}

	private static Map<Face, ClientChunk> getLevel(ChunkManager chunkManager, ChunkPosition chunkPosition) {
		final var map = new EnumMap<Face, ClientChunk>(Face.class);

		map.put(Face.EAST, chunkManager.get(chunkPosition.add(Face.EAST)));
		map.put(Face.WEST, chunkManager.get(chunkPosition.add(Face.WEST)));
		map.put(Face.NORTH, chunkManager.get(chunkPosition.add(Face.NORTH)));
		map.put(Face.SOUTH, chunkManager.get(chunkPosition.add(Face.SOUTH)));
		map.put(Face.NORTH_EAST, chunkManager.get(chunkPosition.add(Face.NORTH_EAST)));
		map.put(Face.NORTH_WEST, chunkManager.get(chunkPosition.add(Face.NORTH_WEST)));
		map.put(Face.SOUTH_EAST, chunkManager.get(chunkPosition.add(Face.SOUTH_EAST)));
		map.put(Face.SOUTH_WEST, chunkManager.get(chunkPosition.add(Face.SOUTH_WEST)));
		map.put(Face.SELF, chunkManager.get(chunkPosition));

		return map;
	}

	public static CubicNeighborChunks from(ClientChunk chunk) {
		return from(chunk.getWorld().getChunkManager(), chunk.getPosition());
	}

}
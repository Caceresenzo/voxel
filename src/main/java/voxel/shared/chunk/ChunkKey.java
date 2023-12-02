package voxel.shared.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import voxel.server.chunk.Chunk;

@Getter
@ToString
@Accessors(fluent = true)
public final class ChunkKey {

	private static final Long2ObjectOpenHashMap<ChunkKey> CACHE = new Long2ObjectOpenHashMap<>(512, 0.5F);

	private final int x;
	private final int z;
	private final int hashCode;

	private ChunkKey(long id) {
		this((int) id, (int) (id >> 32));
	}

	private ChunkKey(int x, int z) {
		this.x = x;
		this.z = z;
		this.hashCode = x * 31 + z;
	}

	public static ChunkKey of(int x, int z) {
		final var id = getId(x, z);

		var value = CACHE.get(id);
		if (value == null) {
			value = new ChunkKey(x, z);
			CACHE.put(id, value);
		}

		return value;
	}

	public static ChunkKey of(long id) {
		var value = CACHE.get(id);
		if (value == null) {
			value = new ChunkKey(id);
			CACHE.put(id, value);
		}

		return value;
	}

	public static ChunkKey to(Chunk chunk) {
		return of(chunk.getX(), chunk.getZ());
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChunkKey otherKey) {
			return x == otherKey.x && z == otherKey.z;
		}

		return false;
	}

	public static long getId(int x, int z) {
		return ((long) x << 32) | (z & 0xFFFFFFFFL);
	}

}
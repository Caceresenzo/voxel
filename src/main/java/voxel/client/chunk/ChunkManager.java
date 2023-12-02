package voxel.client.chunk;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import voxel.client.world.World;
import voxel.shared.chunk.ChunkKey;

public class ChunkManager {

	private final World world;
	private final ChunkShaderProgram chunkShaderProgram;
	private final ConcurrentMap<ChunkKey, Chunk> storage = new ConcurrentHashMap<>();

	public ChunkManager(World world, ChunkShaderProgram chunkShaderProgram) {
		this.world = world;
		this.chunkShaderProgram = chunkShaderProgram;
	}

	public Chunk get(ChunkKey key) {
		return storage.get(key);
	}

	public boolean isLoaded(ChunkKey key) {
		final var chunk = storage.get(key);

		return chunk != null;
	}

	public Chunk load(ChunkKey key) {
		final var chunk = new Chunk(world, key, chunkShaderProgram);
		storage.put(key, chunk);

		return chunk;
	}

	public Collection<Chunk> getAll() {
		return storage.values();
	}

}
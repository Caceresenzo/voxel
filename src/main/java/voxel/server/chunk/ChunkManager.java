package voxel.server.chunk;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import voxel.server.chunk.generator.ChunkGenerator;
import voxel.server.world.World;
import voxel.shared.chunk.ChunkKey;

public class ChunkManager {

	private final World world;
	private final ChunkGenerator generator;
	private final ConcurrentMap<ChunkKey, Chunk> storage = new ConcurrentHashMap<>();

	public ChunkManager(World world, ChunkGenerator generator) {
		this.world = world;
		this.generator = generator;
	}

	public Chunk get(ChunkKey key) {
		return storage.computeIfAbsent(key, (key_) -> new Chunk(world, key_));
	}

	public boolean isLoaded(ChunkKey key) {
		final var chunk = storage.get(key);

		return chunk != null && chunk.isLoaded();
	}

	public boolean load(ChunkKey key) {
		return load(get(key));
	}

	public boolean load(Chunk chunk) {
		generate(chunk);

		return true;
	}

	private void generate(Chunk chunk) {
		final var voxels = new byte[Chunk.VOLUME];

		generator.generate(chunk.getWorld(), chunk.getPosition(), voxels);

		chunk.setVoxels(voxels);
	}

	public Collection<Chunk> getAll() {
		return storage.values();
	}

}
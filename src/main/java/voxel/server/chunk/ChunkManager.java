package voxel.server.chunk;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import voxel.server.chunk.generator.ChunkGenerator;
import voxel.server.world.World;
import voxel.shared.chunk.ChunkPosition;

public class ChunkManager {

	private final World world;
	private final ChunkGenerator generator;
	private final ConcurrentMap<ChunkPosition, ServerChunk> storage = new ConcurrentHashMap<>();

	public ChunkManager(World world, ChunkGenerator generator) {
		this.world = world;
		this.generator = generator;
	}

	public ServerChunk get(ChunkPosition key) {
		return storage.computeIfAbsent(key, (key_) -> new ServerChunk(world, key_));
	}

	public boolean isLoaded(ChunkPosition key) {
		final var chunk = storage.get(key);

		return chunk != null && chunk.isLoaded();
	}

	public boolean load(ChunkPosition key) {
		return load(get(key));
	}

	public boolean load(ServerChunk chunk) {
		generate(chunk);

		return true;
	}

	private void generate(ServerChunk chunk) {
		final var voxels = new byte[ServerChunk.VOLUME];

		generator.generate(chunk.getWorld(), chunk.getPosition(), voxels);

		chunk.setVoxels(voxels);
	}

	public Collection<ServerChunk> getAll() {
		return storage.values();
	}

	public Collection<ServerChunk> getNear(ChunkPosition center, int radius) {
		final var centerVector = center.toFloatVector();
		final var maxDistance = Math.pow(radius, 2);

		return storage.values()
			.stream()
			.filter((chunk) -> chunk.getPosition().toFloatVector().distanceSquared(centerVector) < maxDistance)
			.toList();
	}

}
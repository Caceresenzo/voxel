package voxel.client.chunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import voxel.client.world.World;
import voxel.shared.chunk.ChunkKey;
import voxel.shared.chunk.Face;

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

	List<Chunk> getNeighborToInvalidate(ChunkKey chunkKey) {
		final var chunks = new ArrayList<Chunk>(6);

		chunks.add(get(chunkKey.at(Face.UP)));
		chunks.add(get(chunkKey.at(Face.DOWN)));
		chunks.add(get(chunkKey.at(Face.EAST)));
		chunks.add(get(chunkKey.at(Face.WEST)));
		chunks.add(get(chunkKey.at(Face.NORTH)));
		chunks.add(get(chunkKey.at(Face.SOUTH)));

		return chunks;
	}

	public Collection<Chunk> getAll() {
		return storage.values();
	}

}
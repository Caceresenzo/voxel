package voxel.client.chunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import voxel.client.world.World;
import voxel.shared.chunk.ChunkPosition;
import voxel.shared.chunk.Face;

public class ChunkManager {

	private final World world;
	private final ChunkShaderProgram chunkShaderProgram;
	private final ConcurrentMap<ChunkPosition, ClientChunk> storage = new ConcurrentHashMap<>();

	public ChunkManager(World world, ChunkShaderProgram chunkShaderProgram) {
		this.world = world;
		this.chunkShaderProgram = chunkShaderProgram;
	}

	public ClientChunk get(ChunkPosition key) {
		return storage.get(key);
	}

	public boolean isLoaded(ChunkPosition key) {
		final var chunk = storage.get(key);

		return chunk != null;
	}

	public ClientChunk load(ChunkPosition key) {
		final var chunk = new ClientChunk(world, key, chunkShaderProgram);
		storage.put(key, chunk);

		return chunk;
	}

	List<ClientChunk> getNeighborToInvalidate(ChunkPosition chunkPosition) {
		final var chunks = new ArrayList<ClientChunk>(6);

		chunks.add(get(chunkPosition.add(Face.UP)));
		chunks.add(get(chunkPosition.add(Face.DOWN)));
		chunks.add(get(chunkPosition.add(Face.EAST)));
		chunks.add(get(chunkPosition.add(Face.WEST)));
		chunks.add(get(chunkPosition.add(Face.NORTH)));
		chunks.add(get(chunkPosition.add(Face.SOUTH)));

		return chunks;
	}

	public Collection<ClientChunk> getAll() {
		return storage.values();
	}

}
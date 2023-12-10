package voxel.client.world;

import org.joml.Vector3i;

import lombok.Getter;
import voxel.client.chunk.ChunkManager;
import voxel.client.chunk.ChunkShaderProgram;
import voxel.client.chunk.ClientChunk;
import voxel.client.render.Camera;
import voxel.shared.chunk.ChunkPosition;

public class World {

	@Getter
	private final String name;

	@Getter
	private final ChunkManager chunkManager;

	public World(String name, ChunkShaderProgram chunkShaderProgram) {
		this.name = name;
		this.chunkManager = new ChunkManager(this, chunkShaderProgram);
	}

	public void render(Camera camera) {
		for (final var chunk : chunkManager.getAll()) {
			chunk.render(camera);
		}
	}

	public ClientChunk getChunk(int x, int y, int z) {
		return chunkManager.get(new ChunkPosition(x, y, z));
	}

	public ClientChunk getChunk(ChunkPosition position) {
		return chunkManager.get(position);
	}

	public ClientChunk getChunkAt(Vector3i worldPosition) {
		final var x = Math.floorDiv(worldPosition.x, ClientChunk.WIDTH);
		final var y = Math.floorDiv(worldPosition.y, ClientChunk.DEPTH);
		final var z = Math.floorDiv(worldPosition.z, ClientChunk.HEIGHT);

		//		System.out.printf("x=%d y=%d z=%d %n", x, y, z);
		return getChunk(x, y, z);
	}

}
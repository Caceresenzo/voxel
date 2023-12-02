package voxel.client.world;

import lombok.Getter;
import voxel.client.chunk.ChunkManager;
import voxel.client.chunk.ChunkShaderProgram;
import voxel.client.render.Camera;

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

}
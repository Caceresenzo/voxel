package voxel;

import org.joml.Vector3i;

import lombok.Getter;
import voxel.mesh.ChunkShaderProgram;

public class World {
	
	private final ChunkShaderProgram chunkShaderProgram;
	private @Getter Chunk[] chunks = new Chunk[Settings.WORLD_VOLUME];
	private @Getter byte[][] voxels = new byte[Settings.WORLD_VOLUME][];
	
	public World(ChunkShaderProgram chunkShaderProgram) {
		this.chunkShaderProgram = chunkShaderProgram;
		
		buildChunks();
		buildChunkMeshes();
	}
	
	public void buildChunks() {
		for (var x = 0; x < Settings.WORLD_WIDTH; ++x) {
			for (var y = 0; y < Settings.WORLD_HEIGHT; ++y) {
				for (var z = 0; z < Settings.WORLD_DEPTH; ++z) {
					final var position = new Vector3i(x, y, z);
					final var chunk = new Chunk(chunkShaderProgram, position, this);
					
					final var index = toChunkIndex(x, y, z);
//					System.out.printf("x=%d y=%d z=%d index=%d %n", x, y, z, index);
					chunks[index] = chunk;
					voxels[index] = chunk.buildVoxels();
				}
			}
		}
	}
	
	public void buildChunkMeshes() {
		for (final var chunk : chunks) {
			chunk.buildMesh();
		}
	}
	
	public void render() {
		for (final var chunk : chunks) {
			chunk.render();
		}
	}
	
	public static int toChunkIndex(int x, int y, int z) {
		return (z * Settings.WORLD_DEPTH * Settings.WORLD_HEIGHT) + (y * Settings.WORLD_WIDTH) + x;
	}
	
}
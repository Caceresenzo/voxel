package voxel;

import org.joml.SimplexNoise;

import lombok.Getter;
import voxel.mesh.ChunkMesh;
import voxel.mesh.ChunkShaderProgram;

public class Chunk {
	
	private final ChunkShaderProgram shaderProgram;
	private final @Getter byte[] voxels;
	private ChunkMesh mesh;
	
	public Chunk(ChunkShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
		this.voxels = buildVoxels();
		this.buildMesh();
	}
	
	public void buildMesh() {
		mesh = new ChunkMesh(this, shaderProgram);
	}
	
	public void render() {
		mesh.render();
	}
	
	private static byte[] buildVoxels() {
		byte[] voxels = new byte[Settings.CHUNK_VOLUME];
		
		for (var x = 0; x < Settings.CHUNK_SIZE; ++x) {
			for (var y = 0; y < Settings.CHUNK_HEIGHT; ++y) {
				for (var z = 0; z < Settings.CHUNK_SIZE; ++z) {
					final var index = (z * Settings.CHUNK_SIZE * Settings.CHUNK_HEIGHT) + (y * Settings.CHUNK_SIZE) + x;
					
					final var value = SimplexNoise.noise(x * 0.05f + 1, y * 0.05f + 1, z * 0.05f + 1);
					if (value > 0) {
						voxels[index] = (byte) (((x + y + z) % 255) & 0xff);
					}
				}
			}
		}
		
		return voxels;
	}
	
}
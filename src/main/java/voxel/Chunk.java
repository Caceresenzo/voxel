package voxel;

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
			for (var y = 0; y < Settings.CHUNK_SIZE; ++y) {
				for (var z = 0; z < Settings.CHUNK_SIZE; ++z) {
					final var index = (x + Settings.CHUNK_SIZE * z + Settings.CHUNK_AREA * y);
					voxels[index] = (byte) (((x + y + z) % 255) & 0xff);
					System.out.println(voxels[index]);
				}
			}
		}
		
		return voxels;
	}
	
}
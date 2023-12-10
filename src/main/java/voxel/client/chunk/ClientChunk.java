package voxel.client.chunk;

import org.joml.Vector3f;

import lombok.Getter;
import voxel.client.Settings;
import voxel.client.render.Camera;
import voxel.client.world.World;
import voxel.shared.chunk.Chunk;
import voxel.shared.chunk.ChunkPosition;

public class ClientChunk extends Chunk {

	@Getter
	private final World world;

	@Getter
	private final ChunkShaderProgram shaderProgram;

	private ChunkMesh mesh;

	@Getter
	private final Vector3f center;

	public ClientChunk(World world, ChunkPosition position, ChunkShaderProgram shaderProgram) {
		super(position);

		this.world = world;
		this.shaderProgram = shaderProgram;

		this.center = position.toFloatVector().add(new Vector3f(0.5f)).mul(Settings.CHUNK_SIZE);
	}

	public void buildMesh() {
		final var newMesh = new ChunkMeshBuilder(this).build();

		if (this.mesh != null) {
			this.mesh.delete();
		}

		this.mesh = newMesh;
	}

	public void render(Camera camera) {
		if (mesh == null) {
			buildMesh();
		}

		if (isEmpty() || !camera.getFrustum().contains(this)) {
			return;
		}

		mesh.render();
	}

	@Override
	public void setVoxels(byte[] voxels) {
		super.setVoxels(voxels);

		deleteMesh();
	}

	public void setVoxel(int x, int y, int z, byte value) {
		super.setVoxel(x, y, z, value);

		deleteMesh();
	}

	public void deleteMesh() {
		if (mesh != null) {
			mesh.delete();
			mesh = null;
		}
	}

}
package voxel.client.chunk;

import java.util.Arrays;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import lombok.AccessLevel;
import lombok.Getter;
import voxel.client.Settings;
import voxel.client.render.Camera;
import voxel.client.world.World;
import voxel.shared.chunk.ChunkConstants;
import voxel.shared.chunk.ChunkKey;

public class Chunk implements ChunkConstants {

	@Getter
	private final World world;

	@Getter
	private final Vector3ic position;

	@Getter
	private final ChunkShaderProgram shaderProgram;

	@Getter(AccessLevel.PACKAGE)
	private final byte[] voxels;

	private ChunkMesh mesh;
	private boolean isEmpty;

	@Getter
	private final Vector3f center;

	public Chunk(World world, ChunkKey key, ChunkShaderProgram shaderProgram) {
		this.world = world;
		this.position = new Vector3i(key.x(), key.y(), key.z());
		this.shaderProgram = shaderProgram;

		this.voxels = new byte[VOLUME];
		this.isEmpty = true;

		this.center = new Vector3f(position).add(new Vector3f(0.5f)).mul(Settings.CHUNK_SIZE);
	}

	public void fill(byte voxelId) {
		Arrays.fill(voxels, voxelId);
		testIfEmpty();
		buildMesh();
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

		if (isEmpty || !camera.getFrustum().contains(this)) {
			return;
		}

		mesh.render();
	}

	public void testIfEmpty() {
		for (final var voxel : voxels) {
			if (voxel != 0) {
				isEmpty = false;
				break;
			}
		}
	}

	public Vector3i getWorldPosition() {
		return new Vector3i(
			position.x() * WIDTH,
			position.y() * DEPTH,
			position.z() * HEIGHT
		);
	}

	public void setVoxels(byte[] voxels) {
		System.arraycopy(voxels, 0, this.voxels, 0, this.voxels.length);

		deleteMesh();
		testIfEmpty();

		//		for (final var chunk : world.getChunkManager().getNeighborToInvalidate(asKey())) {
		//			if (chunk != null) {
		//				chunk.deleteMesh();
		//			}
		//		}
	}

	public void setVoxel(int x, int y, int z, byte value) {
		final var index = index(x, y, z);

		voxels[index] = value;

		testIfEmpty();
		deleteMesh();
	}

	public void deleteMesh() {
		if (mesh != null) {
			mesh.delete();
			mesh = null;
		}
	}

	public byte getVoxelId(int x, int y, int z) {
		return voxels[index(x, y, z)];
	}

	public ChunkKey asKey() {
		return new ChunkKey(position.x(), position.y(), position.z());
	}

	public static int index(Vector3i position) {
		return (position.y * AREA) + (position.z * WIDTH) + position.x;
	}

	public static int index(int x, int y, int z) {
		return (y * AREA) + (z * WIDTH) + x;
	}

}
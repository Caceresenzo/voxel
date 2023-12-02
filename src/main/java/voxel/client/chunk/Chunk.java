package voxel.client.chunk;

import java.util.Arrays;

import org.joml.Vector2f;
import org.joml.Vector3i;

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
	private final int x;

	@Getter
	private final int z;

	private final ChunkShaderProgram shaderProgram;

	@Getter(AccessLevel.PACKAGE)
	private final byte[] voxels;

	private ChunkMesh mesh;
	private boolean isEmpty;

	@Getter
	private final Vector2f center;

	public Chunk(World world, ChunkKey key, ChunkShaderProgram shaderProgram) {
		this.world = world;
		this.x = key.x();
		this.z = key.z();
		this.shaderProgram = shaderProgram;

		this.voxels = new byte[Settings.CHUNK_VOLUME];
		this.isEmpty = true;

		this.center = new Vector2f(x, z).add(new Vector2f(0.5f)).mul(Settings.CHUNK_SIZE);
	}

	public void fill(byte voxelId) {
		Arrays.fill(voxels, voxelId);
		testIfEmpty();
		buildMesh();
	}

	public void buildMesh() {
		mesh = new ChunkMesh(this, shaderProgram);
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

	public int getWorldX() {
		return x * WIDTH;
	}

	public int getWorldZ() {
		return z * HEIGHT;
	}

	public void setVoxels(byte[] voxels) {
		System.arraycopy(voxels, 0, this.voxels, 0, this.voxels.length);

		if (mesh != null) {
			mesh.delete();
			mesh = null;
		}

		testIfEmpty();
	}

	public byte getVoxelId(int x, int y, int z) {
		return voxels[index(x, y, z)];
	}

	public static int index(Vector3i position) {
		return (position.y * AREA) + (position.z * WIDTH) + position.x;
	}

	public static int index(int x, int y, int z) {
		return (y * AREA) + (z * WIDTH) + x;
	}

}
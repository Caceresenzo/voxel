package voxel.server.chunk;

import org.joml.Vector3i;
import org.joml.Vector3ic;

import lombok.Getter;
import voxel.server.world.World;
import voxel.shared.chunk.ChunkConstants;
import voxel.shared.chunk.ChunkKey;
import voxel.util.VoxelUtils;

public class Chunk implements ChunkConstants {

	@Getter
	private final World world;

	@Getter
	private final Vector3ic position;

	private int nonAirCount;
	private byte[] voxels;

	Chunk(World world, ChunkKey key) {
		this.world = world;
		this.position = new Vector3i(key.x(), key.y(), key.z());
	}

	Chunk(World world, Vector3ic position) {
		this.world = world;
		this.position = new Vector3i(position);
	}

	public boolean isLoaded() {
		return voxels != null;
	}

	public boolean load() {
		return isLoaded() || world.getChunkManager().load(this);
	}

	public boolean unload() {
		if (!isLoaded()) {
			return true;
		}

		nonAirCount = 0;
		voxels = null;
		return true;
	}

	void setVoxels(byte[] voxels) {
		if (isLoaded()) {
			throw new IllegalStateException("voxels alreay provided");
		}

		if (voxels.length != VOLUME) {
			throw new IllegalArgumentException("voxels arrray length is not %d: %d".formatted(VOLUME, voxels.length));
		}

		this.voxels = voxels.clone();
		this.nonAirCount = VoxelUtils.countNonZero(this.voxels);
	}

	public byte getVoxel(int x, int y, int z) {
		return voxels[index(x, y, z)];
	}

	public void setVoxel(int x, int y, int z, byte value) {
		final var index = index(x, y, z);

		if (voxels[index] != 0) {
			--nonAirCount;
		}

		if (value != 0) {
			++nonAirCount;
		}

		voxels[index] = value;
	}

	public boolean isEmpty() {
		return nonAirCount == 0;
	}

	public ChunkKey asKey() {
		return new ChunkKey(position.x(), position.y(), position.z());
	}

	@Override
	public String toString() {
		return "Chunk[world=" + world.getName() + ", position=" + position + "]";
	}

	public static int index(int x, int y, int z) {
		return (y * Chunk.AREA) + (z * Chunk.WIDTH) + x;
	}

}
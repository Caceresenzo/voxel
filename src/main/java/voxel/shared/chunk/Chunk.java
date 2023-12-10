package voxel.shared.chunk;

import lombok.Getter;
import voxel.util.VoxelUtils;

public class Chunk {

	public static final int WIDTH = 16;
	public static final int LAST_X = WIDTH - 1;
	public static final int HEIGHT = 16;
	public static final int LAST_Y = HEIGHT - 1;
	public static final int DEPTH = 16;
	public static final int LAST_Z = DEPTH - 1;
	public static final int AREA = WIDTH * HEIGHT;
	public static final int VOLUME = AREA * DEPTH;

	@Getter
	private final ChunkPosition position;

	protected int nonAirCount;
	protected byte[] voxels;

	protected Chunk(ChunkPosition position) {
		this.position = position;
	}

	public boolean isLoaded() {
		return voxels != null;
	}

	public void setVoxels(byte[] voxels) {
		if (isLoaded()) {
			throw new IllegalStateException("voxels alreay provided");
		}

		if (voxels.length != VOLUME) {
			throw new IllegalArgumentException("voxels arrray length is not %d: %d".formatted(VOLUME, voxels.length));
		}

		if (this.voxels != null) {
			System.arraycopy(voxels, 0, this.voxels, 0, this.voxels.length);
		} else {
			this.voxels = voxels.clone();
		}

		this.nonAirCount = VoxelUtils.countNonZero(this.voxels);
	}

	public byte getVoxel(LocalBlockPosition localPosition) {
		return getVoxel(localPosition.x(), localPosition.y(), localPosition.z());
	}

	public byte getVoxel(int x, int y, int z) {
		return voxels[index(x, y, z)];
	}

	public void setVoxel(LocalBlockPosition localPosition, byte value) {
		setVoxel(localPosition.x(), localPosition.y(), localPosition.z(), value);
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

	@Override
	public String toString() {
		return "Chunk[" + position + "]";
	}

	public static int index(int x, int y, int z) {
		return (y * AREA) + (z * WIDTH) + x;
	}

}
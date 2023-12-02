package voxel.shared.chunk;

import org.joml.Vector3i;

import lombok.Getter;
import voxel.server.chunk.Chunk;

public class ChunkSection {

	public static final int ARRAY_SIZE = Chunk.SECTION_VOLUME;

	@Getter
	private final int y;

	private final byte[] data;
	private int count;

	public ChunkSection(int y) {
		this(y, new byte[ARRAY_SIZE]);
	}

	public ChunkSection(int y, byte[] data) {
		if (data.length != ARRAY_SIZE) {
			throw new IllegalArgumentException("array length is not %d: %d".formatted(ARRAY_SIZE, data.length));
		}

		this.y = y;
		this.data = data;
	}

	public byte getType(int x, int y, int z) {
		return data[index(x, y, z)];
	}

	public void setType(int x, int y, int z, byte value) {
		final var index = index(x, y, z);

		if (data[index] != 0) {
			--count;
		}

		if (value != 0) {
			++count;
		}

		data[index] = value;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public static int index(int x, int y, int z) {
		return (y * Chunk.AREA) + (z * Chunk.WIDTH) + x;
	}

}
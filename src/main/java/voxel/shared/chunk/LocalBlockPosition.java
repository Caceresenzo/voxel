package voxel.shared.chunk;

import org.joml.Vector3ic;

public record LocalBlockPosition(
	byte x,
	byte y,
	byte z
) {

	private static final LocalBlockPosition ZERO = new LocalBlockPosition((byte) 0, (byte) 0, (byte) 0);

	public LocalBlockPosition {
		checkX(x);
		checkY(y);
		checkZ(z);
	}

	public LocalBlockPosition(int x, int y, int z) {
		this((byte) x, (byte) y, (byte) z);
	}

	public LocalBlockPosition(Vector3ic vector) {
		this(vector.x(), vector.y(), vector.z());
	}

	public static LocalBlockPosition zero() {
		return ZERO;
	}

	public static void checkX(byte x) {
		if (x < 0) {
			throw new IllegalStateException("x < 0");
		}

		if (x > Chunk.LAST_X) {
			throw new IllegalStateException("x > " + Chunk.LAST_X);
		}
	}

	public static void checkY(byte y) {
		if (y < 0) {
			throw new IllegalStateException("y < 0");
		}

		if (y > Chunk.LAST_Y) {
			throw new IllegalStateException("y > " + Chunk.LAST_Y);
		}
	}

	public static void checkZ(byte z) {
		if (z < 0) {
			throw new IllegalStateException("z < 0");
		}

		if (z > Chunk.LAST_Z) {
			throw new IllegalStateException("z > " + Chunk.LAST_Z);
		}
	}

}
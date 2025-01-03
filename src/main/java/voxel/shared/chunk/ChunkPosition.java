package voxel.shared.chunk;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

import voxel.shared.block.BlockPosition;

public record ChunkPosition(
	int x,
	int y,
	int z
) {

	private static final ChunkPosition ZERO = new ChunkPosition(0, 0, 0);

	public ChunkPosition(Vector3ic vector) {
		this(vector.x(), vector.y(), vector.z());
	}

	public BlockPosition toBlockPosition() {
		return new BlockPosition(
			x * Chunk.WIDTH,
			y * Chunk.DEPTH,
			z * Chunk.HEIGHT
		);
	}

	// TODO Is this taking into account negative number properly? is flooring needed?
	public static ChunkPosition fromAbsolute(Vector3fc vector) {
		return new ChunkPosition(
			Math.floorDiv((int) vector.x(), Chunk.WIDTH),
			Math.floorDiv((int) vector.y(), Chunk.DEPTH),
			Math.floorDiv((int) vector.z(), Chunk.HEIGHT)
		);
	}

	public static ChunkPosition zero() {
		return ZERO;
	}

	public ChunkPosition add(Face top) {
		final var relative = top.getRelative();

		return new ChunkPosition(
			x + relative.x(),
			y + relative.y(),
			z + relative.z()
		);
	}

	public Vector3f toFloatVector() {
		return new Vector3f(x, y, z);
	}

}
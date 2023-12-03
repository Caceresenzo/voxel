package voxel.shared.chunk;

import org.joml.Vector3ic;

import voxel.server.chunk.Chunk;

public record ChunkKey(
	int x,
	int y,
	int z
) {

	private static final ChunkKey ZERO = new ChunkKey(0, 0, 0);
	
	public ChunkKey(Vector3ic vector) {
		this(vector.x(), vector.y(), vector.z());
	}

	public static ChunkKey of(Chunk chunk) {
		return chunk.asKey();
	}

	public static ChunkKey zero() {
		return ZERO;
	}

	public ChunkKey at(Face top) {
		final var relative = top.relative();

		return new ChunkKey(
			x + relative.x(),
			y + relative.y(),
			z + relative.z()
		);
	}

}
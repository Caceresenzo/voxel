package voxel.shared.block;

import org.joml.Vector3f;
import org.joml.Vector3ic;

import voxel.client.chunk.ClientChunk;
import voxel.shared.chunk.Chunk;
import voxel.shared.chunk.ChunkPosition;
import voxel.shared.chunk.Face;
import voxel.shared.chunk.LocalBlockPosition;

public record BlockPosition(
	int x,
	int y,
	int z
) {

	private static final BlockPosition ZERO = new BlockPosition(0, 0, 0);

	public BlockPosition(Vector3ic vector) {
		this(vector.x(), vector.y(), vector.z());
	}

	public ChunkPosition toChunkPosition() {
		return new ChunkPosition(
			Math.floorDiv(x, Chunk.WIDTH),
			Math.floorDiv(y, Chunk.DEPTH),
			Math.floorDiv(z, Chunk.HEIGHT)
		);
	}

	public LocalBlockPosition toLocalPosition(ChunkPosition chunkPosition) {
		var x = (this.x - (chunkPosition.x() * ClientChunk.WIDTH)) % ClientChunk.WIDTH;
		var y = (this.y - (chunkPosition.y() * ClientChunk.DEPTH)) % ClientChunk.DEPTH;
		var z = (this.z - (chunkPosition.z() * ClientChunk.HEIGHT)) % ClientChunk.HEIGHT;

		x = (x + ClientChunk.WIDTH) % ClientChunk.WIDTH;
		y = (y + ClientChunk.DEPTH) % ClientChunk.DEPTH;
		z = (z + ClientChunk.HEIGHT) % ClientChunk.HEIGHT;

		return new LocalBlockPosition(x, y, z);
	}

	public BlockPosition add(Face top) {
		final var relative = top.getRelative();

		return new BlockPosition(
			x + relative.x(),
			y + relative.y(),
			z + relative.z()
		);
	}

	public Vector3f toFloatVector() {
		return new Vector3f(x, y, z);
	}

	public static BlockPosition zero() {
		return ZERO;
	}

}
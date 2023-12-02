package voxel.server.chunk;

import lombok.Getter;
import voxel.server.world.World;
import voxel.shared.chunk.ChunkConstants;
import voxel.shared.chunk.ChunkKey;
import voxel.shared.chunk.ChunkSection;

public class Chunk implements ChunkConstants {

	@Getter
	private final World world;

	@Getter
	private final int x;

	@Getter
	private final int z;

	@Getter
	private ChunkSection[] sections;

	Chunk(World world, ChunkKey key) {
		this(world, key.x(), key.z());
	}

	Chunk(World world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}

	public boolean isLoaded() {
		return sections != null;
	}

	public boolean load() {
		return isLoaded() || world.getChunkManager().load(this);
	}

	public boolean unload() {
		if (!isLoaded()) {
			return true;
		}

		sections = null;
		return true;
	}

	void setSections(ChunkSection[] sections) {
		if (isLoaded()) {
			throw new IllegalStateException("section alreay provided");
		}

		if (sections.length != SECTION_COUNT) {
			throw new IllegalArgumentException("section arrray length is not %d: %d".formatted(SECTION_COUNT, sections.length));
		}

		this.sections = sections.clone();
	}

	@Override
	public String toString() {
		return "Chunk[world=" + world.getName() + ", x=" + x + ", z=" + z + "]";
	}

}
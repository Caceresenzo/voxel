package voxel.shared.chunk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ChunkKeyTest {

	@Test
	void getId() {
		assertEquals(42949672975l, ChunkKey.getId(10, 15));
	}

	@Test
	void of_x_y() {
		final var key = ChunkKey.of(10, 15);

		assertEquals(10, key.x());
		assertEquals(15, key.z());
	}

	@Test
	void of_id() {
		final var key = ChunkKey.of(42949672975l);

		assertEquals(10, key.x());
		assertEquals(15, key.z());
	}

	@Test
	void equals() {
		final var a = ChunkKey.of(10, 15);
		final var b = ChunkKey.of(42949672975l);

		assertEquals(a, b);
	}

}
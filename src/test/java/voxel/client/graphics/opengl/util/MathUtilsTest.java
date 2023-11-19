package voxel.client.graphics.opengl.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import opengl.util.MathUtils;

class MathUtilsTest {

	@Test
	void flatten() {
		assertArrayEquals(
			new byte[] { 0, 1, 2, 3, 4, 5 },
			MathUtils.flatten(new byte[][] {
				{ 0, 1, 2 },
				{ 3, 4, 5 }
			})
		);
	}

	@Test
	void flatten_varyingSize() {
		assertArrayEquals(
			new byte[] { 0, 1, 2, 3, 4 },
			MathUtils.flatten(new byte[][] {
				{ 0, 1, 2 },
				{ 3, 4 }
			})
		);
	}

}
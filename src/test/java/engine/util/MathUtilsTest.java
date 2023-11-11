package engine.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
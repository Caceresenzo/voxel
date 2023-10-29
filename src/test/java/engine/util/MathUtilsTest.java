package engine.util;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class MathUtilsTest {
	
	@Test
	void test() {
		final float[][] first = {
			{ 0, 0 }, { 1, 1 }, { 1, 0 },
			{ 0, 0 }, { 0, 1 }, { 1, 1 }
		};
		
		final float[][] second = {
			{ 0, 0, 0 }, { 1, 0, 1 }, { 1, 0, 0 },
			{ 0, 0, 0 }, { 0, 0, 1 }, { 1, 0, 1 }
		};
		
		final float[][] expected = {
			{ 0, 0, 0, 0, 0 },
			{ 1, 1, 1, 0, 1 },
			{ 1, 0, 1, 0, 0 },
			{ 0, 0, 0, 0, 0 },
			{ 0, 1, 0, 0, 1 },
			{ 1, 1, 1, 0, 1 }
		};
		
		assertTrue(Arrays.deepEquals(
			expected,
			MathUtils.horizontalStack(first, second)
		));
	}
	
	@Test
	void testFlat() {
		final float[] first = {
			0, 0,
			1, 1,
			1, 0,
			0, 0,
			0, 1,
			1, 1
		};
		
		final float[] second = {
			0, 0, 0,
			1, 0, 1,
			1, 0, 0,
			0, 0, 0,
			0, 0, 1,
			1, 0, 1
		};
		
		final float[] expected = {
			0, 0, 0, 0, 0,
			1, 1, 1, 0, 1,
			1, 0, 1, 0, 0,
			0, 0, 0, 0, 0,
			0, 1, 0, 0, 1,
			1, 1, 1, 0, 1
		};
		
		assertTrue(Arrays.equals(
			expected,
			MathUtils.horizontalStack(first, 2, second, 3)
		));
	}
	
}
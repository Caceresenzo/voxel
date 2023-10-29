package engine.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {
	
	public static float[][] horizontalStack(float[][] first, float[][] second) {
		// System.out.println(Arrays.deepToString(first));
		// System.out.println(Arrays.deepToString(second));
		final var lines = first.length;
		final var columns = first[0].length + second[0].length;
		// System.out.println(lines);
		// System.out.println(columns);
		
		float[][] result = new float[lines][];
		for (var index = 0; index < lines; ++index) {
			final var column = result[index] = new float[columns];
			
			final var firstColumn = first[index];
			System.arraycopy(firstColumn, 0, column, 0, firstColumn.length);
			
			final var secondColumn = second[index];
			System.arraycopy(secondColumn, 0, column, firstColumn.length, secondColumn.length);
		}
		
		// System.out.println(Arrays.deepToString(result));
		return result;
	}
	
	public static float[] horizontalStack(float[] first, int firstSize, float[] second, int secondSize) {
		// System.out.println(Arrays.deepToString(first));
		// System.out.println(Arrays.deepToString(second));
		final var lines = first.length / firstSize;
		final var columns = firstSize + secondSize;
		// System.out.println(lines);
		// System.out.println(columns);
		
		float[] result = new float[columns * lines];
		for (var index = 0; index < lines; ++index) {
			// System.out.println("index " + index);
			final var offset = columns * index;
			// System.out.println("offset " + offset);
			
			System.arraycopy(first, firstSize * index, result, offset, firstSize);
			System.arraycopy(second, secondSize * index, result, offset + firstSize, secondSize);
		}
		
		// System.out.println(Arrays.deepToString(result));
		return result;
	}
	
	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}
	
}
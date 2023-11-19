package opengl.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {

	public static byte[][] horizontalStack(byte[][] first, byte[][] second) {
		final var lines = first.length;
		final var columns = first[0].length + second[0].length;

		byte[][] result = new byte[lines][];
		for (var index = 0; index < lines; ++index) {
			final var column = result[index] = new byte[columns];

			final var firstColumn = first[index];
			System.arraycopy(firstColumn, 0, column, 0, firstColumn.length);

			final var secondColumn = second[index];
			System.arraycopy(secondColumn, 0, column, firstColumn.length, secondColumn.length);
		}

		return result;
	}

	public static float[] horizontalStack(float[] first, int firstSize, float[] second, int secondSize) {
		final var lines = first.length / firstSize;
		final var columns = firstSize + secondSize;

		float[] result = new float[columns * lines];
		for (var index = 0; index < lines; ++index) {
			final var offset = columns * index;

			System.arraycopy(first, firstSize * index, result, offset, firstSize);
			System.arraycopy(second, secondSize * index, result, offset + firstSize, secondSize);
		}

		return result;
	}

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public static byte[] flatten(byte[][] nested) {
		var size = 0;
		for (final var array : nested) {
			size += array.length;
		}

		final var result = new byte[size];

		var index = 0;
		for (final var array : nested) {
			System.arraycopy(array, 0, result, index, array.length);
			index += array.length;
		}

		return result;
	}

}
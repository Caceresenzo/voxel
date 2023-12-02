package voxel.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class VoxelUtils {

	public static int countNonZero(byte[] array) {
		var count = 0;

		for (final var value : array) {
			if (value != 0) {
				++count;
			}
		}

		return count;
	}

}
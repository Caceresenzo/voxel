package engine.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

}
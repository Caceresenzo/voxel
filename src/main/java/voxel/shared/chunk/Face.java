package voxel.shared.chunk;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3i;
import org.joml.Vector3ic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public enum Face {

	UP(new Vector3i(0, 1, 0)) /* top */,
	DOWN(new Vector3i(0, -1, 0)) /* bottom */,
	EAST(new Vector3i(1, 0, 0)) /* right */,
	WEST(new Vector3i(-1, 0, 0)) /* left */,
	NORTH(new Vector3i(0, 0, -1)) /* back */,
	SOUTH(new Vector3i(0, 0, 1)), /* front */
	NORTH_EAST(NORTH, EAST),
	NORTH_WEST(NORTH, WEST),
	SOUTH_EAST(SOUTH, EAST),
	SOUTH_WEST(SOUTH, WEST),
	SELF(new Vector3i(0));

	private static final List<Face> VALUES = Collections.unmodifiableList(Arrays.asList(Face.values()));
	private static final Map<Vector3ic, Face> RELATIVE = new HashMap<>();

	static {
		for (final var face : VALUES) {
			RELATIVE.put(face.relative, face);
		}
	}

	private final Vector3ic relative;

	private Face(Face first, Face second) {
		this.relative = first.relative.add(second.relative, new Vector3i());
	}

	public List<Face> valuesList() {
		return VALUES;
	}

	public static Face from(Vector3ic vector) {
		return RELATIVE.get(vector);
	}

}
package voxel.shared.chunk;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3i;
import org.joml.Vector3ic;

import lombok.Getter;
import voxel.util.Vectors;

@Getter
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
	SELF(Vectors.zero3i());

	private static final List<Face> VALUES = Collections.unmodifiableList(Arrays.asList(Face.values()));
	private static final Face[] CARDINALS = { UP, DOWN, EAST, WEST, NORTH, SOUTH };
	private static final Map<Vector3ic, Face> RELATIVE = new HashMap<>();

	static {
		for (final var face : VALUES) {
			RELATIVE.put(face.relative, face);
		}
	}

	private final Vector3ic relative;
	private final boolean cartesian;

	private Face(Vector3ic relative) {
		this.relative = relative;
		this.cartesian = Vectors.zero3i().equals(Vectors.zero3i());
	}

	private Face(Face first, Face second) {
		this.relative = first.relative.add(second.relative, new Vector3i());
		this.cartesian = false;
	}

	public List<Face> valuesList() {
		return VALUES;
	}

	public static Face from(Vector3ic vector) {
		return RELATIVE.get(vector);
	}

	public static Face valueOfCardinal(int ordinal) {
		return CARDINALS[ordinal];
	}

	public void addRelative(Vector3i vector) {
		vector.x += relative.x();
		vector.y += relative.y();
		vector.z += relative.z();
	}

}
package voxel.mesh.chunk;

public record AmbiantOcclusion(
	int _0,
	int _1,
	int _2,
	int _3,
	boolean flip
) {

	public AmbiantOcclusion(int _0, int _1, int _2, int _3) {
		this(
			_0, _1, _2, _3,
			_1 + _3 > _0 + _2
		);
	}

	public AmbiantOcclusion(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f, boolean g, boolean h) {
		this(
			add(a, b, c),
			add(g, h, a),
			add(e, f, g),
			add(c, d, e)
		);
	}

	private static int add(boolean a, boolean b, boolean c) {
		int x = 0;

		if (a) {
			++x;
		}

		if (b) {
			++x;
		}

		if (c) {
			++x;
		}

		return x;
	}

	public int flipId() {
		return flip ? 1 : 0;
	}

}
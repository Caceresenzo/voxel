package voxel.shared.chunk;

public interface ChunkConstants {

	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;
	public static final int DEPTH = 64;
	public static final int AREA = WIDTH * HEIGHT;
	public static final int VOLUME = AREA * DEPTH;

	public static final int SECTION_DEPTH = 16;
	public static final int SECTION_VOLUME = AREA * SECTION_DEPTH;
	public static final int SECTION_COUNT = DEPTH / SECTION_DEPTH;

}
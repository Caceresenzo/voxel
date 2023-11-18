package voxel.client;

import org.joml.Vector2i;
import org.joml.Vector3f;

public class Settings {
	
	public static final Vector2i WINDOW_RESOLUTION = new Vector2i(1200, 720);
	
	public static final int CHUNK_SIZE = 32;
	public static final int CHUNK_HEIGHT = 32;
	public static final int HALF_CHUNK_SIZE = CHUNK_SIZE / 2;
	public static final int CHUNK_AREA = CHUNK_SIZE * CHUNK_SIZE;
	public static final int CHUNK_VOLUME = CHUNK_AREA * CHUNK_HEIGHT;
	public static final double CHUNK_SPHERE_RADIUS = HALF_CHUNK_SIZE * Math.sqrt(3);
	
	public static final int WORLD_WIDTH = 5;
	public static final int WORLD_HEIGHT = 3;
	public static final int WORLD_DEPTH = WORLD_WIDTH;
	public static final int WORLD_AREA = WORLD_WIDTH * WORLD_DEPTH;
	public static final int WORLD_VOLUME = WORLD_AREA * WORLD_HEIGHT;

	public static final int CENTER_XZ = WORLD_WIDTH * HALF_CHUNK_SIZE;
	public static final int CENTER_Y = WORLD_HEIGHT * HALF_CHUNK_SIZE;
	
	public static final float ASPECT_RATIO = (float) WINDOW_RESOLUTION.x / WINDOW_RESOLUTION.y;
	public static final int FOV_DEGREE = 50;
	public static final float VERTICAL_FOV = (float) Math.toRadians(FOV_DEGREE);
	public static final float HORIZONTAL_FOV = (float) (2 * Math.atan(Math.tan(VERTICAL_FOV * 0.5) * ASPECT_RATIO));
	public static final float NEAR = 0.1f;
	public static final float FAR = 200000.0f;
	public static final float PITCH_MAX = (float) Math.toRadians(89);
	
	public static final float PLAYER_SPEED = 0.1f;
	public static final float PLAYER_ROTATION_SPEED = 0.003f;
	public static final Vector3f PLAYER_POSITION = new Vector3f(CENTER_XZ, WORLD_HEIGHT * CHUNK_SIZE, CENTER_XZ);
	public static final float MOUSE_SENSITIVITY = 0.002f;
	
	public static final Vector3f WORLD_UP = new Vector3f(0, 1, 0);

	public static final int MAX_RAY_DIST = 60;
	
}
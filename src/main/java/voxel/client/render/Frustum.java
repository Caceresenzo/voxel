package voxel.client.render;

import org.joml.Vector3f;

import voxel.client.Settings;
import voxel.client.level.chunk.Chunk;

public class Frustum {

	private final Camera camera;
	private final double factorX;
	private final double tanX;
	private final double factorY;
	private final double tanY;

	public Frustum(Camera camera) {
		this.camera = camera;
		
		var halfX = Settings.HORIZONTAL_FOV * 0.5;
		factorX = 1.0 / Math.cos(halfX);
		tanX = Math.tan(halfX);
		
		var halfY = Settings.VERTICAL_FOV * 0.5;
		factorY = 1.0 / Math.cos(halfY);
		tanY = Math.tan(halfY);
	}

	public boolean contains(Chunk chunk) {
		var sphereVector = new Vector3f(chunk.getCenter()).sub(camera.getPosition());
		
		var sphereZ = sphereVector.dot(camera.getForward());
		if (!(Settings.NEAR - Settings.CHUNK_SPHERE_RADIUS <= sphereZ && sphereZ <= Settings.FAR + Settings.CHUNK_SPHERE_RADIUS)) {
			return false;
		}
		
		var sphereY = sphereVector.dot(camera.getUp());
		var distanceY = factorY * Settings.CHUNK_SPHERE_RADIUS + sphereZ * tanY;
		if (!(-distanceY <= sphereY && sphereY <= distanceY)) {
			return false;
		}
		
		var sphereX = sphereVector.dot(camera.getRight());
		var distanceX = factorX * Settings.CHUNK_SPHERE_RADIUS + sphereZ * tanX;
		if (!(-distanceX <= sphereY && sphereX <= distanceX)) {
			return false;
		}
		
		return true;
	}

}
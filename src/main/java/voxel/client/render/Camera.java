package voxel.client.render;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import lombok.Getter;
import opengl.util.MathUtils;
import voxel.client.Settings;

@Getter
public class Camera {

	private Vector3f position;
	private float yaw;
	private float pitch;
	private Vector3f up;
	private Vector3f right;
	private Vector3f forward;
	private Matrix4f projection;
	private Matrix4f view;
	private Frustum frustum;

	public Camera(Vector3f position, float yaw, float pitch) {
		this.position = position;
		this.yaw = (float) Math.toRadians(yaw);
		this.pitch = (float) Math.toRadians(pitch);
		this.up = new Vector3f(0, 1, 0);
		this.right = new Vector3f(1, 0, 0);
		this.forward = new Vector3f(0, 0, -1);
		this.projection = new Matrix4f().perspective(Settings.VERTICAL_FOV, Settings.ASPECT_RATIO, Settings.NEAR, Settings.FAR);
		this.view = new Matrix4f();

		this.frustum = new Frustum(this);
	}

	public Matrix4f getViewWithoutTranslation() {
		return new Matrix4f(new Matrix3f(view));
	}

	public void move(float x, float y, float z, float yaw, float pitch, boolean update) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		this.yaw = yaw;
		this.pitch = pitch;

		if (update) {
			update();
		}
	}

	public void update() {
		updateVectors();
		updateViewMatrix();
	}

	public void updateViewMatrix() {
		view = new Matrix4f().lookAt(position, position.add(forward, new Vector3f()), up);
	}

	public void updateVectors() {
		forward.x = (float) (Math.cos(yaw) * Math.cos(pitch));
		forward.y = (float) Math.sin(pitch);
		forward.z = (float) (Math.sin(yaw) * Math.cos(pitch));

		forward.normalize();
		forward.cross(Settings.WORLD_UP, right).normalize();
		right.cross(forward, up).normalize();
	}

	public void rotatePitch(float yDelta) {
		pitch -= yDelta;
		pitch = MathUtils.clamp(pitch, -Settings.PITCH_MAX, Settings.PITCH_MAX);
	}

	public void rotateYaw(float xDelta) {
		yaw += xDelta;
	}

	public void moveLeft(float velocity) {
		position.sub(right.mul(velocity, new Vector3f()));
	}

	public void moveRight(float velocity) {
		position.add(right.mul(velocity, new Vector3f()));
	}

	public void moveUp(float velocity) {
		// position.add(up.mul(velocity, new Vector3f()));
		position.add(Settings.WORLD_UP.mul(velocity, new Vector3f()));
	}

	public void moveDown(float velocity) {
		// position.sub(up.mul(velocity, new Vector3f()));
		position.sub(Settings.WORLD_UP.mul(velocity, new Vector3f()));
	}

	public void moveForward(float velocity) {
		// position.add(forward.mul(velocity, new Vector3f()));

		final var front = new Vector3f(
			(float) (Math.cos(yaw) * Math.cos(pitch)),
			(float) 0,
			(float) (Math.sin(yaw) * Math.cos(pitch))
		).normalize(this.forward.length()).mul(velocity);

		position.add(front);
	}

	public void moveBackward(float velocity) {
		// position.sub(forward.mul(velocity, new Vector3f()));

		final var front = new Vector3f(
			(float) (Math.cos(yaw) * Math.cos(pitch)),
			(float) 0,
			(float) (Math.sin(yaw) * Math.cos(pitch))
		).normalize(this.forward.length()).mul(velocity);

		position.sub(front);
	}

}
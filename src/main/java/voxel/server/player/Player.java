package voxel.server.player;

import java.util.UUID;

import org.joml.Vector3f;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import voxel.server.RemoteClient;

@RequiredArgsConstructor
public class Player {

	@Getter
	private final RemoteClient client;

	@Getter
	private final UUID uuid;

	@Getter
	private final String login;

	@Getter
	@Setter
	@NonNull
	private Vector3f position = new Vector3f();

	@Getter
	@Setter
	private float yaw;

	@Getter
	@Setter
	private float pitch;

	public void updateLocation(float x, float y, float z, float yaw, float pitch) {
		this.position.x = x;
		this.position.y = y;
		this.yaw = yaw;
		this.pitch = pitch;
	}

}
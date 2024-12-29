package voxel.client.player;

import java.util.UUID;

import org.joml.Vector3f;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import voxel.client.Settings;
import voxel.client.marker.MarkerMesh;
import voxel.client.marker.MarkerShaderProgram;
import voxel.client.render.Camera;
import voxel.client.world.World;

@RequiredArgsConstructor
public class RemotePlayer implements Player {

	private final UUID uuid;
	private final @Getter String login;
	private @Getter Vector3f position = new Vector3f(Settings.PLAYER_POSITION);
	private @Getter float yaw;
	private @Getter float pitch;
	private @Getter @Setter World world;
	private MarkerMesh mesh;

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public void move(float x, float y, float z, float yaw, float pitch) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public void render(MarkerShaderProgram markerShaderProgram, Camera camera) {
		if (mesh == null) {
			mesh = new MarkerMesh(markerShaderProgram);
		}

		mesh.move((int) position.x, (int) position.y, (int) position.z);
		mesh.render(camera);
	}

}
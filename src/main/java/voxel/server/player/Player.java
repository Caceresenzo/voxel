package voxel.server.player;

import java.util.UUID;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import voxel.server.RemoteClient;
import voxel.shared.chunk.ChunkPosition;

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

	@Getter
	@NonNull
	private ChunkPosition centerChunkPosition = ChunkPosition.zero();

	public boolean updateLocation(Vector3fc position, float yaw, float pitch) {
		this.position.set(position);
		this.yaw = yaw;
		this.pitch = pitch;

		return updateCenterChunkPosition();
	}

	private boolean updateCenterChunkPosition() {
		final var newPosition = ChunkPosition.fromAbsolute(position);

		if (newPosition.equals(centerChunkPosition)) {
			return false;
		}

		centerChunkPosition = newPosition;
		return true;
	}

}
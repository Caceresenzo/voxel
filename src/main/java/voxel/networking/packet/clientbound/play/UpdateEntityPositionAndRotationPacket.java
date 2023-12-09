package voxel.networking.packet.clientbound.play;

import java.io.IOException;
import java.util.UUID;

import org.joml.Vector3fc;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record UpdateEntityPositionAndRotationPacket(
	UUID playerId,
	float x,
	float y,
	float z,
	float yaw,
	float pitch
) implements Packet {

	public UpdateEntityPositionAndRotationPacket(UUID playerId, Vector3fc position, float yaw, float pitch) {
		this(
			playerId,
			position.x(),
			position.y(),
			position.z(),
			yaw,
			pitch
		);
	}

	public static final PacketSerializer<UpdateEntityPositionAndRotationPacket> SERIALIZER = new PacketSerializer<UpdateEntityPositionAndRotationPacket>() {

		@Override
		public void serialize(UpdateEntityPositionAndRotationPacket packet, BufferWritter output) throws IOException {
			output.writeUUID(packet.playerId);
			output.writeFloat(packet.x);
			output.writeFloat(packet.y);
			output.writeFloat(packet.z);
			output.writeFloat(packet.yaw);
			output.writeFloat(packet.pitch);
		}

		@Override
		public UpdateEntityPositionAndRotationPacket deserialize(BufferReader input) throws IOException {
			final var playerId = input.readUUID();
			final var x = input.readFloat();
			final var y = input.readFloat();
			final var z = input.readFloat();
			final var yaw = input.readFloat();
			final var pitch = input.readFloat();

			return new UpdateEntityPositionAndRotationPacket(playerId, x, y, z, yaw, pitch);
		}

	};

}
package voxel.networking.packet.clientbound.play;

import java.io.IOException;

import org.joml.Vector3fc;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record SynchronizePlayerPositionPacket(
	float x,
	float y,
	float z,
	float yaw,
	float pitch,
	int teleportId
) implements Packet {

	public SynchronizePlayerPositionPacket(Vector3fc position, float yaw, float pitch, int teleportId) {
		this(
			position.x(),
			position.y(),
			position.z(),
			yaw,
			pitch,
			teleportId
		);
	}

	public static final PacketSerializer<SynchronizePlayerPositionPacket> SERIALIZER = new PacketSerializer<SynchronizePlayerPositionPacket>() {

		@Override
		public void serialize(SynchronizePlayerPositionPacket packet, BufferWritter output) throws IOException {
			output.writeFloat(packet.x);
			output.writeFloat(packet.y);
			output.writeFloat(packet.z);
			output.writeFloat(packet.yaw);
			output.writeFloat(packet.pitch);
			output.writeInt(packet.teleportId);
		}

		@Override
		public SynchronizePlayerPositionPacket deserialize(BufferReader input) throws IOException {
			final var x = input.readFloat();
			final var y = input.readFloat();
			final var z = input.readFloat();
			final var yaw = input.readFloat();
			final var pitch = input.readFloat();
			final var teleportId = input.readInt();

			return new SynchronizePlayerPositionPacket(x, y, z, yaw, pitch, teleportId);
		}

	};

}
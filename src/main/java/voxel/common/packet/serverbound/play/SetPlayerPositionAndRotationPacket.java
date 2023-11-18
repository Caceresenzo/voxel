package voxel.common.packet.serverbound.play;

import java.io.IOException;

import voxel.common.data.BufferReader;
import voxel.common.data.BufferWritter;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record SetPlayerPositionAndRotationPacket(
	float x,
	float y,
	float z,
	float yaw,
	float pitch
) implements Packet {

	public static final PacketSerializer<SetPlayerPositionAndRotationPacket> SERIALIZER = new PacketSerializer<SetPlayerPositionAndRotationPacket>() {

		@Override
		public void serialize(SetPlayerPositionAndRotationPacket packet, BufferWritter output) throws IOException {
			output.writeFloat(packet.x);
			output.writeFloat(packet.y);
			output.writeFloat(packet.z);
			output.writeFloat(packet.yaw);
			output.writeFloat(packet.pitch);
		}

		@Override
		public SetPlayerPositionAndRotationPacket deserialize(BufferReader input) throws IOException {
			final var x = input.readFloat();
			final var y = input.readFloat();
			final var z = input.readFloat();
			final var yaw = input.readFloat();
			final var pitch = input.readFloat();
			
			return new SetPlayerPositionAndRotationPacket(x, y, z, yaw, pitch);
		}

	};

}
package voxel.networking.packet.clientbound.login;

import java.io.IOException;
import java.util.UUID;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record LoginSuccessPacket(
	UUID uuid,
	String name
) implements Packet {

	public static final PacketSerializer<LoginSuccessPacket> SERIALIZER = new PacketSerializer<LoginSuccessPacket>() {

		@Override
		public void serialize(LoginSuccessPacket packet, BufferWritter output) throws IOException {
			output.writeUUID(packet.uuid);
			output.writeAsciiString(packet.name);
		}

		@Override
		public LoginSuccessPacket deserialize(BufferReader input) throws IOException {
			final var uuid = new UUID(input.readLong(), input.readLong());
			final var name = input.readAsciiString();

			return new LoginSuccessPacket(uuid, name);
		}

	};

}
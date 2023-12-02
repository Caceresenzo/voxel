package voxel.networking.packet.serverbound.login;

import java.io.IOException;
import java.util.UUID;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record LoginStartPacket(
	UUID uuid,
	String login
) implements Packet {

	public static final PacketSerializer<LoginStartPacket> SERIALIZER = new PacketSerializer<LoginStartPacket>() {

		@Override
		public void serialize(LoginStartPacket packet, BufferWritter output) throws IOException {
			output.writeUUID(packet.uuid);
			output.writeAsciiString(packet.login);
		}

		@Override
		public LoginStartPacket deserialize(BufferReader input) throws IOException {
			final var uuid = input.readUUID();
			final var name = input.readAsciiString();

			return new LoginStartPacket(uuid, name);
		}

	};

}
package voxel.common.packet.serverbound.login;

import java.io.IOException;
import java.util.UUID;

import voxel.common.data.BufferReader;
import voxel.common.data.BufferWritter;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

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
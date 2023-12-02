package voxel.networking.packet.clientbound.status;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record StatusResponsePacket(
	String serverName,
	int playerCount
) implements Packet {

	public static final PacketSerializer<StatusResponsePacket> SERIALIZER = new PacketSerializer<StatusResponsePacket>() {

		@Override
		public void serialize(StatusResponsePacket packet, BufferWritter output) throws IOException {
			output.writeAsciiString(packet.serverName);
			output.writeInt(packet.playerCount);
		}

		@Override
		public StatusResponsePacket deserialize(BufferReader input) throws IOException {
			final var serverName = input.readAsciiString();
			final var playerCount = input.readInt();

			return new StatusResponsePacket(serverName, playerCount);
		}

	};

}
package voxel.common.packet.clientbound.status;

import java.io.IOException;

import voxel.common.data.BufferReader;
import voxel.common.data.BufferWritter;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record StatusResponsePacket(
	String serverName,
	long playerCount
) implements Packet {

	public static final PacketSerializer<StatusResponsePacket> SERIALIZER = new PacketSerializer<StatusResponsePacket>() {

		@Override
		public void serialize(StatusResponsePacket packet, BufferWritter output) throws IOException {
			output.writeAsciiString(packet.serverName);
			output.writeLong(packet.playerCount);
		}

		@Override
		public StatusResponsePacket deserialize(BufferReader input) throws IOException {
			final var serverName = input.readAsciiString();
			final var playerCount = input.readLong();

			return new StatusResponsePacket(serverName, playerCount);
		}

	};

}
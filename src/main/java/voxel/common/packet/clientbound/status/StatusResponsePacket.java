package voxel.common.packet.clientbound.status;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketDeserializer;
import voxel.common.packet.PacketIdentifier;
import voxel.common.packet.PacketSerializer;

public record StatusResponsePacket(
	String serverName,
	long playerCount
) implements Packet<StatusResponsePacket> {

	public static PacketIdentifier<StatusResponsePacket> IDENTIFIER = new PacketIdentifier<>(
		0x00,
		"status response",
		new Serializer(),
		new Deserializer()
	);

	@Override
	public PacketIdentifier<StatusResponsePacket> getPacketIdentifier() {
		return IDENTIFIER;
	}

	public static class Serializer implements PacketSerializer<StatusResponsePacket> {

		@Override
		public void serialize(StatusResponsePacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeUTF(packet.serverName);
			dataOutput.writeLong(packet.playerCount);
		}

	}

	public static class Deserializer implements PacketDeserializer<StatusResponsePacket> {

		@Override
		public StatusResponsePacket deserialize(DataInput dataInput) throws IOException {
			final var serverName = dataInput.readUTF();
			final var playerCount = dataInput.readLong();

			return new StatusResponsePacket(serverName, playerCount);
		}

	}

}
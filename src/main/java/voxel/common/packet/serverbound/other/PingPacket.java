package voxel.common.packet.serverbound.other;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketDeserializer;
import voxel.common.packet.PacketIdentifier;
import voxel.common.packet.PacketSerializer;

public record PingPacket(
	long payload
) implements Packet<PingPacket> {

	public static PacketIdentifier<PingPacket> IDENTIFIER = new PacketIdentifier<>(
		0x01,
		"ping",
		new Serializer(),
		new Deserializer()
	);

	@Override
	public PacketIdentifier<PingPacket> getPacketIdentifier() {
		return IDENTIFIER;
	}

	public static class Serializer implements PacketSerializer<PingPacket> {

		@Override
		public void serialize(PingPacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeLong(packet.payload);
		}

	}

	public static class Deserializer implements PacketDeserializer<PingPacket> {

		@Override
		public PingPacket deserialize(DataInput dataInput) throws IOException {
			final var payload = dataInput.readLong();

			return new PingPacket(payload);
		}

	}

}
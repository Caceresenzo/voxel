package voxel.common.packet.clientbound.other;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketDeserializer;
import voxel.common.packet.PacketIdentifier;
import voxel.common.packet.PacketSerializer;

public record PongPacket(
	long payload
) implements Packet<PongPacket> {

	public static PacketIdentifier<PongPacket> IDENTIFIER = new PacketIdentifier<>(
		0x01,
		"pong",
		new Serializer(),
		new Deserializer()
	);

	@Override
	public PacketIdentifier<PongPacket> getPacketIdentifier() {
		return IDENTIFIER;
	}

	public static class Serializer implements PacketSerializer<PongPacket> {

		@Override
		public void serialize(PongPacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeLong(packet.payload);
		}

	}

	public static class Deserializer implements PacketDeserializer<PongPacket> {

		@Override
		public PongPacket deserialize(DataInput dataInput) throws IOException {
			final var payload = dataInput.readLong();

			return new PongPacket(payload);
		}

	}

}
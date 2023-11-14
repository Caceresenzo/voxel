package voxel.common.packet.clientbound.other;

import java.io.IOException;

import voxel.common.data.BufferReader;
import voxel.common.data.BufferWritter;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record PongPacket(
	long payload
) implements Packet {

	public static final PacketSerializer<PongPacket> SERIALIZER = new PacketSerializer<PongPacket>() {

		@Override
		public void serialize(PongPacket packet, BufferWritter output) throws IOException {
			output.writeLong(packet.payload);
		}

		@Override
		public PongPacket deserialize(BufferReader input) throws IOException {
			final var payload = input.readLong();

			return new PongPacket(payload);
		}

	};

}
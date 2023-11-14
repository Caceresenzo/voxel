package voxel.common.packet.serverbound.other;

import java.io.IOException;

import voxel.common.data.BufferReader;
import voxel.common.data.BufferWritter;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record PingPacket(
	long payload
) implements Packet {

	public static final PacketSerializer<PingPacket> SERIALIZER = new PacketSerializer<PingPacket>() {

		@Override
		public void serialize(PingPacket packet, BufferWritter output) throws IOException {
			output.writeLong(packet.payload);
		}

		@Override
		public PingPacket deserialize(BufferReader input) throws IOException {
			final var payload = input.readLong();

			return new PingPacket(payload);
		}

	};

}
package voxel.networking.packet.serverbound.other;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

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
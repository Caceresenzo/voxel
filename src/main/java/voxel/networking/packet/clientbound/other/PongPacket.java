package voxel.networking.packet.clientbound.other;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

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
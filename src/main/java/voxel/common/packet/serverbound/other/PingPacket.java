package voxel.common.packet.serverbound.other;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record PingPacket(
	long payload
) implements Packet {

	public static final PacketSerializer<PingPacket> SERIALIZER = new PacketSerializer<PingPacket>() {

		@Override
		public void serialize(PingPacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeLong(packet.payload);
		}

		@Override
		public PingPacket deserialize(DataInput dataInput) throws IOException {
			final var payload = dataInput.readLong();

			return new PingPacket(payload);
		}

	};

}
package voxel.common.packet.clientbound.other;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record PongPacket(
	long payload
) implements Packet {

	public static final PacketSerializer<PongPacket> SERIALIZER = new PacketSerializer<PongPacket>() {

		@Override
		public void serialize(PongPacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeLong(packet.payload);
		}

		@Override
		public PongPacket deserialize(DataInput dataInput) throws IOException {
			final var payload = dataInput.readLong();

			return new PongPacket(payload);
		}

	};

}
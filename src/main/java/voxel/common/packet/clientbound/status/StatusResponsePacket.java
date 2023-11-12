package voxel.common.packet.clientbound.status;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record StatusResponsePacket(
	String serverName,
	long playerCount
) implements Packet {

	public static final PacketSerializer<StatusResponsePacket> SERIALIZER = new PacketSerializer<StatusResponsePacket>() {

		@Override
		public void serialize(StatusResponsePacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeUTF(packet.serverName);
			dataOutput.writeLong(packet.playerCount);
		}

		@Override
		public StatusResponsePacket deserialize(DataInput dataInput) throws IOException {
			final var serverName = dataInput.readUTF();
			final var playerCount = dataInput.readLong();

			return new StatusResponsePacket(serverName, playerCount);
		}

	};

}
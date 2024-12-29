package voxel.networking.packet.serverbound.other;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record ConfirmTeleportationPacket(
	int teleportId
) implements Packet {

	public static final PacketSerializer<ConfirmTeleportationPacket> SERIALIZER = new PacketSerializer<ConfirmTeleportationPacket>() {

		@Override
		public void serialize(ConfirmTeleportationPacket packet, BufferWritter output) throws IOException {
			output.writeInt(packet.teleportId);
		}

		@Override
		public ConfirmTeleportationPacket deserialize(BufferReader input) throws IOException {
			final var teleportId = input.readInt();

			return new ConfirmTeleportationPacket(teleportId);
		}

	};

}
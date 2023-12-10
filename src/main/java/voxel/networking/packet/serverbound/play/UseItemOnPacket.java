package voxel.networking.packet.serverbound.play;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.shared.block.BlockPosition;
import voxel.shared.chunk.Face;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record UseItemOnPacket(
	BlockPosition blockPosition,
	Face face
) implements Packet {

	public UseItemOnPacket {
		if (!face.isCartesian()) {
			throw new IllegalArgumentException("face is not cartesian");
		}
	}

	public static final PacketSerializer<UseItemOnPacket> SERIALIZER = new PacketSerializer<UseItemOnPacket>() {

		@Override
		public void serialize(UseItemOnPacket packet, BufferWritter output) throws IOException {
			output.writeBlockPosition(packet.blockPosition);
			output.writeByte((byte) packet.face.ordinal());
		}

		@Override
		public UseItemOnPacket deserialize(BufferReader input) throws IOException {
			final var blockPosition = input.readBlockPosition();
			final var face = Face.valueOfCardinal(input.readByte());

			return new UseItemOnPacket(blockPosition, face);
		}

	};

}
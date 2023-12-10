package voxel.networking.packet.clientbound.play;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.shared.block.BlockPosition;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record BlockUpdatePacket(
	BlockPosition position,
	byte id
) implements Packet {

	public static final PacketSerializer<BlockUpdatePacket> SERIALIZER = new PacketSerializer<BlockUpdatePacket>() {

		@Override
		public void serialize(BlockUpdatePacket packet, BufferWritter output) throws IOException {
			output.writeBlockPosition(packet.position);
			output.writeByte(packet.id);
		}

		@Override
		public BlockUpdatePacket deserialize(BufferReader input) throws IOException {
			final var position = input.readBlockPosition();
			final var id = input.readByte();

			return new BlockUpdatePacket(position, id);
		}

	};

}
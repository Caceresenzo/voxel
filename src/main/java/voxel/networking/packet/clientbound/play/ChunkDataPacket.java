package voxel.networking.packet.clientbound.play;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.shared.chunk.ChunkPosition;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record ChunkDataPacket(
	ChunkPosition position,
	byte[] voxels
) implements Packet {

	public static final PacketSerializer<ChunkDataPacket> SERIALIZER = new PacketSerializer<ChunkDataPacket>() {

		@Override
		public void serialize(ChunkDataPacket packet, BufferWritter output) throws IOException {
			output.writeChunkPosition(packet.position);
			output.writeByteArray(packet.voxels);
		}

		@Override
		public ChunkDataPacket deserialize(BufferReader input) throws IOException {
			final var position = input.readChunkPosition();
			final var voxels = input.readByteArray();

			return new ChunkDataPacket(position, voxels);
		}

	};

}
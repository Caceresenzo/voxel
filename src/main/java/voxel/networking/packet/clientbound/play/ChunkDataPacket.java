package voxel.networking.packet.clientbound.play;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record ChunkDataPacket(
	int chunkX,
	int chunkZ,
	byte[] voxelIds
) implements Packet {

	public static final PacketSerializer<ChunkDataPacket> SERIALIZER = new PacketSerializer<ChunkDataPacket>() {

		@Override
		public void serialize(ChunkDataPacket packet, BufferWritter output) throws IOException {
			output.writeInt(packet.chunkX);
			output.writeInt(packet.chunkZ);
			output.writeInt(packet.voxelIds.length);
			output.write(packet.voxelIds);
		}

		@Override
		public ChunkDataPacket deserialize(BufferReader input) throws IOException {
			final var chunkX = input.readInt();
			final var chunkZ = input.readInt();
			final var voxelIdsLength = input.readInt();

			final var voxelIds = new byte[voxelIdsLength];
			input.read(voxelIds);

			return new ChunkDataPacket(chunkX, chunkZ, voxelIds);
		}

	};

}
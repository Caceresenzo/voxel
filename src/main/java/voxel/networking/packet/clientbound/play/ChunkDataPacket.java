package voxel.networking.packet.clientbound.play;

import java.io.IOException;

import org.joml.Vector3ic;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record ChunkDataPacket(
	int x,
	int y,
	int z,
	byte[] voxelIds
) implements Packet {

	public ChunkDataPacket(Vector3ic position, byte[] voxelIds) {
		this(position.x(), position.y(), position.z(), voxelIds);
	}

	public static final PacketSerializer<ChunkDataPacket> SERIALIZER = new PacketSerializer<ChunkDataPacket>() {

		@Override
		public void serialize(ChunkDataPacket packet, BufferWritter output) throws IOException {
			output.writeInt(packet.x);
			output.writeInt(packet.y);
			output.writeInt(packet.z);
			output.writeInt(packet.voxelIds.length);
			output.write(packet.voxelIds);
		}

		@Override
		public ChunkDataPacket deserialize(BufferReader input) throws IOException {
			final var x = input.readInt();
			final var y = input.readInt();
			final var z = input.readInt();
			final var voxelIdsLength = input.readInt();

			final var voxelIds = new byte[voxelIdsLength];
			input.read(voxelIds);

			return new ChunkDataPacket(x, y, z, voxelIds);
		}

	};

}
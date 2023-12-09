package voxel.networking.packet.clientbound.play;

import java.io.IOException;

import org.joml.Vector3ic;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record BlockUpdatePacket(
	int x,
	int y,
	int z,
	byte id
) implements Packet {

	public BlockUpdatePacket(Vector3ic position, byte id) {
		this(
			position.x(),
			position.y(),
			position.z(),
			id
		);
	}

	public static final PacketSerializer<BlockUpdatePacket> SERIALIZER = new PacketSerializer<BlockUpdatePacket>() {

		@Override
		public void serialize(BlockUpdatePacket packet, BufferWritter output) throws IOException {
			output.writeInt(packet.x);
			output.writeInt(packet.y);
			output.writeInt(packet.z);
			output.writeByte(packet.id);
		}

		@Override
		public BlockUpdatePacket deserialize(BufferReader input) throws IOException {
			final var x = input.readInt();
			final var y = input.readInt();
			final var z = input.readInt();
			final var id = input.readByte();

			return new BlockUpdatePacket(x, y, z, id);
		}

	};

}
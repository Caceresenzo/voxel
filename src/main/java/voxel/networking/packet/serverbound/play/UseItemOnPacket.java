package voxel.networking.packet.serverbound.play;

import java.io.IOException;

import org.joml.Vector3i;
import org.joml.Vector3ic;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.shared.chunk.Face;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record UseItemOnPacket(
	int blockX,
	int blockY,
	int blockZ,
	Face face
) implements Packet {

	public UseItemOnPacket {
		if (!face.isCartesian()) {
			throw new IllegalArgumentException("face is not cartesian");
		}
	}

	public UseItemOnPacket(Vector3ic blockPosition, Face face) {
		this(
			blockPosition.x(),
			blockPosition.y(),
			blockPosition.z(),
			face
		);
	}

	public Vector3i blockPosition() {
		return new Vector3i(blockX, blockY, blockZ);
	}

	public static final PacketSerializer<UseItemOnPacket> SERIALIZER = new PacketSerializer<UseItemOnPacket>() {

		@Override
		public void serialize(UseItemOnPacket packet, BufferWritter output) throws IOException {
			output.writeInt(packet.blockX);
			output.writeInt(packet.blockY);
			output.writeInt(packet.blockZ);
			output.writeByte((byte) packet.face.ordinal());
		}

		@Override
		public UseItemOnPacket deserialize(BufferReader input) throws IOException {
			final var blockX = input.readInt();
			final var blockY = input.readInt();
			final var blockZ = input.readInt();
			final var face = Face.valueOfCardinal(input.readByte());

			return new UseItemOnPacket(blockX, blockY, blockZ, face);
		}

	};

}
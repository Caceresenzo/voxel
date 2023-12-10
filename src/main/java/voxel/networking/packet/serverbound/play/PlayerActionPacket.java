package voxel.networking.packet.serverbound.play;

import java.io.IOException;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.networking.packet.serverbound.play.PlayerActionPacket.Status;
import voxel.shared.block.BlockPosition;
import voxel.shared.chunk.Face;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

@SuppressWarnings("unused")
public record PlayerActionPacket(
	Status status,
	BlockPosition blockPosition,
	Face face
) implements Packet {

	public PlayerActionPacket {
		if (!face.isCartesian()) {
			throw new IllegalArgumentException("face is not cartesian");
		}
	}

	public static PlayerActionPacket startedDigging(BlockPosition blockPosition, Face face) {
		return new PlayerActionPacket(
			Status.STARTED_DIGGING,
			blockPosition,
			face
		);
	}

	public static final PacketSerializer<PlayerActionPacket> SERIALIZER = new PacketSerializer<PlayerActionPacket>() {

		@Override
		public void serialize(PlayerActionPacket packet, BufferWritter output) throws IOException {
			output.writeByte((byte) packet.status.ordinal());
			output.writeBlockPosition(packet.blockPosition);
			output.writeByte((byte) packet.face.ordinal());
		}

		@Override
		public PlayerActionPacket deserialize(BufferReader input) throws IOException {
			final var status = Status.valueOf(input.readByte());
			final var blockPosition = input.readBlockPosition();
			final var face = Face.valueOfCardinal(input.readByte());

			return new PlayerActionPacket(status, blockPosition, face);
		}

	};

	public enum Status {

		STARTED_DIGGING;

		private static final Status[] VALUES = Status.values();

		public static Status valueOf(int ordinal) {
			return VALUES[ordinal];
		}

	}

}
package voxel.networking.packet.serverbound.play;

import java.io.IOException;

import org.joml.Vector3ic;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.networking.packet.serverbound.play.PlayerActionPacket.Status;
import voxel.shared.chunk.Face;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

@SuppressWarnings("unused")
public record PlayerActionPacket(
	Status status,
	int blockX,
	int blockY,
	int blockZ,
	Face face
) implements Packet {

	public PlayerActionPacket {
		if (!face.isCartesian()) {
			throw new IllegalArgumentException("face is not cartesian");
		}
	}

	public PlayerActionPacket(Status status, Vector3ic blockPosition, Face face) {
		this(
			status,
			blockPosition.x(),
			blockPosition.y(),
			blockPosition.z(),
			face
		);
	}

	public static PlayerActionPacket startedDigging(Vector3ic blockPosition, Face face) {
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
			output.writeInt(packet.blockX);
			output.writeInt(packet.blockY);
			output.writeInt(packet.blockZ);
			output.writeByte((byte) packet.face.ordinal());
		}

		@Override
		public PlayerActionPacket deserialize(BufferReader input) throws IOException {
			final var status = Status.valueOf(input.readByte());
			final var blockX = input.readInt();
			final var blockY = input.readInt();
			final var blockZ = input.readInt();
			final var face = Face.valueOfCardinal(input.readByte());

			return new PlayerActionPacket(status, blockX, blockY, blockZ, face);
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
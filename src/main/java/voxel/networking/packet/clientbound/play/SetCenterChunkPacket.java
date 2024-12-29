package voxel.networking.packet.clientbound.play;

import java.io.IOException;
import java.util.Objects;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.shared.chunk.ChunkPosition;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record SetCenterChunkPacket(
	ChunkPosition position
) implements Packet {

	public SetCenterChunkPacket {
		Objects.requireNonNull(position);
	}

	public static final PacketSerializer<SetCenterChunkPacket> SERIALIZER = new PacketSerializer<SetCenterChunkPacket>() {

		@Override
		public void serialize(SetCenterChunkPacket packet, BufferWritter output) throws IOException {
			output.writeChunkPosition(packet.position);
		}

		@Override
		public SetCenterChunkPacket deserialize(BufferReader input) throws IOException {
			final var position = input.readChunkPosition();

			return new SetCenterChunkPacket(position);
		}

	};

}
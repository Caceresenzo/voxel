package voxel.networking.packet.clientbound.play;

import java.io.IOException;
import java.util.Objects;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record GameEventPacket(
	Event event,
	float value
) implements Packet {

	public GameEventPacket {
		Objects.requireNonNull(event);
	}

	public static GameEventPacket startWaitingForLevelChunks() {
		return new GameEventPacket(Event.START_WAITING_FOR_LEVEL_CHUNKS, 0);
	}

	public enum Event {

		START_WAITING_FOR_LEVEL_CHUNKS;

		private static final Event[] VALUES = Event.values();

		public static Event valueOf(int ordinal) {
			return VALUES[ordinal];
		}

	}

	public static final PacketSerializer<GameEventPacket> SERIALIZER = new PacketSerializer<GameEventPacket>() {

		@Override
		public void serialize(GameEventPacket packet, BufferWritter output) throws IOException {
			output.writeByte((byte) packet.event.ordinal());
			output.writeFloat(packet.value);
		}

		@Override
		public GameEventPacket deserialize(BufferReader input) throws IOException {
			final var event = Event.valueOf(input.readByte());
			final var value = input.readFloat();

			return new GameEventPacket(event, value);
		}

	};

}
package voxel.common.packet.serverbound.handshake;

import java.io.IOException;

import voxel.common.data.BufferReader;
import voxel.common.data.BufferWritter;
import voxel.common.packet.ConnectionState;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record HandshakePacket(
	ConnectionState nextState
) implements Packet {

	public HandshakePacket {
		if (!ConnectionState.STATUS.equals(nextState) && !ConnectionState.LOGIN.equals(nextState)) {
			throw new IllegalArgumentException("nextState can only be STATUS or LOGIN: " + nextState);
		}
	}

	public static final PacketSerializer<HandshakePacket> SERIALIZER = new PacketSerializer<HandshakePacket>() {

		private final ConnectionState[] values = ConnectionState.values();

		@Override
		public void serialize(HandshakePacket packet, BufferWritter output) throws IOException {
			output.writeByte((byte) packet.nextState.ordinal());
		}

		@Override
		public HandshakePacket deserialize(BufferReader input) throws IOException {
			final var nextState = values[input.readByte()];

			return new HandshakePacket(nextState);
		}

	};

}
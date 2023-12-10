package voxel.networking.packet.serverbound.handshake;

import java.io.IOException;

import voxel.networking.packet.ConnectionState;
import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public record HandshakePacket(
	ConnectionState nextState
) implements Packet {

	public HandshakePacket {
		if (!ConnectionState.STATUS.equals(nextState) && !ConnectionState.LOGIN.equals(nextState)) {
			throw new IllegalArgumentException("nextState can only be STATUS or LOGIN: " + nextState);
		}
	}

	public static final PacketSerializer<HandshakePacket> SERIALIZER = new PacketSerializer<HandshakePacket>() {

		@Override
		public void serialize(HandshakePacket packet, BufferWritter output) throws IOException {
			output.writeByte((byte) packet.nextState.ordinal());
		}

		@Override
		public HandshakePacket deserialize(BufferReader input) throws IOException {
			final var nextState = ConnectionState.valueOf(input.readByte());

			return new HandshakePacket(nextState);
		}

	};

}
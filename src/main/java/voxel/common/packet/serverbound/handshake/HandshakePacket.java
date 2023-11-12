package voxel.common.packet.serverbound.handshake;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

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

		@Override
		public void serialize(HandshakePacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeByte(packet.nextState.ordinal());
		}

		@Override
		public HandshakePacket deserialize(DataInput dataInput) throws IOException {
			final var nextState = ConnectionState.values()[dataInput.readByte()];

			return new HandshakePacket(nextState);
		}

	};

}
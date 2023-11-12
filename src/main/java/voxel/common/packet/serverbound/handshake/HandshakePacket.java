package voxel.common.packet.serverbound.handshake;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import voxel.common.packet.ConnectionState;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketDeserializer;
import voxel.common.packet.PacketIdentifier;
import voxel.common.packet.PacketSerializer;

public record HandshakePacket(
	ConnectionState nextState
) implements Packet<HandshakePacket> {

	public static PacketIdentifier<HandshakePacket> IDENTIFIER = new PacketIdentifier<>(
		0x00,
		"handshake",
		new Serializer(),
		new Deserializer()
	);

	public HandshakePacket {
		if (!ConnectionState.STATUS.equals(nextState) && !ConnectionState.LOGIN.equals(nextState)) {
			throw new IllegalArgumentException("nextState can only be STATUS or LOGIN: " + nextState);
		}
	}

	@Override
	public PacketIdentifier<HandshakePacket> getPacketIdentifier() {
		return IDENTIFIER;
	}

	public static class Serializer implements PacketSerializer<HandshakePacket> {

		@Override
		public void serialize(HandshakePacket packet, DataOutput dataOutput) throws IOException {
			dataOutput.writeByte(packet.nextState.ordinal());
		}

	}

	public static class Deserializer implements PacketDeserializer<HandshakePacket> {

		@Override
		public HandshakePacket deserialize(DataInput dataInput) throws IOException {
			final var nextState = ConnectionState.values()[dataInput.readByte()];

			return new HandshakePacket(nextState);
		}

	}

}
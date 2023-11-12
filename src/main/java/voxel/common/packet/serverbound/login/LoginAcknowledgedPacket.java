package voxel.common.packet.serverbound.login;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketDeserializer;
import voxel.common.packet.PacketIdentifier;
import voxel.common.packet.PacketSerializer;

public record LoginAcknowledgedPacket() implements Packet<LoginAcknowledgedPacket> {

	public static PacketIdentifier<LoginAcknowledgedPacket> IDENTIFIER = new PacketIdentifier<>(
		0x03,
		"login acknowledged",
		PacketSerializer.nothing(),
		PacketDeserializer.nothing(LoginAcknowledgedPacket::new)
	);

	@Override
	public PacketIdentifier<LoginAcknowledgedPacket> getPacketIdentifier() {
		return IDENTIFIER;
	}

}
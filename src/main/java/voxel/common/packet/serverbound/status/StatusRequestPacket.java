package voxel.common.packet.serverbound.status;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketDeserializer;
import voxel.common.packet.PacketIdentifier;
import voxel.common.packet.PacketSerializer;

public record StatusRequestPacket() implements Packet<StatusRequestPacket> {

	public static PacketIdentifier<StatusRequestPacket> IDENTIFIER = new PacketIdentifier<>(
		0x00,
		"status request",
		PacketSerializer.nothing(),
		PacketDeserializer.nothing(StatusRequestPacket::new)
	);

	@Override
	public PacketIdentifier<StatusRequestPacket> getPacketIdentifier() {
		return IDENTIFIER;
	}

}
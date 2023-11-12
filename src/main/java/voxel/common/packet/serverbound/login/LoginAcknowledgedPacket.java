package voxel.common.packet.serverbound.login;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record LoginAcknowledgedPacket() implements Packet {

	public static final PacketSerializer<LoginAcknowledgedPacket> SERIALIZER = PacketSerializer.noField(LoginAcknowledgedPacket::new);

}
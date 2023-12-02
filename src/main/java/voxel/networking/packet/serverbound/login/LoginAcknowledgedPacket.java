package voxel.networking.packet.serverbound.login;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;

public record LoginAcknowledgedPacket() implements Packet {

	public static final PacketSerializer<LoginAcknowledgedPacket> SERIALIZER = PacketSerializer.noField(LoginAcknowledgedPacket::new);

}
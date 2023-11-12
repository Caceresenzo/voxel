package voxel.common.packet.serverbound.status;

import voxel.common.packet.Packet;
import voxel.common.packet.PacketSerializer;

public record StatusRequestPacket() implements Packet {

	public static final PacketSerializer<StatusRequestPacket> SERIALIZER = PacketSerializer.noField(StatusRequestPacket::new);

}
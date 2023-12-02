package voxel.networking.packet.serverbound.status;

import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketSerializer;

public record StatusRequestPacket() implements Packet {

	public static final PacketSerializer<StatusRequestPacket> SERIALIZER = PacketSerializer.noField(StatusRequestPacket::new);

}
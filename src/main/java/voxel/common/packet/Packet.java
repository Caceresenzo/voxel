package voxel.common.packet;

public interface Packet<T extends Packet<T>> {

	PacketIdentifier<T> getPacketIdentifier();
	
}
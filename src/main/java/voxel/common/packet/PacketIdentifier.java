package voxel.common.packet;

public record PacketIdentifier<T extends Packet>(
	ConnectionState state,
	int number,
	Class<T> clazz,
	PacketSerializer<T> serializer
) {}
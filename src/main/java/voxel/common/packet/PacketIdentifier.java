package voxel.common.packet;

public record PacketIdentifier<T extends Packet<T>>(
	int number,
	String name,
	PacketSerializer<T> serializer,
	PacketDeserializer<T> deserializer
) {}
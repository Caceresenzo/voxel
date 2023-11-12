package voxel.common.packet;

import java.io.DataOutput;
import java.io.IOException;

@FunctionalInterface
public interface PacketSerializer<T extends Packet<T>> {

	void serialize(T packet, DataOutput dataOutput) throws IOException;
	
	public static <T extends Packet<T>> PacketSerializer<T> nothing() {
		return (packet, dataOutput) -> {};
	}
	
}
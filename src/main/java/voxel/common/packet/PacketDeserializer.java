package voxel.common.packet;

import java.io.DataInput;
import java.io.IOException;
import java.util.function.Supplier;

@FunctionalInterface
public interface PacketDeserializer<T extends Packet<T>> {

	T deserialize(DataInput dataInput) throws IOException;
	
	public static <T extends Packet<T>> PacketDeserializer<T> nothing(Supplier<T> supplier) {
		return (dataInput) -> supplier.get();
	}
	
}
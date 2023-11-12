package voxel.common.packet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.Supplier;

public interface PacketSerializer<T extends Packet> {

	void serialize(T packet, DataOutput dataOutput) throws IOException;

	T deserialize(DataInput dataInput) throws IOException;

	public static <T extends Packet> PacketSerializer<T> noField(Supplier<T> factory) {
		return new NoFieldSerializer<>(factory);
	}

	static record NoFieldSerializer<T extends Packet>(
		Supplier<T> factory
	) implements PacketSerializer<T> {

		@Override
		public void serialize(T packet, DataOutput dataOutput) throws IOException {

		}

		@Override
		public T deserialize(DataInput dataInput) throws IOException {
			return factory.get();
		}

	}

}
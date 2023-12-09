package voxel.networking.packet;

import java.io.IOException;
import java.util.function.Supplier;

import voxel.util.data.BufferReader;
import voxel.util.data.BufferWritter;

public interface PacketSerializer<T extends Packet> {

	void serialize(T packet, BufferWritter output) throws IOException;

	T deserialize(BufferReader input) throws IOException;

	public static <T extends Packet> PacketSerializer<T> noField(Supplier<T> factory) {
		return new NoFieldSerializer<>(factory);
	}

	static record NoFieldSerializer<T extends Packet>(
		Supplier<T> factory
	) implements PacketSerializer<T> {

		@Override
		public void serialize(T packet, BufferWritter output) throws IOException {
		}

		@Override
		public T deserialize(BufferReader input) throws IOException {
			return factory.get();
		}

	}

}
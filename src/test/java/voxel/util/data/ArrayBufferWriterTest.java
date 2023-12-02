package voxel.util.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ArrayBufferWriterTest {

	@Test
	void writeBoolean() throws IOException {
		final var array = new byte[1];
		final var buffer = new ArrayBufferWriter(array);

		buffer.writeBoolean(true);

		assertArrayEquals(new byte[] { 1 }, array);
	}

	@Test
	void writeByte() throws IOException {
		final var array = new byte[1];
		final var buffer = new ArrayBufferWriter(array);

		buffer.writeByte((byte) 42);

		assertArrayEquals(new byte[] { 42 }, array);
	}

	@Test
	void writeShort() throws IOException {
		final var array = new byte[2];
		final var buffer = new ArrayBufferWriter(array);

		buffer.writeShort((short) 12345);

		assertArrayEquals(new byte[] { 48, 57 }, array);
	}

	@Test
	void writeInt() throws IOException {
		final var array = new byte[4];
		final var buffer = new ArrayBufferWriter(array);

		buffer.writeInt(16909060);

		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, array);
	}

	@Test
	void writeLong() throws IOException {
		final var array = new byte[8];
		final var buffer = new ArrayBufferWriter(array);

		buffer.writeLong(72623859790382856l);

		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 }, array);
	}

	@Test
	void writeAsciiString() throws IOException {
		final var array = new byte[7];
		final var buffer = new ArrayBufferWriter(array);

		buffer.writeAsciiString("hello");

		assertArrayEquals(new byte[] { 0, 5, 'h', 'e', 'l', 'l', 'o' }, array);
	}

	@Test
	void writeUUID() throws IOException {
		final var array = new byte[16];
		final var buffer = new ArrayBufferWriter(array);

		final var uuid = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
		buffer.writeUUID(uuid);

		assertArrayEquals(ArrayBufferReaderTest.toByteArray(uuid), array);
	}

}
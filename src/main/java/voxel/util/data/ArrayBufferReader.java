package voxel.util.data;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArrayBufferReader implements BufferReader {

	private final byte[] buffer = new byte[Long.BYTES];
	private final byte[] array;
	private @Getter int index;

	public ArrayBufferReader resetIndex() {
		index = 0;
		return this;
	}

	@Override
	public int read() throws IOException {
		if (index == this.array.length) {
			return -1;
		}

		return Byte.toUnsignedInt(this.array[index++]);
	}

	@Override
	public void read(byte[] buffer) throws IOException {
		read(buffer, 0, buffer.length);
	}

	@Override
	public void read(byte[] buffer, int offset, int length) throws IOException {
		for (var jndex = 0; jndex < length; ++jndex) {
			buffer[offset + jndex] = readByte();
		}
	}

	@Override
	public boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		final var value = read();

		if (value == -1) {
			throw new EOFException();
		}

		return (byte) value;
	}

	@Override
	public short readShort() throws IOException {
		return (short) readUnsignedShort();
	}

	public int readUnsignedShort() throws IOException {
		read(buffer, 0, Short.BYTES);

		// @formatter:off
		return (
			((buffer[1] & 0xff)     ) +
			((buffer[0] & 0xff) << 8)
		);
		// @formatter:on
	}

	@Override
	public int readInt() throws IOException {
		return (int) readUnsignedInt();
	}

	public long readUnsignedInt() throws IOException {
		read(buffer, 0, Integer.BYTES);

		// @formatter:off
		return (
			((buffer[3] & 0xff)      ) +
			((buffer[2] & 0xff) << 8 ) +
			((buffer[1] & 0xff) << 16) +
			((buffer[0] & 0xff) << 24)
		);
		// @formatter:on
	}

	@Override
	public long readLong() throws IOException {
		read(buffer, 0, Long.BYTES);

		// @formatter:off
		return (
			((long) (buffer[7] & 0xff)      ) +
			((long) (buffer[6] & 0xff) << 8 ) +
			((long) (buffer[5] & 0xff) << 16) +
			((long) (buffer[4] & 0xff) << 24) +
			((long) (buffer[3] & 0xff) << 32) +
			((long) (buffer[2] & 0xff) << 40) +
			((long) (buffer[1] & 0xff) << 48) +
			((long) (buffer[0] & 0xff) << 56)
		);
		// @formatter:on
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public String readAsciiString() throws IOException {
		final var length = readShort();
		final var bytes = new byte[length];
		read(bytes);

		return new String(bytes, StandardCharsets.US_ASCII);
	}

	@Override
	public UUID readUUID() throws IOException {
		return new UUID(readLong(), readLong());
	}

}
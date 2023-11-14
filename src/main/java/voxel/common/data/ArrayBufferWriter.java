package voxel.common.data;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArrayBufferWriter implements BufferWritter {

	private final @Getter byte[] array;
	private @Getter int index;

	public ArrayBufferWriter resetIndex() {
		index = 0;
		return this;
	}

	@Override
	public void write(int value) throws IOException {
		if (index == array.length) {
			throw new EOFException();
		}

		array[index++] = (byte) value;
	}

	@Override
	public void write(byte[] buffer) throws IOException {
		write(buffer, 0, buffer.length);
	}

	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException {
		for (var index = 0; index < length; ++index) {
			write(buffer[offset + index]);
		}
	}

	@Override
	public void writeBoolean(boolean value) throws IOException {
		if (value) {
			write(1);
		} else {
			write(0);
		}
	}

	@Override
	public void writeByte(byte value) throws IOException {
		write(value);
	}

	@Override
	public void writeShort(short value) throws IOException {
		write((value >> 8) & 0xff);
		write(value & 0xff);
	}

	@Override
	public void writeInt(int value) throws IOException {
		write((value >> 24) & 0xff);
		write((value >> 16) & 0xff);
		write((value >> 8) & 0xff);
		write(value & 0xff);
	}

	@Override
	public void writeLong(long value) throws IOException {
		write((byte) (value >> 56));
		write((byte) (value >> 48));
		write((byte) (value >> 40));
		write((byte) (value >> 32));
		write((byte) (value >> 24));
		write((byte) (value >> 16));
		write((byte) (value >> 8));
		write((byte) value);
	}

	@Override
	public void writeFloat(float value) throws IOException {
		writeInt(Float.floatToIntBits(value));
	}

	@Override
	public void writeDouble(double value) throws IOException {
		writeLong(Double.doubleToLongBits(value));
	}

	@Override
	public void writeAsciiString(String value) throws IOException {
		final var bytes = value.getBytes(StandardCharsets.US_ASCII);

		writeShort((short) bytes.length);
		write(bytes);
	}

	@Override
	public void writeUUID(UUID value) throws IOException {
		writeLong(value.getMostSignificantBits());
		writeLong(value.getLeastSignificantBits());
	}

}
package voxel.util.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.EOFException;
import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ArrayBufferReaderTest {

	@Test
	void read() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 1, 2, 3 });

		assertEquals(1, buffer.read());
		assertEquals(2, buffer.read());
		assertEquals(3, buffer.read());
		assertEquals(-1, buffer.read());
	}

	@Test
	void read_buffer() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 1, 2, 3, 4 });

		final var array = new byte[2];

		buffer.read(array);
		assertArrayEquals(new byte[] { 1, 2 }, array);

		buffer.read(array);
		assertArrayEquals(new byte[] { 3, 4 }, array);

		assertThrows(EOFException.class, () -> buffer.read(array));
	}

	@Test
	void read_buffer_offset_length() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 1, 2, 3, 4 });

		final var array = new byte[2];

		buffer.read(array, 1, 1);
		assertArrayEquals(new byte[] { 0, 1 }, array);

		buffer.read(array, 0, 2);
		assertArrayEquals(new byte[] { 2, 3 }, array);

		assertThrows(EOFException.class, () -> buffer.read(array, 0, 2));
	}

	@Test
	void readBoolean() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 0, 1 });

		assertFalse(buffer.readBoolean());
		assertTrue(buffer.readBoolean());

		assertThrows(EOFException.class, () -> buffer.readBoolean());
	}

	@Test
	void readByte() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 0, 1 });

		assertEquals(0, buffer.readByte());
		assertEquals(1, buffer.readByte());

		assertThrows(EOFException.class, () -> buffer.readByte());
	}
	
	@Test
	void readShort() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 1, 1 });
		
		assertEquals(257, buffer.readShort());
		
		assertThrows(EOFException.class, () -> buffer.readByte());
	}
	
	@Test
	void readInt() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 1, 2, 3, 4 });
		
		assertEquals(16909060, buffer.readInt());
		
		assertThrows(EOFException.class, () -> buffer.readByte());
	}
	
	@Test
	void readLong() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 });
		
		assertEquals(72623859790382856l, buffer.readLong());
		
		assertThrows(EOFException.class, () -> buffer.readByte());
	}
	
	@Test
	void readAsciiString() throws IOException {
		final var buffer = new ArrayBufferReader(new byte[] { 0, 5, 'h', 'e', 'l', 'l', 'o' });
		
		assertEquals("hello", buffer.readAsciiString());
		
		assertThrows(EOFException.class, () -> buffer.readByte());
	}
	
	@Test
	void readUUID() throws IOException {
		final var uuid = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479");
		
		final var buffer = new ArrayBufferReader(toByteArray(uuid));
		assertEquals(uuid, buffer.readUUID());
		
		assertThrows(EOFException.class, () -> buffer.readByte());
	}

    public static byte[] toByteArray(UUID uuid) {
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();

        byte[] byteArray = new byte[16];

        for (int i = 0; i < 8; i++) {
            byteArray[i] = (byte) (mostSignificantBits >>> 8 * (7 - i));
            byteArray[i + 8] = (byte) (leastSignificantBits >>> 8 * (7 - i));
        }

        return byteArray;
    }

}
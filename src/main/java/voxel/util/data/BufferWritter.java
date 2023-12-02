package voxel.util.data;

import java.io.IOException;
import java.util.UUID;

public interface BufferWritter {

	void write(int value) throws IOException;

	void write(byte[] buffer) throws IOException;

	void write(byte[] buffer, int offset, int length) throws IOException;

	void writeBoolean(boolean value) throws IOException;

	void writeByte(byte value) throws IOException;

	void writeShort(short value) throws IOException;

	void writeInt(int value) throws IOException;

	void writeLong(long value) throws IOException;

	void writeFloat(float value) throws IOException;

	void writeDouble(double value) throws IOException;

	void writeAsciiString(String value) throws IOException;
	
	void writeUUID(UUID value) throws IOException;

}
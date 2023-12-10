package voxel.util.data;

import java.io.IOException;
import java.util.UUID;

import voxel.shared.block.BlockPosition;
import voxel.shared.chunk.ChunkPosition;

public interface BufferReader {

	int read() throws IOException;

	void read(byte[] buffer) throws IOException;

	void read(byte[] buffer, int offset, int length) throws IOException;

	boolean readBoolean() throws IOException;

	byte readByte() throws IOException;

	short readShort() throws IOException;

	int readInt() throws IOException;

	long readLong() throws IOException;

	float readFloat() throws IOException;

	double readDouble() throws IOException;

	String readAsciiString() throws IOException;

	UUID readUUID() throws IOException;

	ChunkPosition readChunkPosition() throws IOException;

	BlockPosition readBlockPosition() throws IOException;
	
	byte[] readByteArray() throws IOException;

}
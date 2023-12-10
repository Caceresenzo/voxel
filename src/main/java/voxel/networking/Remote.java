package voxel.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;
import voxel.networking.packet.ConnectionState;
import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketRegistry;
import voxel.networking.packet.PacketSerializer;
import voxel.util.data.ArrayBufferReader;
import voxel.util.data.ArrayBufferWriter;

public abstract class Remote {

	public static final int MAX_PACKET_SIZE = 1024 * 32;
	public static final int MAX_PACKET_QUEUE_SIZE = 1024 * 8;

	private final Socket socket;
	protected ConnectionState state = ConnectionState.HANDSHAKE;

	private final PacketRegistry readPacketRegistry;
	private final Thread readThread;
	private final byte[] readBuffer = new byte[MAX_PACKET_SIZE];

	private final PacketRegistry writePacketRegistry;
	private final Thread writeThread;
	private final BlockingQueue<Packet> writeQueue = new ArrayBlockingQueue<>(MAX_PACKET_QUEUE_SIZE, true);
	private final byte[] writeBuffer = new byte[MAX_PACKET_SIZE];

	public Remote(Socket socket, PacketRegistry readPacketRegistry, PacketRegistry writePacketRegistry, ThreadFactory threadFactory) {
		this.socket = socket;

		this.readPacketRegistry = readPacketRegistry;
		this.readThread = threadFactory.newThread(new ReadRunnable());
		this.readThread.setName("%s-read-%d".formatted(getClass().getSimpleName(), socket.getPort()));
		this.writePacketRegistry = writePacketRegistry;
		this.writeThread = threadFactory.newThread(new WriteRunnable());
		this.writeThread.setName("%s-write-%d".formatted(getClass().getSimpleName(), socket.getPort()));
	}

	public void start() {
		readThread.start();
		writeThread.start();
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		readThread.interrupt();
		writeThread.interrupt();
	}

	public boolean offer(Packet packet) {
		final var sizeBefore = writeQueue.size();

		if (writeQueue.offer(packet)) {
			return true;
		}

		System.err.printf("write packet discarded (size=%d): %s", sizeBefore, packet);
		return false;
	}

	public abstract void onPacketReceived(Packet packet);

	public abstract void onPacketSent(Packet packet);

	public class ReadRunnable implements Runnable {

		@Override
		@SneakyThrows
		@SuppressWarnings("rawtypes")
		public void run() {
			final var input = new ArrayBufferReader(readBuffer);

			final var inputStream = new DataInputStream(socket.getInputStream());

			while (socket.isConnected()) {
				try {
					final var length = inputStream.readInt() - Integer.BYTES;
					if (length < 0 || length >= readBuffer.length) {
						throw new RuntimeException("invalid buffer length: " + length);
					}

					final var packetId = inputStream.readInt();
					inputStream.read(readBuffer, 0, length);

					final var identifier = writePacketRegistry.get(state, packetId);
					if (identifier == null) {
						throw new IllegalStateException(Remote.this.getClass().getSimpleName() + ": " + packetId + " is not registered in " + state + " remote registry");
					}

					final PacketSerializer deserializer = identifier.serializer();
					final var packet = deserializer.deserialize(input.resetIndex());

					onPacketReceived(packet);
				} catch (Exception exception) {
					if (Thread.interrupted()) {
						break;
					}

					throw exception;
				}
			}
		}

	}

	public class WriteRunnable implements Runnable {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		@SneakyThrows
		public void run() {
			final var writer = new ArrayBufferWriter(writeBuffer);

			final var outputStream = new DataOutputStream(socket.getOutputStream());

			while (socket.isConnected()) {
				try {
					final var packet = writeQueue.poll(1, TimeUnit.HOURS);
					if (packet == null) {
						continue;
					}

					final var identifier = readPacketRegistry.get(state, packet.getClass());
					if (identifier == null) {
						throw new IllegalStateException(Remote.this.getClass().getSimpleName() + ": " + packet.getClass().getSimpleName() + " is not registered in " + state + " local registry");
					}

					final PacketSerializer serializer = identifier.serializer();
					serializer.serialize(packet, writer.resetIndex());

					final var length = writer.getIndex();
					outputStream.writeInt(length + Integer.BYTES);
					outputStream.writeInt(identifier.number());
					outputStream.write(writeBuffer, 0, length);

					onPacketSent(packet);
				} catch (Exception exception) {
					if (Thread.interrupted()) {
						break;
					}

					throw exception;
				}
			}
		}

	}

}
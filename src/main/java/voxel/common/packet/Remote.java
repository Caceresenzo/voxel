package voxel.common.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public abstract class Remote {

	public static final int MAX_PACKET_SIZE = 2048;
	public static final int MAX_PACKET_QUEUE_SIZE = 128;

	private final Socket socket;
	protected ConnectionState state = ConnectionState.HANDSHAKE;

	private final PacketRegistry readPacketRegistry;
	private final Thread readThread;
	private final byte[] readBuffer = new byte[MAX_PACKET_SIZE];

	private final PacketRegistry writePacketRegistry;
	private final Thread writeThread;
	private final BlockingQueue<Packet> writeQueue = new ArrayBlockingQueue<>(MAX_PACKET_QUEUE_SIZE, true);

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

	public void offer(Packet packet) {
		writeQueue.offer(packet);
	}

	public abstract void onPacketReceived(Packet packet);

	public abstract void onPacketSent(Packet packet);

	public class ReadRunnable implements Runnable {

		@Override
		@SneakyThrows
		@SuppressWarnings("rawtypes")
		public void run() {
//			System.out.println(PacketExchanger.this.getClass().getSimpleName() + ".ReadRunnable.run()");

			try {
				final var inputStream = new DataInputStream(socket.getInputStream());

				while (socket.isConnected()) {
//					System.out.println(PacketExchanger.this.getClass().getSimpleName() + ".readInt length");
					final var length = inputStream.readInt() - Integer.BYTES;
					if (length < 0 || length >= readBuffer.length) {
						throw new RuntimeException("invalid buffer length: " + length);
					}

//					System.out.println(PacketExchanger.this.getClass().getSimpleName() + ".readInt packetId");
					final var packetId = inputStream.readInt();

//					System.out.println(PacketExchanger.this.getClass().getSimpleName() + ".readInt readBuffer");
					inputStream.read(readBuffer, 0, length);

					final var identifier = writePacketRegistry.get(state, packetId);
					if (identifier == null) {
						throw new IllegalStateException(Remote.this.getClass().getSimpleName() + ": " + packetId + " is not registered in " + state + " remote registry");
					}

					final PacketSerializer deserializer = identifier.serializer();
					final var packet = deserializer.deserialize(new DataInputStream(new ByteArrayInputStream(readBuffer, 0, length)));

					onPacketReceived(packet);
				}
			} catch (Exception exception) {
				if (!(exception instanceof InterruptedException)) {
					throw exception;
				}

				System.out.println(socket);
				if (exception instanceof EOFException && !socket.isClosed()) {
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
//			System.out.println(PacketExchanger.this.getClass().getSimpleName() + ".WriteRunnable.run()");

			try {
				final var outputStream = new DataOutputStream(socket.getOutputStream());

				while (socket.isConnected()) {
					final var packet = writeQueue.poll(1, TimeUnit.HOURS);
					if (packet == null) {
						continue;
					}

					final var identifier = readPacketRegistry.get(state, packet.getClass());
					if (identifier == null) {
						throw new IllegalStateException(Remote.this.getClass().getSimpleName() + ": " + packet.getClass().getSimpleName() + " is not registered in " + state + " local registry");
					}

					final var writeStream = new ByteArrayOutputStream();

					final PacketSerializer serializer = identifier.serializer();
					serializer.serialize(packet, new DataOutputStream(writeStream));

					final var bytes = writeStream.toByteArray();
					outputStream.writeInt(bytes.length + Integer.BYTES);
					outputStream.writeInt(identifier.number());
					outputStream.write(bytes);
					
					onPacketSent(packet);
				}
			} catch (Exception exception) {
				if (!(exception instanceof InterruptedException)) {
					throw exception;
				}
			}
		}

	}

}
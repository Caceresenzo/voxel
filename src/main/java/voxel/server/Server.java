package voxel.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import lombok.Getter;

public class Server implements Runnable {

	private final @Getter String name;
	private final ServerSocket serverSocket;
	private final ThreadFactory threadFactory;
	private final Thread thread;

	private final List<RemoteClient> clients;

	public Server(String name, ServerSocket serverSocket, ThreadFactory threadFactory) {
		this.name = name;
		this.serverSocket = serverSocket;
		this.threadFactory = threadFactory;
		this.thread = threadFactory.newThread(this);
		this.clients = Collections.synchronizedList(new ArrayList<>());
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		thread.interrupt();
	}

	@Override
	public void run() {
		try {
			while (true) {
				final Socket clientSocket;
				try {
					clientSocket = serverSocket.accept();
				} catch (IOException exception) {
					exception.printStackTrace();
					continue;
				}

				final var client = new RemoteClient(this, clientSocket, threadFactory);
				clients.add(client);

				client.start();
			}
		} catch (Exception exception) {
			if (!(exception instanceof InterruptedException)) {
				throw exception;
			}
		}
	}

	public long getClientCount() {
		return clients.size();
	}

	public static Server create(String name, int port) throws IOException {
		final var serverSocket = new ServerSocket(port);

		return new Server(name, serverSocket, Thread.ofVirtual().factory());
	}

}
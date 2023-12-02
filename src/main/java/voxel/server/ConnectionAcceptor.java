package voxel.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;

public class ConnectionAcceptor implements Runnable {

	private final ServerSocket serverSocket;
	private final ThreadFactory threadFactory;
	private final Thread thread;
	private Server server;

	private final List<RemoteClient> clients;

	public ConnectionAcceptor(ServerSocket serverSocket, ThreadFactory threadFactory) {
		this.serverSocket = serverSocket;
		this.threadFactory = threadFactory;
		this.thread = threadFactory.newThread(this);
		this.clients = Collections.synchronizedList(new ArrayList<>());
	}

	public void start(Server server) {
		this.server = server;
		thread.start();
	}

	public void stop() {
		thread.interrupt();
		this.server = null;
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

				final var client = new RemoteClient(server, clientSocket, threadFactory);
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

	public List<RemoteClient> getClients() {
		return Collections.unmodifiableList(clients);
	}

	public static ConnectionAcceptor create(int port) throws IOException {
		final var serverSocket = new ServerSocket(port);

		return new ConnectionAcceptor(serverSocket, Thread.ofVirtual().factory());
	}

}
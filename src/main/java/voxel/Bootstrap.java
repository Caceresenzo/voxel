package voxel;

import java.net.BindException;
import java.time.Duration;
import java.util.UUID;

import voxel.client.Game;
import voxel.client.RemoteServer;
import voxel.networking.packet.ConnectionState;
import voxel.networking.packet.serverbound.handshake.HandshakePacket;
import voxel.server.ConnectionAcceptor;
import voxel.server.Server;

public class Bootstrap {

	public static void main(String[] args) throws Exception {
		Thread.sleep(Duration.ofSeconds(1));

		final var port = 1234;
		var name = "srv";

		try {
			final var acceptor = ConnectionAcceptor.create(port);
			final var server = new Server("Hello");

			server.start(acceptor);

			new Thread(() -> server.loop()).start();
			System.out.println("server started");
		} catch (BindException exception) {
			System.out.println("server already running, not starting another one: " + exception.getMessage());
			name = "pid-" + ProcessHandle.current().pid();
		}

		final var client = RemoteServer.connect("localhost", port);
		client.start();

		client.offer(new HandshakePacket(ConnectionState.LOGIN));
		client.login(UUID.randomUUID(), name);

		Game.run();
	}

}
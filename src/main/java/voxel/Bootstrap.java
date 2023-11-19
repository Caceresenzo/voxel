package voxel;

import java.net.BindException;
import java.time.Duration;
import java.util.UUID;

import voxel.client.Game;
import voxel.client.multiplayer.RemoteServer;
import voxel.common.packet.ConnectionState;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.server.Server;

public class Bootstrap {

	public static void main(String[] args) throws Exception {
		Thread.sleep(Duration.ofSeconds(1));
		
		final var port = 1234;
		var name = "srv";

		try {
			final var server = Server.create("Hello World", port);
			server.start();
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
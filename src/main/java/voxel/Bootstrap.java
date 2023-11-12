package voxel;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.UUID;

import voxel.common.packet.ConnectionState;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketRegistry;
import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.ClientBoundPacketHandler;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginPacket;
import voxel.common.packet.serverbound.other.PingPacket;
import voxel.common.packet.serverbound.status.StatusRequestPacket;
import voxel.server.Server;

public class Bootstrap {

	public static void main(String[] args) throws Exception {
		final var serverBoundPacketRegistry = new PacketRegistry();
		final var clientBoundPacketRegistry = new PacketRegistry();

		serverBoundPacketRegistry.register(ConnectionState.HANDSHAKE, HandshakePacket.IDENTIFIER);

		serverBoundPacketRegistry.register(ConnectionState.STATUS, StatusRequestPacket.IDENTIFIER);
		clientBoundPacketRegistry.register(ConnectionState.STATUS, StatusResponsePacket.IDENTIFIER);

		serverBoundPacketRegistry.register(ConnectionState.STATUS, PingPacket.IDENTIFIER);
		clientBoundPacketRegistry.register(ConnectionState.STATUS, PongPacket.IDENTIFIER);

		serverBoundPacketRegistry.register(ConnectionState.LOGIN, LoginPacket.IDENTIFIER);
		clientBoundPacketRegistry.register(ConnectionState.LOGIN, LoginSuccessPacket.IDENTIFIER);
		serverBoundPacketRegistry.register(ConnectionState.LOGIN, LoginAcknowledgedPacket.IDENTIFIER);

		final var port = 1234;

		final var server = Server.create(serverBoundPacketRegistry, "Hello World", port);
		server.start();

		final var client = RemoteServer.connect(clientBoundPacketRegistry, "localhost", port);
		client.start();

		client.offer(new HandshakePacket(ConnectionState.LOGIN));
		client.setState(ConnectionState.LOGIN);

		client.login(UUID.randomUUID(), "Enzo");

//		client.offer(new StatusRequestPacket());
//		client.offer(new PingPacket(System.currentTimeMillis()));

//		Thread.sleep(Duration.ofSeconds(1));
//		client.disconnect();

		Thread.sleep(Duration.ofDays(10));
	}

	public static class RemoteServer extends Remote implements ClientBoundPacketHandler<RemoteServer> {

		private UUID uuid;
		private String login;

		public RemoteServer(Socket socket, PacketRegistry packetRegistry) {
			super(socket, packetRegistry, Thread.ofPlatform().factory());
		}

		public void login(UUID uuid, String login) {
			this.uuid = uuid;
			this.login = login;

			offer(new LoginPacket(uuid, login));
		}

		public void setState(ConnectionState state) {
			this.state = state;
		}

		@Override
		public void onPacket(Packet<?> packet) {
			if (packet instanceof PongPacket packet_) {
				onPong(this, packet_);
			} else if (packet instanceof StatusResponsePacket packet_) {
				onStatusResponse(this, packet_);
			} else if (packet instanceof LoginSuccessPacket packet_) {
				onLoginSuccess(this, packet_);
			}
		}

		@Override
		public void onPong(RemoteServer client, PongPacket packet) {
			System.out.println("Bootstrap.SimpleClient.onPong()" + packet);
		}

		@Override
		public void onStatusResponse(RemoteServer client, StatusResponsePacket packet) {
			System.out.println("Bootstrap.SimpleClient.onStatusResponse()" + packet);
		}

		@Override
		public void onLoginSuccess(RemoteServer client, LoginSuccessPacket packet) {
			System.out.println("Bootstrap.SimpleClient.onLoginSuccess()" + packet);

			client.offer(new LoginAcknowledgedPacket());
		}

		public static RemoteServer connect(PacketRegistry packetRegistry, String host, int port) throws UnknownHostException, IOException {
			final var socket = new Socket(host, port);

			return new RemoteServer(socket, packetRegistry);
		}

	}

}
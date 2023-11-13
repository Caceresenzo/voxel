package voxel;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.UUID;

import voxel.common.packet.ConnectionState;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketRegistries;
import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.ClientBoundPacketHandler;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginStartPacket;
import voxel.server.Server;

public class Bootstrap {

	public static void main(String[] args) throws Exception {
		final var port = 1234;

		final var server = Server.create("Hello World", port);
		server.start();

		final var client = RemoteServer.connect("localhost", port);
		client.start();

		client.offer(new HandshakePacket(ConnectionState.LOGIN));
		client.login(UUID.randomUUID(), "Enzo");

//		client.offer(new HandshakePacket(ConnectionState.STATUS));
//		client.offer(new StatusRequestPacket());
//		client.offer(new PingPacket(System.currentTimeMillis()));

//		Thread.sleep(Duration.ofSeconds(1));
//		client.disconnect();

		Thread.sleep(Duration.ofDays(10));
	}

	public static class RemoteServer extends Remote implements ClientBoundPacketHandler<RemoteServer> {

		private UUID uuid;
		private String login;

		public RemoteServer(Socket socket) {
			super(
				socket,
				PacketRegistries.SERVER_BOUND,
				PacketRegistries.CLIENT_BOUND,
				Thread.ofPlatform().factory()
			);
		}

		public void login(UUID uuid, String login) {
			this.uuid = uuid;
			this.login = login;

			offer(new LoginStartPacket(uuid, login));
		}

		public void setState(ConnectionState state) {
			this.state = state;
		}

		@Override
		public void onPacketReceived(Packet packet) {
			ClientBoundPacketHandler.dispatch(this, this, packet);
		}

		@Override
		public void onPacketSent(Packet packet) {
			if (packet instanceof HandshakePacket packet_) {
				state = packet_.nextState();
			} else if (packet instanceof LoginAcknowledgedPacket) {
				state = ConnectionState.PLAY;
			}
		}

		@Override
		public void onPong(RemoteServer remote, PongPacket packet) {
			System.out.println("Bootstrap.SimpleClient.onPong()" + packet);
		}

		@Override
		public void onStatusResponse(RemoteServer remote, StatusResponsePacket packet) {
			System.out.println("Bootstrap.SimpleClient.onStatusResponse()" + packet);
		}

		@Override
		public void onLoginSuccess(RemoteServer remote, LoginSuccessPacket packet) {
			System.out.println("Bootstrap.SimpleClient.onLoginSuccess()" + packet);

			remote.offer(new LoginAcknowledgedPacket());
		}

		public static RemoteServer connect(String host, int port) throws UnknownHostException, IOException {
			final var socket = new Socket(host, port);

			return new RemoteServer(socket);
		}

	}

}
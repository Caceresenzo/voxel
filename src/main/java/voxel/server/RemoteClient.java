package voxel.server;

import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

import voxel.common.packet.ConnectionState;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketRegistries;
import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;
import voxel.common.packet.serverbound.ServerBoundPacketHandler;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginStartPacket;
import voxel.common.packet.serverbound.other.PingPacket;
import voxel.common.packet.serverbound.status.StatusRequestPacket;

public class RemoteClient extends Remote implements ServerBoundPacketHandler<RemoteClient> {

	private final Server server;
	private UUID uuid;
	private String login;

	public RemoteClient(Server server, Socket socket, ThreadFactory threadFactory) {
		super(
			socket,
			PacketRegistries.CLIENT_BOUND,
			PacketRegistries.SERVER_BOUND,
			threadFactory
		);

		this.server = server;
	}

	@Override
	public void onPacketReceived(Packet packet) {
		ServerBoundPacketHandler.dispatch(this, this, packet);
	}
	
	@Override
	public void onPacketSent(Packet packet) {
	}

	@Override
	public void onHandshake(RemoteClient remote, HandshakePacket packet) {
		remote.state = packet.nextState();
		System.out.println("set state " + remote.state);
	}

	@Override
	public void onPing(RemoteClient remote, PingPacket packet) {
		remote.offer(new PongPacket(packet.payload()));
	}

	@Override
	public void onStatusRequest(RemoteClient remote, StatusRequestPacket packet) {
		remote.offer(new StatusResponsePacket(
			server.getName(),
			server.getClientCount()
		));
	}

	@Override
	public void onLogin(RemoteClient remote, LoginStartPacket packet) {
		uuid = packet.uuid();
		login = packet.login();

		remote.offer(new LoginSuccessPacket(uuid, login));
	}

	@Override
	public void onLoginAcknowledged(RemoteClient remote, LoginAcknowledgedPacket packet) {
		remote.state = ConnectionState.PLAY;
		System.out.println("onLoginAcknowledged");
	}

}
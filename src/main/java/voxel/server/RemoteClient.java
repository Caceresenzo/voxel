package voxel.server;

import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

import voxel.common.packet.Packet;
import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;
import voxel.common.packet.serverbound.ServerBoundPacketHandler;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginPacket;
import voxel.common.packet.serverbound.other.PingPacket;
import voxel.common.packet.serverbound.status.StatusRequestPacket;

public class RemoteClient extends Remote implements ServerBoundPacketHandler<RemoteClient> {

	private final Server server;
	private UUID uuid;
	private String login;

	public RemoteClient(Server server, Socket socket, ThreadFactory threadFactory) {
		super(socket, server.getPacketRegistry(), threadFactory);

		this.server = server;
	}

	@Override
	public void onPacket(Packet<?> packet) {
		if (packet instanceof HandshakePacket packet_) {
			onHandshake(this, packet_);
		} else if (packet instanceof PingPacket packet_) {
			onPing(this, packet_);
		} else if (packet instanceof StatusRequestPacket packet_) {
			onStatusRequest(this, packet_);
		} else if (packet instanceof LoginPacket packet_) {
			onLogin(this, packet_);
		} else if (packet instanceof LoginAcknowledgedPacket packet_) {
			onLoginAcknowledged(this, packet_);
		}
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
	public void onLogin(RemoteClient remote, LoginPacket packet) {
		uuid = packet.uuid();
		login = packet.login();
		
		remote.offer(new LoginSuccessPacket(uuid, login));
	}

	@Override
	public void onLoginAcknowledged(RemoteClient remote, LoginAcknowledgedPacket packet) {
		System.out.println("onLoginAcknowledged");
	}

}
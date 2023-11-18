package voxel.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

import org.joml.Vector3f;

import lombok.Getter;
import voxel.common.packet.ConnectionState;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketRegistries;
import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.common.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;
import voxel.common.packet.serverbound.ServerBoundPacketHandler;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginStartPacket;
import voxel.common.packet.serverbound.other.PingPacket;
import voxel.common.packet.serverbound.play.SetPlayerPositionAndRotationPacket;
import voxel.common.packet.serverbound.status.StatusRequestPacket;

public class RemoteClient extends Remote implements ServerBoundPacketHandler<RemoteClient> {

	private final Server server;
	private @Getter UUID uuid;
	private String login;
	private int latency;
	private Vector3f position = new Vector3f();
	private float yaw;
	private float pitch;

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

		final var mask = (byte) (PlayerInfoUpdatePacket.Action.ADD_PLAYER.bit() | PlayerInfoUpdatePacket.Action.UPDATE_LATENCY.bit());

		final var selfPacket = new PlayerInfoUpdatePacket(
			mask,
			Collections.singletonList(new PlayerInfoUpdatePacket.PlayerData(uuid, login, latency))
		);

		final var others = server.getAuthenticatedClients();
		final var players = new ArrayList<PlayerInfoUpdatePacket.PlayerData>(others.size());

		for (final var other : others) {
			if (other == remote) {
				continue;
			}

			other.offer(selfPacket);
			players.add(new PlayerInfoUpdatePacket.PlayerData(
				other.uuid,
				other.login,
				other.latency
			));
		}

		if (!players.isEmpty()) {
			remote.offer(new PlayerInfoUpdatePacket(mask, players));
		}
	}

	@Override
	public void onSetPlayerPositionAndRotation(RemoteClient remote, SetPlayerPositionAndRotationPacket packet) {
		position.x = packet.x();
		position.y = packet.y();
		position.z = packet.z();
		yaw = packet.yaw();
		pitch = packet.pitch();

		final var updatePacket = new UpdateEntityPositionAndRotationPacket(
			uuid,
			position.x, position.y, position.z,
			yaw, pitch
		);

		final var others = server.getAuthenticatedClients();
		for (final var other : others) {
			if (other == remote) {
				continue;
			}

			other.offer(updatePacket);
		}
	}

	public boolean isAuthenticated() {
		return uuid != null && login != null;
	}

}
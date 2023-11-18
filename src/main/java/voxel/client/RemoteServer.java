package voxel.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import voxel.client.game.Game;
import voxel.client.game.PlayingGameState;
import voxel.client.player.RemotePlayer;
import voxel.common.packet.ConnectionState;
import voxel.common.packet.Packet;
import voxel.common.packet.PacketRegistries;
import voxel.common.packet.Remote;
import voxel.common.packet.clientbound.ClientBoundPacketHandler;
import voxel.common.packet.clientbound.login.LoginSuccessPacket;
import voxel.common.packet.clientbound.other.PongPacket;
import voxel.common.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.common.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.common.packet.clientbound.status.StatusResponsePacket;
import voxel.common.packet.serverbound.handshake.HandshakePacket;
import voxel.common.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.common.packet.serverbound.login.LoginStartPacket;

public class RemoteServer extends Remote implements ClientBoundPacketHandler<RemoteServer> {

	private UUID uuid;
	private String login;
	private @Getter List<RemotePlayer> otherPlayers = new ArrayList<>();

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
		System.out.println("onPong " + packet);
	}

	@Override
	public void onStatusResponse(RemoteServer remote, StatusResponsePacket packet) {
		System.out.println("onStatusResponse " + packet);
	}

	@Override
	public void onLoginSuccess(RemoteServer remote, LoginSuccessPacket packet) {
		remote.offer(new LoginAcknowledgedPacket());

		System.out.println("logged as " + login + " (" + uuid + ")");

		Game.setState(new PlayingGameState(uuid, login, this));
	}

	@Override
	public void onPlayerInfoUpdate(RemoteServer remote, PlayerInfoUpdatePacket packet) {
		final var actionMask = packet.actionMask();

		for (final var playerData : packet.players()) {
			var player = getPlayer(playerData.uuid());

			if (PlayerInfoUpdatePacket.Action.ADD_PLAYER.test(actionMask) && player == null) {
				player = new RemotePlayer(playerData.uuid(), playerData.login());
				otherPlayers.add(player);
				System.out.println("player " + player.getLogin() + " joined (" + player.getUUID() + ")");
			}

			if (player == null) {
				continue;
			}

			if (PlayerInfoUpdatePacket.Action.UPDATE_LATENCY.test(actionMask)) {
				// TODO
			}
		}
	}

	@Override
	public void onUpdateEntityPositionAndRotation(RemoteServer remote, UpdateEntityPositionAndRotationPacket packet) {
		final var entity = getPlayer(packet.playerId());

		entity.move(packet.x(), packet.y(), packet.z(), packet.yaw(), packet.pitch());
	}

	public RemotePlayer getPlayer(UUID uuid) {
		for (final var player : otherPlayers) {
			if (player.getUUID().equals(uuid)) {
				return player;
			}
		}

		return null;
	}

	public static RemoteServer connect(String host, int port) throws UnknownHostException, IOException {
		final var socket = new Socket(host, port);

		return new RemoteServer(socket);
	}

}
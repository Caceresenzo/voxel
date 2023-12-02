package voxel.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadFactory;

import voxel.networking.Remote;
import voxel.networking.packet.ConnectionState;
import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketRegistries;
import voxel.networking.packet.clientbound.login.LoginSuccessPacket;
import voxel.networking.packet.clientbound.other.PongPacket;
import voxel.networking.packet.clientbound.play.ChunkDataPacket;
import voxel.networking.packet.clientbound.play.LoginPacket;
import voxel.networking.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.networking.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.networking.packet.clientbound.status.StatusResponsePacket;
import voxel.networking.packet.serverbound.ServerBoundPacketHandler;
import voxel.networking.packet.serverbound.handshake.HandshakePacket;
import voxel.networking.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.networking.packet.serverbound.login.LoginStartPacket;
import voxel.networking.packet.serverbound.other.PingPacket;
import voxel.networking.packet.serverbound.play.SetPlayerPositionAndRotationPacket;
import voxel.networking.packet.serverbound.status.StatusRequestPacket;
import voxel.server.chunk.Chunk;
import voxel.server.player.Player;

public class RemoteClient extends Remote implements ServerBoundPacketHandler<RemoteClient> {

	private final Server server;
	private Player player;
	private int latency;

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
			server.getPlayerCount()
		));
	}

	@Override
	public void onLogin(RemoteClient remote, LoginStartPacket packet) {
		this.player = new Player(this, packet.uuid(), packet.login());
		this.server.getPlayers().add(player);

		remote.offer(new LoginSuccessPacket(player.getUuid(), player.getLogin()));
	}

	@Override
	public void onLoginAcknowledged(RemoteClient remote, LoginAcknowledgedPacket packet) {
		remote.state = ConnectionState.PLAY;

		remote.offer(new LoginPacket(server.getWorld().getName()));

		final var mask = (byte) (PlayerInfoUpdatePacket.Action.ADD_PLAYER.bit() | PlayerInfoUpdatePacket.Action.UPDATE_LATENCY.bit());

		final var selfPacket = new PlayerInfoUpdatePacket(
			mask,
			Collections.singletonList(new PlayerInfoUpdatePacket.PlayerData(player.getUuid(), player.getLogin(), latency))
		);

		final var others = server.getPlayers();
		final var players = new ArrayList<PlayerInfoUpdatePacket.PlayerData>(others.size());

		for (final var other : others) {
			if (other == player) {
				continue;
			}

			other.getClient().offer(selfPacket);
			players.add(new PlayerInfoUpdatePacket.PlayerData(
				other.getUuid(),
				other.getLogin(),
				other.getClient().latency
			));
		}

		if (!players.isEmpty()) {
			remote.offer(new PlayerInfoUpdatePacket(mask, players));
		}

		for (final var chunk : server.getWorld().getChunkManager().getAll()) {
			final var voxelIds = new byte[Chunk.VOLUME];

			for (var y = 0; y < Chunk.DEPTH; y++) {
				for (var z = 0; z < Chunk.HEIGHT; z++) {
					for (var x = 0; x < Chunk.WIDTH; x++) {
						final var index = Chunk.index(x, y, z);
						voxelIds[index] = chunk.getVoxel(x, y, z);
					}
				}
			}

			remote.offer(new ChunkDataPacket(chunk.getPosition(), voxelIds));
		}
	}

	@Override
	public void onSetPlayerPositionAndRotation(RemoteClient remote, SetPlayerPositionAndRotationPacket packet) {
		player.updateLocation(packet.x(), packet.y(), packet.z(), packet.yaw(), packet.pitch());

		final var updatePacket = new UpdateEntityPositionAndRotationPacket(
			player.getUuid(),
			player.getPosition().x(),
			player.getPosition().y(),
			player.getPosition().z(),
			player.getYaw(),
			player.getPitch()
		);

		final var others = server.getPlayers();
		for (final var other : others) {
			if (other == player) {
				continue;
			}

			other.getClient().offer(updatePacket);
		}
	}

}
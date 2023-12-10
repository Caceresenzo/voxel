package voxel.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

import org.joml.Vector3i;

import lombok.Getter;
import voxel.client.player.LocalPlayer;
import voxel.client.player.RemotePlayer;
import voxel.client.state.PlayingGameState;
import voxel.networking.Remote;
import voxel.networking.packet.ConnectionState;
import voxel.networking.packet.Packet;
import voxel.networking.packet.PacketRegistries;
import voxel.networking.packet.clientbound.ClientBoundPacketHandler;
import voxel.networking.packet.clientbound.login.LoginSuccessPacket;
import voxel.networking.packet.clientbound.other.PongPacket;
import voxel.networking.packet.clientbound.play.BlockUpdatePacket;
import voxel.networking.packet.clientbound.play.ChunkDataPacket;
import voxel.networking.packet.clientbound.play.LoginPacket;
import voxel.networking.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.networking.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.networking.packet.clientbound.status.StatusResponsePacket;
import voxel.networking.packet.serverbound.handshake.HandshakePacket;
import voxel.networking.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.networking.packet.serverbound.login.LoginStartPacket;
import voxel.shared.chunk.ChunkKey;
import voxel.util.DoubleBufferedBlockingQueue;

public class RemoteServer extends Remote implements ClientBoundPacketHandler<RemoteServer> {

	private LocalPlayer player;
	private PlayingGameState gameState;
	private final @Getter List<RemotePlayer> otherPlayers = new ArrayList<>();
	private final DoubleBufferedBlockingQueue<Packet> readQueue = new DoubleBufferedBlockingQueue<>(() -> new ArrayBlockingQueue<>(MAX_PACKET_QUEUE_SIZE));

	public RemoteServer(Socket socket) {
		super(
			socket,
			PacketRegistries.SERVER_BOUND,
			PacketRegistries.CLIENT_BOUND,
			Thread.ofPlatform().factory()
		);
	}

	public void login(UUID uuid, String login) {
		this.player = new LocalPlayer(uuid, login);

		offer(new LoginStartPacket(uuid, login));
	}

	public void setState(ConnectionState state) {
		this.state = state;
	}

	@Override
	public void onPacketReceived(Packet packet) {
		if (ConnectionState.PLAY.equals(state)) {
			readQueue.add(packet);
		} else {
			dispatch(packet);
		}
	}

	@Override
	public void onPacketSent(Packet packet) {
		if (packet instanceof HandshakePacket packet_) {
			state = packet_.nextState();
		} else if (packet instanceof LoginAcknowledgedPacket) {
			state = ConnectionState.PLAY;
		}
	}

	public void processPackets() {
		final var queue = readQueue.swap();

		if (queue.isEmpty()) {
			return;
		}

		for (final var packet : queue) {
			dispatch(packet);
		}

		queue.clear();
	}

	private void dispatch(Packet packet) {
		ClientBoundPacketHandler.dispatch(this, this, packet);
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

		this.gameState = new PlayingGameState(player, this);
		Game.setState(this.gameState);
	}

	@Override
	public void onLogin(RemoteServer remote, LoginPacket packet) {
		gameState.awaitInitialize();

		final var world = gameState.setWorld(packet.dimensionName());
		player.setWorld(world);
	}

	@Override
	public void onBlockUpdate(RemoteServer remote, BlockUpdatePacket packet) {
		final var blockPosition = new Vector3i(packet.x(), packet.y(), packet.z());

		final var chunk = player.getWorld().getChunkAt(blockPosition);
		if (chunk == null) {
			return;
		}

		final var localPosition = VoxelHandler.worldToLocal(blockPosition, chunk.getPosition());

		chunk.setVoxel(localPosition.x, localPosition.y, localPosition.z, packet.id());
		gameState.getVoxelHandler().rebuildAdjacentChunks(chunk, localPosition);
	}

	@Override
	public void onChunkData(RemoteServer remote, ChunkDataPacket packet) {
		final var key = new ChunkKey(packet.x(), packet.y(), packet.z());
		final var chunk = player.getWorld().getChunkManager().load(key);
		chunk.setVoxels(packet.voxelIds());
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
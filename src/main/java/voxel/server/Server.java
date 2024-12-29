package voxel.server;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;
import lombok.SneakyThrows;
import voxel.networking.Remote;
import voxel.networking.packet.ConnectionState;
import voxel.networking.packet.Packet;
import voxel.networking.packet.clientbound.login.LoginSuccessPacket;
import voxel.networking.packet.clientbound.other.PongPacket;
import voxel.networking.packet.clientbound.play.BlockUpdatePacket;
import voxel.networking.packet.clientbound.play.ChunkDataPacket;
import voxel.networking.packet.clientbound.play.GameEventPacket;
import voxel.networking.packet.clientbound.play.LoginPacket;
import voxel.networking.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.networking.packet.clientbound.play.SetCenterChunkPacket;
import voxel.networking.packet.clientbound.play.SynchronizePlayerPositionPacket;
import voxel.networking.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.networking.packet.clientbound.status.StatusResponsePacket;
import voxel.networking.packet.serverbound.ServerBoundPacketHandler;
import voxel.networking.packet.serverbound.handshake.HandshakePacket;
import voxel.networking.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.networking.packet.serverbound.login.LoginStartPacket;
import voxel.networking.packet.serverbound.other.ConfirmTeleportationPacket;
import voxel.networking.packet.serverbound.other.PingPacket;
import voxel.networking.packet.serverbound.play.PlayerActionPacket;
import voxel.networking.packet.serverbound.play.SetPlayerPositionAndRotationPacket;
import voxel.networking.packet.serverbound.play.UseItemOnPacket;
import voxel.networking.packet.serverbound.status.StatusRequestPacket;
import voxel.server.chunk.generator.SimplexNoiseChunkGenerator;
import voxel.server.player.Player;
import voxel.server.world.World;
import voxel.server.world.WorldCreator;
import voxel.shared.chunk.Chunk;
import voxel.shared.chunk.ChunkPosition;
import voxel.util.DoubleBufferedBlockingQueue;

public class Server implements ServerBoundPacketHandler<RemoteClient> {

	@Getter
	private final String name;

	@Getter
	private final List<Player> players = Collections.synchronizedList(new ArrayList<>());

	@Getter
	private final World world = new World(new WorldCreator("overworld", new SimplexNoiseChunkGenerator()));

	@Getter
	private final DoubleBufferedBlockingQueue<Pair<RemoteClient, Packet>> readPacketQueue = new DoubleBufferedBlockingQueue<>(() -> new ArrayBlockingQueue<>(Remote.MAX_PACKET_QUEUE_SIZE * 8));

	private ConnectionAcceptor networkServer;
	private boolean running;

	private ConcurrentMap<UUID, PendingTeleport> pendingTeleports = new ConcurrentHashMap<>();

	public Server(String name) {
		this.name = name;
	}

	public void start(ConnectionAcceptor networkServer) {
		world.loadSpawnChunks(10, 3);

		this.networkServer = networkServer;
		this.networkServer.start(this);

		this.running = true;
	}

	@SneakyThrows
	public void loop() {
		while (running) {
			processPackets();

			Thread.sleep(Duration.ofMillis(50));
		}
	}

	public void stop() {
		this.networkServer.stop();
		this.networkServer = null;

		this.running = false;
	}

	public void onPacketReceived(RemoteClient remote, Packet packet) {
		if (ConnectionState.PLAY.equals(remote.getConnectionState())) {
			readPacketQueue.add(Pair.of(remote, packet));
		} else {
			dispatch(remote, packet);
		}
	}

	public void processPackets() {
		final var queue = readPacketQueue.swap();

		if (queue.isEmpty()) {
			return;
		}

		for (final var pair : queue) {
			dispatch(pair.getLeft(), pair.getRight());
		}

		queue.clear();
	}

	private void dispatch(RemoteClient remote, Packet packet) {
		ServerBoundPacketHandler.dispatch(this, remote, packet);
	}

	@Override
	public void onHandshake(RemoteClient remote, HandshakePacket packet) {
		remote.setConnectionState(packet.nextState());
	}

	@Override
	public void onPing(RemoteClient remote, PingPacket packet) {
		remote.offer(new PongPacket(packet.payload()));
	}

	@Override
	public void onStatusRequest(RemoteClient remote, StatusRequestPacket packet) {
		remote.offer(new StatusResponsePacket(
			name,
			players.size()
		));
	}

	@Override
	public void onLogin(RemoteClient remote, LoginStartPacket packet) {
		final var player = new Player(remote, packet.uuid(), packet.login());

		remote.setPlayer(player);
		players.add(player);

		remote.offer(new LoginSuccessPacket(player.getUuid(), player.getLogin()));
	}

	@Override
	public void onLoginAcknowledged(RemoteClient remote, LoginAcknowledgedPacket packet) {
		remote.setConnectionState(ConnectionState.PLAY);

		remote.offer(new LoginPacket(world.getName()));

		synchronizePosition(remote, () -> {
			final var mask = (byte) (PlayerInfoUpdatePacket.Action.ADD_PLAYER.bit() | PlayerInfoUpdatePacket.Action.UPDATE_LATENCY.bit());

			final var selfPlayer = remote.getPlayer();
			final var selfPacket = new PlayerInfoUpdatePacket(
				mask,
				Collections.singletonList(new PlayerInfoUpdatePacket.PlayerData(selfPlayer.getUuid(), selfPlayer.getLogin(), remote.getLatency()))
			);

			final var playerDatas = new ArrayList<PlayerInfoUpdatePacket.PlayerData>(players.size());

			for (final var other : players) {
				if (other == selfPlayer) {
					continue;
				}

				other.getClient().offer(selfPacket);
				playerDatas.add(new PlayerInfoUpdatePacket.PlayerData(
					other.getUuid(),
					other.getLogin(),
					other.getClient().getLatency()
				));
			}

			if (!playerDatas.isEmpty()) {
				remote.offer(new PlayerInfoUpdatePacket(mask, playerDatas));
			}

			remote.offer(GameEventPacket.startWaitingForLevelChunks());
			remote.offer(new SetCenterChunkPacket(ChunkPosition.zero()));

			for (final var chunk : world.getChunkManager().getNear(selfPlayer.getCenterChunkPosition(), 2)) {
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
		});
	}

	// TODO Unsafe as removing and re-adding if necessary could leave a gap
	@Override
	public void onConfirmTeleportation(RemoteClient remote, ConfirmTeleportationPacket packet) {
		final var uuid = remote.getPlayer().getUuid();

		final var teleport = pendingTeleports.remove(uuid);
		if (teleport == null) {
			return;
		}

		final var receivedId = packet.teleportId();
		if (teleport.id() != receivedId) {
			pendingTeleports.put(uuid, teleport);
		} else {
			teleport.runnable().run();
		}
	}

	@Override
	public void onPlayerAction(RemoteClient remote, PlayerActionPacket packet) {
		final var chunkPosition = packet.blockPosition().toChunkPosition();
		final var chunk = world.getChunk(chunkPosition);

		if (chunk == null) {
			return;
		}

		final var localPosition = packet.blockPosition().toLocalPosition(chunkPosition);

		switch (packet.status()) {
			case STARTED_DIGGING: {
				final var air = (byte) 0;
				chunk.setVoxel(localPosition, air);

				final var updatePacket = new BlockUpdatePacket(packet.blockPosition(), air);
				broadcast(updatePacket);
				break;
			}
		}
	}

	@Override
	public void onSetPlayerPositionAndRotation(RemoteClient remote, SetPlayerPositionAndRotationPacket packet) {
		final var player = remote.getPlayer();
		if (hasPendingTeleport(player.getUuid())) {
			return;
		}

		final var chunkPositionChanged = player.updateLocation(packet.position(), packet.yaw(), packet.pitch());

		final var updatePacket = new UpdateEntityPositionAndRotationPacket(
			player.getUuid(),
			player.getPosition(),
			player.getYaw(),
			player.getPitch()
		);

		for (final var other : players) {
			if (other == player) {
				continue;
			}

			other.getClient().offer(updatePacket);
		}

		if (chunkPositionChanged) {
			remote.offer(new SetCenterChunkPacket(player.getCenterChunkPosition()));
		}
	}

	@Override
	public void onUseItemOn(RemoteClient remote, UseItemOnPacket packet) {
		final var blockPosition = packet.blockPosition().add(packet.face());

		final var chunkPosition = blockPosition.toChunkPosition();
		final var chunk = world.getChunk(chunkPosition);

		if (chunk == null) {
			return;
		}

		final var localPosition = blockPosition.toLocalPosition(chunkPosition);

		final var grass = (byte) 1;
		chunk.setVoxel(localPosition, grass);

		final var updatePacket = new BlockUpdatePacket(blockPosition, grass);
		broadcast(updatePacket);
	}

	public void broadcast(BlockUpdatePacket updatePacket) {
		for (final var player : players) {
			player.getClient().offer(updatePacket);
		}
	}

	private boolean synchronizePosition(RemoteClient remote, Runnable runnable) {
		final var id = (int) System.currentTimeMillis();
		final var pendingTeleport = new PendingTeleport(id, runnable);

		final var player = remote.getPlayer();
		this.pendingTeleports.put(player.getUuid(), pendingTeleport);

		return remote.offer(new SynchronizePlayerPositionPacket(
			player.getPosition(),
			player.getYaw(),
			player.getPitch(),
			id
		));
	}

	private boolean hasPendingTeleport(UUID playerUuid) {
		return pendingTeleports.containsKey(playerUuid);
	}

}
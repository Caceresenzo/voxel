package voxel.server;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

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
import voxel.networking.packet.clientbound.play.LoginPacket;
import voxel.networking.packet.clientbound.play.PlayerInfoUpdatePacket;
import voxel.networking.packet.clientbound.play.UpdateEntityPositionAndRotationPacket;
import voxel.networking.packet.clientbound.status.StatusResponsePacket;
import voxel.networking.packet.serverbound.ServerBoundPacketHandler;
import voxel.networking.packet.serverbound.handshake.HandshakePacket;
import voxel.networking.packet.serverbound.login.LoginAcknowledgedPacket;
import voxel.networking.packet.serverbound.login.LoginStartPacket;
import voxel.networking.packet.serverbound.other.PingPacket;
import voxel.networking.packet.serverbound.play.PlayerActionPacket;
import voxel.networking.packet.serverbound.play.SetPlayerPositionAndRotationPacket;
import voxel.networking.packet.serverbound.play.UseItemOnPacket;
import voxel.networking.packet.serverbound.status.StatusRequestPacket;
import voxel.server.chunk.ServerChunk;
import voxel.server.chunk.generator.SimplexNoiseChunkGenerator;
import voxel.server.player.Player;
import voxel.server.world.World;
import voxel.server.world.WorldCreator;
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

	public Server(String name) {
		this.name = name;
	}

	public void start(ConnectionAcceptor networkServer) {
		world.loadSpawnChunks(3);

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
		if (ConnectionState.PLAY.equals(remote.getState())) {
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
		remote.setState(packet.nextState());
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
		remote.setState(ConnectionState.PLAY);

		remote.offer(new LoginPacket(world.getName()));

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

		for (final var chunk : world.getChunkManager().getAll()) {
			final var voxelIds = new byte[ServerChunk.VOLUME];

			for (var y = 0; y < ServerChunk.DEPTH; y++) {
				for (var z = 0; z < ServerChunk.HEIGHT; z++) {
					for (var x = 0; x < ServerChunk.WIDTH; x++) {
						final var index = ServerChunk.index(x, y, z);
						voxelIds[index] = chunk.getVoxel(x, y, z);
					}
				}
			}

			remote.offer(new ChunkDataPacket(chunk.getPosition(), voxelIds));
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

		player.updateLocation(packet.position(), packet.yaw(), packet.pitch());

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

}
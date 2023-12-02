package voxel.server;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.SneakyThrows;
import voxel.server.chunk.generator.SimplexNoiseChunkGenerator;
import voxel.server.player.Player;
import voxel.server.world.World;
import voxel.server.world.WorldCreator;

public class Server {

	@Getter
	private final String name;

	@Getter
	private final List<Player> players = Collections.synchronizedList(new ArrayList<>());

	@Getter
	private final World world = new World(new WorldCreator("overworld", new SimplexNoiseChunkGenerator()));

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
			Thread.sleep(Duration.ofMillis(50));
		}
	}

	public void stop() {
		this.networkServer.stop();
		this.networkServer = null;

		this.running = false;
	}

	public int getPlayerCount() {
		return players.size();
	}

}
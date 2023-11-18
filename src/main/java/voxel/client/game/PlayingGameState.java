package voxel.client.game;

import java.util.UUID;

import lombok.SneakyThrows;
import voxel.client.RemoteServer;
import voxel.client.VoxelHandler;
import voxel.client.World;
import voxel.client.graphics.mesh.chunk.ChunkShaderProgram;
import voxel.client.graphics.mesh.marker.Marker;
import voxel.client.graphics.mesh.marker.MarkerMesh;
import voxel.client.graphics.mesh.marker.MarkerShaderProgram;
import voxel.client.graphics.opengl.texture.Atlas;
import voxel.client.graphics.opengl.texture.ImageData;
import voxel.client.graphics.opengl.texture.Texture;
import voxel.client.graphics.opengl.util.OpenGL;
import voxel.client.player.LocalPlayer;
import voxel.common.packet.serverbound.play.SetPlayerPositionAndRotationPacket;

public class PlayingGameState implements GameState {

	private LocalPlayer player;
	private RemoteServer server;
	private ChunkShaderProgram chunkShaderProgram;
	private World world;
	private Texture frameTexture;
	private Texture texture;
	private Atlas atlas;
	private VoxelHandler voxelHandler;

	private MarkerShaderProgram markerShaderProgram;
	private MarkerMesh markerMesh;
	private Marker marker;

	public PlayingGameState(UUID uuid, String login, RemoteServer server) {
		this.player = new LocalPlayer(uuid, login);
		this.server = server;
	}

	@Override
	@SneakyThrows
	public void initialize() {
		chunkShaderProgram = ChunkShaderProgram.create();

		chunkShaderProgram.use();
		chunkShaderProgram.atlas.load(0);

		world = new World(chunkShaderProgram);

		player.update();

		chunkShaderProgram.use();
		chunkShaderProgram.projection.load(player.getProjection());

		frameTexture = Texture.create(
			ImageData.load(getClass().getResourceAsStream("/textures/frame.png"), false)
		);

		texture = Texture.create(
			ImageData.load(getClass().getResourceAsStream("/textures/arrow.png"), false)
		);

		atlas = Atlas.create(
			ImageData.load(getClass().getResourceAsStream("/textures/atlas.png"), true),
			8
		);

		voxelHandler = new VoxelHandler(player, world);

		markerShaderProgram = MarkerShaderProgram.create();

		markerShaderProgram.use();
		markerShaderProgram.projection.load(player.getProjection());

		markerMesh = new MarkerMesh(markerShaderProgram);
		marker = new Marker(frameTexture, markerMesh);
	}

	@Override
	public void update() {
		if (player.handleMouvement()) {
			final var position = player.getPosition();

			server.offer(new SetPlayerPositionAndRotationPacket(
				position.x,
				position.y,
				position.z,
				player.getYaw(),
				player.getPitch()
			));
		}

		player.update();
		voxelHandler.update();
	}

	@Override
	public void render() {
		atlas.activate(0);
		chunkShaderProgram.use();
		chunkShaderProgram.view.load(player.getView());
		world.render(player);

		marker.render(player, voxelHandler);

		texture.activate(0);
		for (final var otherPlayer : server.getOtherPlayers()) {
			otherPlayer.render(markerShaderProgram, player);
		}

		OpenGL.checkErrors();
	}

	@Override
	public void cleanup() {
	}

}
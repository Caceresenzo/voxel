package voxel.client.state;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import java.util.UUID;

import lombok.SneakyThrows;
import opengl.OpenGL;
import opengl.texture.Atlas;
import opengl.texture.ImageData;
import opengl.texture.Texture;
import voxel.client.Game;
import voxel.client.level.VoxelHandler;
import voxel.client.level.World;
import voxel.client.level.chunk.ChunkShaderProgram;
import voxel.client.marker.Marker;
import voxel.client.marker.MarkerMesh;
import voxel.client.marker.MarkerShaderProgram;
import voxel.client.multiplayer.RemoteServer;
import voxel.client.player.LocalPlayer;
import voxel.client.skybox.SkyBox;
import voxel.client.skybox.SkyBoxMesh;
import voxel.client.skybox.SkyBoxShaderProgram;
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

	private SkyBoxShaderProgram skyBoxShaderProgram;
	private SkyBoxMesh skyBoxMesh;
	private SkyBox skyBox;

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

		skyBoxShaderProgram = SkyBoxShaderProgram.create();
		skyBoxShaderProgram.use();
		skyBoxShaderProgram.projection.load(player.getProjection());
		
		skyBoxMesh = new SkyBoxMesh(skyBoxShaderProgram);
		skyBox = new SkyBox(skyBoxMesh);

		glfwSetMouseButtonCallback(Game.window, (window, button, action, mods) -> {
			if (action == GLFW_PRESS) {
				if (button == GLFW_MOUSE_BUTTON_LEFT) {
					voxelHandler.destroy();
				}

				if (button == GLFW_MOUSE_BUTTON_RIGHT) {
					voxelHandler.place();
				}
			}
		});
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
		skyBox.render(player, System.currentTimeMillis());

		texture.activate(0);
		for (final var otherPlayer : server.getOtherPlayers()) {
			otherPlayer.render(markerShaderProgram, player);
		}

		OpenGL.checkErrors();
	}

	@Override
	public void cleanup() {
		glfwSetMouseButtonCallback(Game.window, null);
	}

}
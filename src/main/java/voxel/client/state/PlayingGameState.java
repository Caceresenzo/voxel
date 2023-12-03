package voxel.client.state;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import java.util.concurrent.CountDownLatch;

import lombok.Getter;
import lombok.SneakyThrows;
import opengl.OpenGL;
import opengl.texture.Atlas;
import opengl.texture.ImageData;
import opengl.texture.Texture;
import voxel.client.Game;
import voxel.client.RemoteServer;
import voxel.client.VoxelHandler;
import voxel.client.chunk.ChunkShaderProgram;
import voxel.client.crosshair.CrossHair;
import voxel.client.crosshair.CrossHairMesh;
import voxel.client.crosshair.CrossHairShaderProgram;
import voxel.client.marker.Marker;
import voxel.client.marker.MarkerMesh;
import voxel.client.marker.MarkerShaderProgram;
import voxel.client.player.LocalPlayer;
import voxel.client.skybox.SkyBox;
import voxel.client.skybox.SkyBoxMesh;
import voxel.client.skybox.SkyBoxShaderProgram;
import voxel.client.world.World;
import voxel.networking.packet.serverbound.play.SetPlayerPositionAndRotationPacket;

public class PlayingGameState implements GameState {

	private final CountDownLatch initializeLatch = new CountDownLatch(1);
	private LocalPlayer player;
	private RemoteServer server;
	private ChunkShaderProgram chunkShaderProgram;
	private @Getter World world;
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

	private CrossHairShaderProgram crossHairShaderProgram;
	private CrossHairMesh crossHairMesh;
	private CrossHair crossHair;

	public PlayingGameState(LocalPlayer player, RemoteServer server) {
		this.player = player;
		this.server = server;
	}

	@Override
	@SneakyThrows
	public void initialize() {
		chunkShaderProgram = ChunkShaderProgram.create();

		chunkShaderProgram.use();
		chunkShaderProgram.atlas.load(0);

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

		crossHairShaderProgram = CrossHairShaderProgram.create();
		crossHairMesh = new CrossHairMesh(crossHairShaderProgram);
		crossHair = new CrossHair(crossHairMesh);

		glfwSetMouseButtonCallback(Game.window, (window, button, action, mods) -> {
			if (voxelHandler == null) {
				return;
			}

			if (action == GLFW_PRESS) {
				if (button == GLFW_MOUSE_BUTTON_LEFT) {
					voxelHandler.destroy();
				}

				if (button == GLFW_MOUSE_BUTTON_RIGHT) {
					voxelHandler.place();
				}
			}
		});

		initializeLatch.countDown();
	}

	@SneakyThrows
	public void awaitInitialize() {
		initializeLatch.await();
	}

	public World setWorld(String name) {
		this.world = new World(name, chunkShaderProgram);
		this.voxelHandler = new VoxelHandler(player, world);

		return world;
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

		if (voxelHandler != null) {
			voxelHandler.update();
		}
	}

	@Override
	public void render() {
		atlas.activate(0);
		chunkShaderProgram.use();
		chunkShaderProgram.view.load(player.getView());

		if (world != null) {
			world.render(player);
		}

		if (voxelHandler != null) {
			marker.render(player, voxelHandler);
		}

		texture.activate(0);
		for (final var otherPlayer : server.getOtherPlayers()) {
			otherPlayer.render(markerShaderProgram, player);
		}

		crossHair.render();
		skyBox.render(player, System.currentTimeMillis());

		OpenGL.checkErrors();
	}

	@Override
	public void cleanup() {
		glfwSetMouseButtonCallback(Game.window, null);
	}

}
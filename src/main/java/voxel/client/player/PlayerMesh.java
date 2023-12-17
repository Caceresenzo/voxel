package voxel.client.player;

import java.io.IOException;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

import it.unimi.dsi.fastutil.floats.FloatList;
import opengl.mesh.MeshException;
import opengl.mesh.SimpleMeshLoader;
import opengl.vertex.BufferUsage;
import opengl.vertex.VertexArray;
import opengl.vertex.VertexBuffer;

public class PlayerMesh {

	private final PlayerShaderProgram shaderProgram;
	private final VertexArray vertexArray;
	private final Matrix4f modelMatrix;

	private PlayerMesh(PlayerShaderProgram shaderProgram, VertexArray vertexArray) {
		this.shaderProgram = shaderProgram;
		this.vertexArray = vertexArray;
		this.modelMatrix = new Matrix4f();
	}

	public void render(Player player) {
		render(player.getPosition(), player.getYaw(), player.getPitch());
	}

	public void render(Vector3fc position, float yaw, float pitch) {
		modelMatrix.identity()
			.translate(position)
			.scale(0.3f)
			.rotate(
				/* TODO Math.toRadians */
				new Quaternionf()
					.rotateY(-yaw)
					.rotateZ(pitch)
			);

		shaderProgram.use();
		shaderProgram.model.load(modelMatrix);

		vertexArray.render();
	}

	public static PlayerMesh create(PlayerShaderProgram shaderProgram, FloatList meshData) {
		final var buffer = VertexBuffer.ofArray(BufferUsage.STATIC_DRAW);
		buffer.store(meshData);

		final var array = new VertexArray(shaderProgram);
		array.add(buffer, List.of(shaderProgram.position, shaderProgram.textureCoordinate));

		return new PlayerMesh(shaderProgram, array);
	}

	public static PlayerMesh create(PlayerShaderProgram playerShaderProgram) throws MeshException, IOException {
		try (final var inputStream = PlayerMesh.class.getResourceAsStream("/models/player.obj")) {
			final var meshData = new SimpleMeshLoader().load(inputStream);

			return create(playerShaderProgram, meshData);
		}
	}

}
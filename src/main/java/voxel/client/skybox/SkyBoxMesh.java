package voxel.client.skybox;

import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glDepthFunc;

import java.awt.Color;

import opengl.vertex.BufferUsage;
import opengl.vertex.VertexArray;
import opengl.vertex.VertexBuffer;
import voxel.client.Settings;
import voxel.client.render.Camera;

public class SkyBoxMesh {

	private final SkyBoxShaderProgram shaderProgram;
	private final VertexArray vertexArray;

	public SkyBoxMesh(SkyBoxShaderProgram shaderProgram) {
		this.shaderProgram = shaderProgram;
		this.vertexArray = createVertexArray();
	}

	private VertexArray createVertexArray() {
		final var vertices = new float[] {
			-1.0f, +1.0f, -1.0f,
			-1.0f, -1.0f, -1.0f,
			+1.0f, -1.0f, -1.0f,
			+1.0f, -1.0f, -1.0f,
			+1.0f, +1.0f, -1.0f,
			-1.0f, +1.0f, -1.0f,

			-1.0f, -1.0f, +1.0f,
			-1.0f, -1.0f, -1.0f,
			-1.0f, +1.0f, -1.0f,
			-1.0f, +1.0f, -1.0f,
			-1.0f, +1.0f, +1.0f,
			-1.0f, -1.0f, +1.0f,

			+1.0f, -1.0f, -1.0f,
			+1.0f, -1.0f, +1.0f,
			+1.0f, +1.0f, +1.0f,
			+1.0f, +1.0f, +1.0f,
			+1.0f, +1.0f, -1.0f,
			+1.0f, -1.0f, -1.0f,

			-1.0f, -1.0f, +1.0f,
			-1.0f, +1.0f, +1.0f,
			+1.0f, +1.0f, +1.0f,
			+1.0f, +1.0f, +1.0f,
			+1.0f, -1.0f, +1.0f,
			-1.0f, -1.0f, +1.0f,

			-1.0f, +1.0f, -1.0f,
			+1.0f, +1.0f, -1.0f,
			+1.0f, +1.0f, +1.0f,
			+1.0f, +1.0f, +1.0f,
			-1.0f, +1.0f, +1.0f,
			-1.0f, +1.0f, -1.0f,

			-1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f, +1.0f,
			+1.0f, -1.0f, -1.0f,
			+1.0f, -1.0f, -1.0f,
			-1.0f, -1.0f, +1.0f,
			+1.0f, -1.0f, +1.0f
		};

		final var buffer = VertexBuffer.ofArray(BufferUsage.STATIC_DRAW);
		buffer.store(vertices);

		final var array = new VertexArray(shaderProgram);
		array.add(buffer);

		return array;
	}

	public void render(Camera camera, long time, Color dayColor, Color nightColor) {
		shaderProgram.use();
		shaderProgram.view.load(camera.getViewWithoutTranslation());

		final var color = interpolate(time, dayColor, nightColor);
		shaderProgram.color.load((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255);

		glDepthFunc(GL_LEQUAL);
		vertexArray.render();
		glDepthFunc(GL_LESS);
	}

	private Color interpolate(long time, Color dayColor, Color nightColor) {
		final var half = Settings.DAY_DURATION / 2;

		time = time % Settings.DAY_DURATION;
		if (time > half) {
			time = Settings.DAY_DURATION - time;
		}

		var percent = (double) time / Settings.DAY_DURATION;
		if (percent <= 0) {
			percent = 0;
		}

		return mixColors(dayColor, nightColor, percent);
	}

	public Color mixColors(Color color1, Color color2, double percent) {
		final var inversePercent = 1.0 - percent;

		final var redPart = (int) (color1.getRed() * percent + color2.getRed() * inversePercent);
		final var greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inversePercent);
		final var bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inversePercent);

		return new Color(redPart, greenPart, bluePart);
	}

}
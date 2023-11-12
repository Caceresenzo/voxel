package voxel.client.graphics.opengl.texture;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_MAX_ANISOTROPY;

import java.lang.ref.Cleaner.Cleanable;

import lombok.Getter;
import voxel.client.graphics.opengl.util.OpenGL;

public class Texture {

	private final @Getter int id;
	private final Cleanable cleanable;

	private Texture(int id) {
		this.id = id;

		this.cleanable = OpenGL.registerForGarbageCollect(this, new DeleteAction(id));
	}

	public void activate(int unit) {
		if (unit < 0)
			throw new IllegalArgumentException("unit is negative");

		if (unit > 31)
			throw new IllegalArgumentException("unit > 31");

		glActiveTexture(GL_TEXTURE0 + unit);
		bind();
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}

	public void delete() {
		cleanable.clean();
	}

	public static Texture create(ImageData imageData) {
		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY, 32.0f);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, imageData.getWidth(), imageData.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData.getBuffer());

		glGenerateMipmap(GL_TEXTURE_2D);
		OpenGL.checkErrors();

		return new Texture(id);
	}

	private record DeleteAction(int textureId) implements Runnable {

		@Override
		public void run() {
			glDeleteTextures(textureId);
		}

	}

}
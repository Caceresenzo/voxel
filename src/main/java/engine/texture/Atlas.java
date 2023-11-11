package engine.texture;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_MAX_ANISOTROPY;

import java.io.IOException;
import java.lang.ref.Cleaner.Cleanable;

import engine.util.OpenGL;
import lombok.Getter;

public class Atlas {

	private final @Getter int id;
	private final Cleanable cleanable;

	private Atlas(int id) {
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
		glBindTexture(GL_TEXTURE_2D_ARRAY, id);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D_ARRAY, id);
	}

	public void delete() {
		cleanable.clean();
	}

	public static Atlas create(ImageData imageData, int layer) throws IOException {
		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY, id);

		glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_ANISOTROPY, 32.0f);
		OpenGL.checkErrors();
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		OpenGL.checkErrors();
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		OpenGL.checkErrors();

		final var height = imageData.getHeight() / layer;
		glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8, imageData.getWidth(), height, layer, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData.getBuffer());
		OpenGL.checkErrors();

		glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
		OpenGL.checkErrors();

		return new Atlas(id);
	}

	private record DeleteAction(int textureId) implements Runnable {

		@Override
		public void run() {
			glDeleteTextures(textureId);
		}

	}

}
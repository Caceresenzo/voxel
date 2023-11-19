package opengl.texture;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_MAX_ANISOTROPY;

import java.lang.ref.Cleaner.Cleanable;

import lombok.Getter;
import opengl.OpenGL;

public class CubeMap {

	private final @Getter int id;
	private final Cleanable cleanable;

	private CubeMap(int id) {
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
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
	}

	public void delete() {
		cleanable.clean();
	}

	public static CubeMap create(ImageData xPositiveImageData, ImageData xNegativeImageData, ImageData yPositiveImageData, ImageData yNegativeImageData, ImageData zPositiveImageData, ImageData zNegativeImageData) {
		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);

		glTexParameterf(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAX_ANISOTROPY, 32.0f);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

		loadAs(GL_TEXTURE_CUBE_MAP_POSITIVE_X, xPositiveImageData);
		loadAs(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, xNegativeImageData);
		loadAs(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, yPositiveImageData);
		loadAs(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, yNegativeImageData);
		loadAs(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, zPositiveImageData);
		loadAs(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, zNegativeImageData);

		glGenerateMipmap(GL_TEXTURE_CUBE_MAP);
		OpenGL.checkErrors();

		return new CubeMap(id);
	}

	private static void loadAs(int target, ImageData imageData) {
		glTexImage2D(target, 0, GL_RGBA8, imageData.getWidth(), imageData.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData.getBuffer());
	}

	private record DeleteAction(int textureId) implements Runnable {

		@Override
		public void run() {
			glDeleteTextures(textureId);
		}

	}

}
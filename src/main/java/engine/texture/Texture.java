package engine.texture;

import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import engine.util.OpenGL;
import lombok.Getter;

public class Texture {
	
	private final @Getter int id;
	
	private Texture(int id) {
		this.id = id;
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
	
	public static Texture load(File file) throws IOException {
		try (final var inputStream = new FileInputStream(file)) {
			return load(inputStream);
		}
	}
	
	public static Texture load(InputStream inputStream) throws IOException {
		return load(inputStream.readAllBytes());
	}
	
	public static Texture load(byte[] bytes) throws IOException {
		final var image = ImageIO.read(new ByteArrayInputStream(bytes));
		
		final var pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		ByteBuffer buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);
		
		for (int h = 0; h < image.getHeight(); h++) {
			for (int w = 0; w < image.getWidth(); w++) {
				int pixel = pixels[h * image.getWidth() + w];
				
				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}
		
		buffer.flip();
		
		int id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 3);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glGenerateMipmap(GL_TEXTURE_2D);
		OpenGL.checkErrors();
		
		return new Texture(id);
	}
	
}
package engine.texture;

import static java.lang.Math.round;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_is_hdr_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBImageResize.STBIR_ALPHA_CHANNEL_NONE;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_SRGB;
import static org.lwjgl.stb.STBImageResize.STBIR_EDGE_CLAMP;
import static org.lwjgl.stb.STBImageResize.STBIR_FILTER_MITCHELL;
import static org.lwjgl.stb.STBImageResize.STBIR_FLAG_ALPHA_PREMULTIPLIED;
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8_generic;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

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
		final var buffer = BufferUtils.createByteBuffer(bytes.length);
		buffer.put(bytes).flip();
		
		final ByteBuffer image;
		final int w, h, comp;
		
		try (MemoryStack stack = stackPush()) {
			IntBuffer wbuffer = stack.mallocInt(1);
			IntBuffer hbuffer = stack.mallocInt(1);
			IntBuffer compbuffer = stack.mallocInt(1);
			
			// Use info to read image metadata without decoding the entire image.
			// We don't need this for this demo, just testing the API.
			if (!stbi_info_from_memory(buffer, wbuffer, hbuffer, compbuffer)) {
				throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
			} else {
				System.out.println("OK with reason: " + stbi_failure_reason());
			}
			
			System.out.println("Image width: " + wbuffer.get(0));
			System.out.println("Image height: " + hbuffer.get(0));
			System.out.println("Image components: " + compbuffer.get(0));
			System.out.println("Image HDR: " + stbi_is_hdr_from_memory(buffer));
			
			// Decode the image
			image = stbi_load_from_memory(buffer, wbuffer, hbuffer, compbuffer, 0);
			if (image == null) {
				throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
			}
			
			w = wbuffer.get(0);
			h = hbuffer.get(0);
			comp = compbuffer.get(0);
		}
		
		int texID = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		
		int format;
		if (comp == 3) {
			if ((w & 3) != 0) {
				glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
			}
			format = GL_RGB;
		} else {
			premultiplyAlpha(w, h, image);
			
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
			
			format = GL_RGBA;
		}
		
		glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);
		
		ByteBuffer input_pixels = image;
		int input_w = w;
		int input_h = h;
		int mipmapLevel = 0;
		while (1 < input_w || 1 < input_h) {
			int output_w = Math.max(1, input_w >> 1);
			int output_h = Math.max(1, input_h >> 1);
			
			ByteBuffer output_pixels = memAlloc(output_w * output_h * comp);
			stbir_resize_uint8_generic(
				input_pixels, input_w, input_h, input_w * comp,
				output_pixels, output_w, output_h, output_w * comp,
				comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
				STBIR_EDGE_CLAMP,
				STBIR_FILTER_MITCHELL,
				STBIR_COLORSPACE_SRGB
			);
			
			if (mipmapLevel == 0) {
				stbi_image_free(image);
			} else {
				memFree(input_pixels);
			}
			
			glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);
			
			input_pixels = output_pixels;
			input_w = output_w;
			input_h = output_h;
		}
		if (mipmapLevel == 0) {
			stbi_image_free(image);
		} else {
			memFree(input_pixels);
		}
		
		return new Texture(texID);
	}
	
	private static void premultiplyAlpha(int w, int h, ByteBuffer image) {
		int stride = w * 4;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = y * stride + x * 4;
				
				float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
				image.put(i + 0, (byte) round(((image.get(i + 0) & 0xFF) * alpha)));
				image.put(i + 1, (byte) round(((image.get(i + 1) & 0xFF) * alpha)));
				image.put(i + 2, (byte) round(((image.get(i + 2) & 0xFF) * alpha)));
			}
		}
	}
	
}
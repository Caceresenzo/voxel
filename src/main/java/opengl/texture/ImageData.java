package opengl.texture;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ImageData {

	private final int width;
	private final int height;
	private final ByteBuffer buffer;

	public int getComponents() {
		return 4;
	}

	public static ImageData load(InputStream inputStream, boolean horizontalFlip) throws IOException {
		return load(inputStream, horizontalFlip, false);
	}

	public static ImageData load(InputStream inputStream, boolean horizontalFlip, boolean verticalFlip) throws IOException {
		var image = ImageIO.read(inputStream);

		if (horizontalFlip || verticalFlip) {
			var sizeX = 1;
			var sizeY = 1;
			var width = 0;
			var height = 0;

			if (horizontalFlip) {
				sizeX = -1;
				width = 0;
				width = -image.getWidth(null);
			}

			if (verticalFlip) {
				sizeY = -1;
				height = -image.getHeight(null);
			}

			final var transform = AffineTransform.getScaleInstance(sizeX, sizeY);
			transform.translate(width, height);

			final var opeartion = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = opeartion.filter(image, null);
		}

		final var pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		final var buffer = ByteBuffer.allocateDirect(image.getWidth() * image.getHeight() * 4);

		for (var height = 0; height < image.getHeight(); height++) {
			for (var width = 0; width < image.getWidth(); width++) {
				final var pixel = pixels[height * image.getWidth() + width];

				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) (pixel & 0xFF));
				buffer.put((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip();

		return new ImageData(image.getWidth(), image.getHeight(), buffer);
	}

}
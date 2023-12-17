package opengl.mesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import lombok.Cleanup;

public class SimpleMeshLoader {

	public static Pattern FACE_PATTERN = Pattern.compile("(\\d+)(?:\\/(\\d+))?(?:\\/(\\d+))?");

	public FloatArrayList load(InputStream inputStream) throws MeshException, IOException {
		final var vertices = new FloatArrayList();
		final var textures = new FloatArrayList();

		final var outputData = new FloatArrayList();

		final @Cleanup var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			final @Cleanup var scanner = new Scanner(line);

			final var first = scanner.next();
			switch (first) {

				case "#":
				case "mtllib":
				case "o":
				case "usemtl":
				case "usemap":
				case "vn":
				case "s":
				case "g": {
					continue;
				}

				case "v": {
					if (!scanner.hasNextFloat()) {
						throw new MeshException("vertex: x: invalid");
					}

					final var x = scanner.nextFloat();

					if (!scanner.hasNextFloat()) {
						throw new MeshException("vertex: y: invalid");
					}

					final var y = scanner.nextFloat();

					if (!scanner.hasNextFloat()) {
						throw new MeshException("vertex: z: invalid");
					}

					final var z = scanner.nextFloat();

					if (scanner.hasNext()) {
						throw new MeshException("vertex: content remain in line");
					}

					vertices.add(x);
					vertices.add(y);
					vertices.add(z);

					continue;
				}

				case "vt": {
					if (!scanner.hasNextFloat()) {
						throw new MeshException("texture vertex: x: invalid");
					}

					final var x = scanner.nextFloat();

					if (!scanner.hasNextFloat()) {
						throw new MeshException("texture vertex: y: invalid");
					}

					final var y = scanner.nextFloat();

					if (scanner.hasNext()) {
						scanner.nextFloat(); /* z */

						if (scanner.hasNext()) {
							throw new MeshException("texture vertex: content remain in line");
						}
					}

					textures.add(x);
					textures.add(y);

					continue;
				}

				case "f": {
					final var x = nextFace(scanner, "x");
					final var y = nextFace(scanner, "y");
					final var z = nextFace(scanner, "z");

					FaceIndices w = null;
					if (scanner.hasNext()) {
						w = nextFace(scanner, "w");

						if (scanner.hasNext()) {
							throw new MeshException("face: content remain in line");
						}
					}

					addAll(vertices, textures, x, outputData);
					addAll(vertices, textures, y, outputData);
					addAll(vertices, textures, z, outputData);

					if (w != null) {
						addAll(vertices, textures, x, outputData);
						addAll(vertices, textures, z, outputData);
						addAll(vertices, textures, w, outputData);
					}

					continue;
				}

				default: {
					throw new UnsupportedOperationException("unknown line: " + line);
				}

			}
		}

		return outputData;
	}

	private void addAll(FloatArrayList vertices, FloatArrayList textures, FaceIndices indices, FloatArrayList outputData) {
		addAll(vertices, indices.vertice() - 1, 3, outputData);
		addAll(textures, indices.texture() - 1, 2, outputData);
	}

	private void addAll(FloatArrayList source, int index, int size, FloatArrayList destination) {
		final var elements = source.elements();

		index *= size;

		for (var jndex = 0; jndex < size; ++jndex) {
			destination.add(elements[index + jndex]);
		}
	}

	private FaceIndices nextFace(Scanner scanner, String axis) throws MeshException {
		if (!scanner.hasNext(FACE_PATTERN)) {
			throw new MeshException("face: %s: invalid".formatted(axis));
		}

		final var token = scanner.next(FACE_PATTERN);
		final var parts = token.split("/");

		final var vertice = Integer.parseInt(parts[0]);
		final var texture = Integer.parseInt(parts[1]);

		return new FaceIndices(
			vertice,
			texture
		);
	}

	public record FaceIndices(
		int vertice,
		int texture
	) {}

}
package voxel.client.graphics.opengl.shader;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Accessors(fluent = true)
public enum DataType {

	BYTE(GL_BYTE, 1, false),
	UNSIGNED_BYTE(GL_UNSIGNED_BYTE, 1, false),
	SHORT(GL_SHORT, 2, false),
	UNSIGNED_SHORT(GL_UNSIGNED_SHORT, 2, false),
	INTEGER(GL_INT, 4, false),
	UNSIGNED_INTEGER(GL_UNSIGNED_INT, 4, false),
	FLOAT(GL_FLOAT, 4, false),
	DOUBLE(GL_DOUBLE, 8, false);

	private final @Getter int value;
	private final @Getter int size;
	private final @Getter boolean unsigned;

}
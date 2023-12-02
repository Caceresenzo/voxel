package opengl.draw;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import opengl.OpenGLConstant;

@Getter
@RequiredArgsConstructor
public enum BeginMode implements OpenGLConstant {

	TRIANGLES(GL_TRIANGLES),
	LINES(GL_LINES);

	private final int value;

}
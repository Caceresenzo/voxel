package opengl.vertex;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import opengl.OpenGLConstant;

@Getter
@RequiredArgsConstructor
public enum UsageType implements OpenGLConstant {

	STATIC_DRAW(GL_STATIC_DRAW),
	DYNAMIC_DRAW(GL_DYNAMIC_DRAW);

	private final int value;

}
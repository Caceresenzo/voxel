package opengl.vertex;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import opengl.OpenGLConstant;

@Getter
@RequiredArgsConstructor
public enum BufferBindTarget implements OpenGLConstant {

	ARRAY(GL_ARRAY_BUFFER),
	ELEMENT_ARRAY(GL_ELEMENT_ARRAY_BUFFER);

	private final int value;

}
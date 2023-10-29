package engine.vertex;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter(AccessLevel.PROTECTED)
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum BufferType {
	
	ARRAY(GL_ARRAY_BUFFER),
	ELEMENT_ARRAY(GL_ELEMENT_ARRAY_BUFFER);
	
	private final int value;
	
}
package engine.vertex;

import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter(AccessLevel.PROTECTED)
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum UsageType {
	
	STATIC_DRAW(GL_STATIC_DRAW),
	DYNAMIC_DRAW(GL_DYNAMIC_DRAW);
	
	private final int value;
	
}
package engine.shader;

import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.List;
import java.util.stream.IntStream;

import lombok.Getter;

@Getter
public class Uniform extends Variable {
	
	protected Uniform(ShaderProgram program, String name) {
		super(program, name);
	}
	
	@Override
	protected int findLocation(ShaderProgram program) {
		return glGetUniformLocation(program.getId(), name);
	}
	
	@Override
	protected List<String> listNames(ShaderProgram program) {
		final var count = glGetProgrami(program.getId(), GL_ACTIVE_UNIFORMS);
		
		try (final var stack = stackPush()) {
			final var ignored = stack.mallocInt(1);
			
			return IntStream.range(0, count)
				.mapToObj((index) -> glGetActiveUniform(program.getId(), 0, ignored, ignored))
				.toList();
		}
	}
	
}
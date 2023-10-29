package engine.shader;

import java.util.List;

import lombok.Getter;

@Getter
public abstract class Variable {
	
	protected final String name;
	protected final int location;
	
	protected Variable(ShaderProgram program, String name) {
		this.name = name;
		
		this.location = findLocation(program);
		if (this.location == -1) {
			final var available = listNames(program);
			System.err.println("variable `%s` not found, available: %s".formatted(name, available));
		}
		
		System.out.println("%s(name=%s, location=%d)".formatted(getClass().getSimpleName(), name, location));
	}
	
	protected abstract int findLocation(ShaderProgram program);
	
	protected abstract List<String> listNames(ShaderProgram program);
	
}
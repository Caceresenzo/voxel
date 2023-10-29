package engine.shader;

import lombok.Getter;

@Getter
public abstract class Variable {
	
	protected final String name;
	protected final int location;
	
	protected Variable(ShaderProgram program, String name) {
		this.name = name;
		
		this.location = findLocation(program);
		if (this.location == -1) {
			throw new IllegalStateException("variable not found: " + name);
		}
		
		System.out.println("%s(name=%s, location=%d)".formatted(getClass().getSimpleName(), name, location));
	}
	
	protected abstract int findLocation(ShaderProgram program);
	
}
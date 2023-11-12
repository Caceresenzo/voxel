package voxel.client.graphics.opengl.shader.variable.attribute;

import static org.lwjgl.opengl.GL30.glVertexAttribIPointer;

import voxel.client.graphics.opengl.shader.DataType;
import voxel.client.graphics.opengl.shader.ShaderProgram;

public class IntegerAttribute extends Attribute {

	public IntegerAttribute(ShaderProgram program, String name, int size, DataType dataType) {
		super(program, name, size, dataType);
	}

	protected void doLink(int stride, int offset) {
		glVertexAttribIPointer(location, size, dataType.value(), stride, offset);
	}

	public static IntegerAttribute ofInteger(ShaderProgram program, String name, int size, boolean unsigned) {
		final var dataType = unsigned
			? DataType.UNSIGNED_INTEGER
			: DataType.INTEGER;

		return new IntegerAttribute(program, name, size, dataType);
	}

}
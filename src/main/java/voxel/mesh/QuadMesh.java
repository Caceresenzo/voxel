package voxel.mesh;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

import engine.util.MathUtils;
import engine.util.OpenGL;
import engine.vertex.BufferType;
import engine.vertex.UsageType;
import engine.vertex.VertexArray;
import engine.vertex.VertexBuffer;
import lombok.Getter;

@Getter
public class QuadMesh {
	
	private final QuadShaderProgram program;
	private final VertexArray vertexArray;
	
	public QuadMesh(QuadShaderProgram program) {
		this.program = program;
		
		this.vertexArray = createVertexArray();
	}
	
	private VertexArray createVertexArray() {
		final var vertexData = getVertexData();
		
		final var buffer = new VertexBuffer(BufferType.ARRAY, UsageType.STATIC_DRAW);
		buffer.store(vertexData);
		
		final var array = new VertexArray().add(buffer);

		program.use();
		
		final var stride = 4 * 6;
		program.position.link(stride, 0);
		
		final var offset = 4 * 3;
		program.color.link(stride, offset);
		
		return array;
	}
	
	public void render() {
		program.use();
		
		program.opacity.load((float) Math.sin((System.currentTimeMillis() / 100)));
		
		vertexArray.bind();
		
		glDrawArrays(GL_TRIANGLES, 0, 36);
		OpenGL.checkErrors();
	}
	
	public float[] getVertexData() {
		float[] vertices = {
			0.5f, 0.5f, 0.0f,
			-0.5f, 0.5f, 0.0f,
			-0.5f, -0.5f, 0.0f,
			0.5f, 0.5f, 0.0f,
			-0.5f, -0.5f, 0.0f,
			0.5f, -0.5f, 0.0f
		};
		
		float[] colors = {
			0f, 1f, 0f,
			1f, 0f, 0f,
			1f, 1f, 0f,
			0f, 1f, 0f,
			1f, 1f, 0f,
			0f, 0f, 1f
		};
		
		return MathUtils.horizontalStack(vertices, 3, colors, 3);
	}
	
}
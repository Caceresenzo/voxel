#version 330 core

layout (location = 0) out vec4 fragColor;

in vec2 uv;

uniform sampler2D u_texture;

void main()
{
	fragColor = texture(u_texture, uv);
	if (fragColor == vec4(0))
		fragColor = vec4(1, 0, 0, 1);
}

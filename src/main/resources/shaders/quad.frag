#version 330 core

uniform float opacity;

layout (location = 0) out vec4 fragColor;

in vec3 color;

void main()
{
	fragColor = vec4(color, opacity);
}
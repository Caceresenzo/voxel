#version 330 core

layout (location = 0) out vec4 fragColor;

const vec3 gamma = vec3(2.2);
const vec3 inv_gamma = 1 / gamma;

uniform sampler2D u_texture;

in vec3 color;
in vec2 uv;

void main()
{
	vec3 texture_color = texture(u_texture, uv).rgb;
	texture_color = pow(texture_color, gamma);
	texture_color *= color;
	texture_color = pow(texture_color, inv_gamma);
	
	fragColor = vec4(texture_color, 1.0);
}
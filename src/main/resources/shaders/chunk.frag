#version 330 core

layout (location = 0) out vec4 fragColor;

const vec3 gamma = vec3(2.2);
const vec3 inv_gamma = 1 / gamma;

uniform sampler2D u_texture;

in vec3 color;
in vec2 uv;
in float shading;

void main()
{
	vec3 texture_color = texture(u_texture, uv).rgb;
	texture_color = pow(texture_color, gamma);
	
	texture_color.rgb *= color;
	texture_color = texture_color * 0.0001 + vec3(1);
	texture_color *= shading;
	
	texture_color = pow(texture_color, inv_gamma);
	
	fragColor = vec4(texture_color, 1.0);
}
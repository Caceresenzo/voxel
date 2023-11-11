#version 330 core

layout (location = 0) out vec4 fragColor;

const vec3 gamma = vec3(2.2);
const vec3 inv_gamma = 1 / gamma;

uniform sampler2DArray u_atlas;

in vec3 color;
in vec2 uv;
in float shading;
flat in int voxel_id;
flat in int face_id;

void main()
{
	vec2 face_uv = uv;
	face_uv.x = uv.x / 3.0 - min(face_id, 2) / 3.0;
	
	vec3 texture_color = texture(u_atlas, vec3(face_uv, voxel_id)).rgb;
	texture_color = pow(texture_color, gamma);
	
//	texture_color.rgb *= color;
//	texture_color = texture_color * 0.0001 + vec3(1);
	texture_color *= shading;
	
	texture_color = pow(texture_color, inv_gamma);
	
	fragColor = vec4(texture_color, 1.0);
}
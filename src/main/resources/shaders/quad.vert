#version 330 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec3 in_color;

uniform mat4 m_projection;
uniform mat4 m_view;
uniform mat4 m_model;

out vec3 color;

void main()
{
	color = in_color;
	mat4 pvm = m_projection * m_view * m_model;
	gl_Position = pvm * vec4(in_position, 1.0);
}
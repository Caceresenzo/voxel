#version 330 core

layout (location = 0) in vec3 in_position;

uniform mat4 m_projection;
uniform mat4 m_view;

void main()
{
	vec4 pos = m_projection * m_view * vec4(in_position, 1.0);
	gl_Position = pos.xyww;
}

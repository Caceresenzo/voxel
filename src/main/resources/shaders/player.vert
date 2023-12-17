#version 330 core

layout (location = 0) in vec3 in_position;
layout (location = 1) in vec2 in_tex_coord;

uniform mat4 m_projection;
uniform mat4 m_view;
uniform mat4 m_model;

out vec2 uv;

void main() {
    uv = in_tex_coord;
    gl_Position = m_projection * m_view * m_model * vec4(in_position, 1.0);
}

#version 330 core

layout (location = 0) in vec2 in_tex_coord;
layout (location = 1) in vec3 in_position;

uniform mat4 m_projection;
uniform mat4 m_view;
uniform mat4 m_model;

out vec2 uv;

void main() {
    uv = in_tex_coord;
    vec3 pos = in_position;
    gl_Position = m_projection * m_view * m_model * vec4((pos - 0.5) * 1.01 + 0.5, 1.0);
}

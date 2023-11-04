#version 330 core

layout (location = 0) in uint packed_data;
int x, y, z;
int voxel_id;
int face_id;
int ao_id;
int flip_id;

uniform mat4 m_projection;
uniform mat4 m_view;
uniform mat4 m_model;

out vec3 color;
out vec2 uv;
out float shading;

const float ao_values[4] = float[4](
	0.1, 0.25, 0.5, 1.0
);

const float face_shading[6] = float[6](
	1.0, 0.5, /* top bottom */
	0.5, 0.8, /* right left */
	0.5, 0.8 /* front back */
);

const vec2 uv_coords[4] = vec2[4](
	vec2(0, 0), vec2(0, 1),
	vec2(1, 0), vec2(1, 1)
);

const int uv_indices[24] = int[24](
	1, 0, 2, 1, 2, 3, /* texture coordinates indices for vertices of an even face */
	3, 0, 2, 3, 1, 0, /* odd face */
	3, 1, 0, 3, 0, 2, /* even flipped face */
	1, 2, 3, 1, 0, 2 /* odd flipped face */
);

vec3 hash31(float p)
{
	vec3 p3 = fract(vec3(p * 21.2) * vec3(0.1031, 0.1030, 0.0973));
	p3 += dot(p3, p3.yzx + 33.33);
	return fract((p3.xxy + p3.yzz) * p3.zyx) + 0.05;
}

const uint ao_id_shift = 1u;
const uint face_id_shift = ao_id_shift + 2u;
const uint voxel_id_shift = face_id_shift + 3u;
const uint z_shift = voxel_id_shift + 8u;
const uint y_shift = z_shift + 6u;
const uint x_shift = y_shift + 6u;

const uint xyz_mask = 63u;
const uint voxel_id_mask = 255u;
const uint face_id_mask = 7u;
const uint ao_id_mask = 3u;
const uint flip_id_mask = 1u;

void unpack(uint packed_data)
{
    x = int(packed_data >> x_shift);
    y = int((packed_data >> y_shift) & xyz_mask);
    z = int((packed_data >> z_shift) & xyz_mask);

    voxel_id = int((packed_data >> voxel_id_shift) & voxel_id_mask);
    face_id = int((packed_data >> face_id_shift) & face_id_mask);
    ao_id = int((packed_data >> ao_id_shift) & ao_id_mask);
    flip_id = int(packed_data & flip_id_mask);
}

void main()
{
    unpack(packed_data);
    
    vec3 in_position = vec3(x, y, z);
	int uv_index = gl_VertexID % 6 + ((face_id & 1) + flip_id * 2) * 6;
	uv = uv_coords[uv_indices[uv_index]];
	
	color = hash31(voxel_id);
	shading = face_shading[face_id] * ao_values[ao_id];
	mat4 pvm = m_projection * m_view * m_model;
	gl_Position = pvm * vec4(in_position, 1.0);
}

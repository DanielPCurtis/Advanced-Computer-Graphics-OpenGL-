#version 430

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

out vec4 varyingColor;

const vec4 vertices[6] = vec4[6]
		(

		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(12.0, 0.0, 0.0, 1.0),
		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(0.0, 12.0, 0.0, 1.0),
		vec4(0.0, 0.0, 0.0, 1.0),
		vec4(0.0, 0.0, 12.0, 1.0));

void main(void)
{

	gl_Position = proj_matrix * mv_matrix * vertices[gl_VertexID];

	if(gl_VertexID < 2){
			varyingColor = vec4(1.0, 0.0, 0.0, 1.0);
		}

		else if(gl_VertexID < 4){
			varyingColor = vec4(0.0, 1.0, 0.0, 1.0);
		}

		else if(gl_VertexID > 3){
			varyingColor = vec4(0.0, 0.0, 1.0, 1.0);
		}
}

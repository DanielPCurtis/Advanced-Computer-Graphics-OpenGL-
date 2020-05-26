#version 430

layout (location=0) in vec4 vertPos;
layout (location=1) in vec4 vertNormal;

out vec3 varyingNormal;

out vec4 shadow_coord;

struct PositionalLight
{	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	vec3 position;
};
struct Material
{	vec4 ambient;  
	vec4 diffuse;  
	vec4 specular;  
	float shininess;
};

uniform vec4 globalSpot;
uniform PositionalLight light2;
uniform Material material;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform mat4 norm_matrix;
uniform mat4 shadowMVP;

layout (binding=0) uniform sampler2DShadow shadowTex;

void main(void)
{	varyingNormal = (norm_matrix * vertNormal).xyz;
	//shadow_coord = shadowMVP * vec4(vertPos,1.0);
	gl_Position = mv_matrix * vertPos;
}

#version 430

layout (triangles) in;

in vec3 varyingNormal[];

out vec3 varyingVertPosG;
out vec3 varyingLightDirG;
out vec3 varyingNormalG;
layout (binding=0) uniform sampler2DShadow shadowTex;

layout (line_strip, max_vertices=8) out;

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

float sLen = 0.1;

void main(void)
{	// offset the three triangle vertices by the surface normal
	vec3 op0 = gl_in[0].gl_Position.xyz;
	vec3 op1 = gl_in[1].gl_Position.xyz;
	vec3 op2 = gl_in[2].gl_Position.xyz;
	vec3 ep0 = gl_in[0].gl_Position.xyz + varyingNormal[0]*sLen;
	vec3 ep1 = gl_in[1].gl_Position.xyz + varyingNormal[1]*sLen;
	vec3 ep2 = gl_in[2].gl_Position.xyz + varyingNormal[2]*sLen;
	
	// compute the new points comprising a small line segment
	vec3 newPoint1 = (op0 + op1 + op2)/3.0;	// start point
	vec3 newPoint2 = (ep0 + ep1 + ep2)/3.0;	// end point
	vec3 newPoint3 = (op0 + op1 + op2)/3.0; // start point2
	vec3 newPoint4 = (ep0); // end point2
	vec3 newPoint5 = (op0 + op1 + op2)/3.0; // start point3
	vec3 newPoint6 = (ep1); // end point3
	vec3 newPoint7 = (op0 + op1 + op2)/3.0; // start point4
	vec3 newPoint8 = (ep2); // end point

	gl_Position = proj_matrix * vec4(newPoint1, 1.0);
	varyingVertPosG = newPoint1;
	varyingLightDirG = light2.position - newPoint1;
	varyingNormalG = varyingNormal[0];

	EmitVertex();
	
	gl_Position = proj_matrix * vec4(newPoint2, 1.0);
	varyingVertPosG = newPoint2;
	varyingLightDirG = light2.position - newPoint2;
	varyingNormalG = varyingNormal[1];
	EmitVertex();

	gl_Position = proj_matrix * vec4(newPoint3, 1.0);
		varyingVertPosG = newPoint3;
		varyingLightDirG = light2.position - newPoint3;
		varyingNormalG = varyingNormal[0];
		EmitVertex();

	gl_Position = proj_matrix * vec4(newPoint4, 1.0);
		varyingVertPosG = newPoint4;
		varyingLightDirG = light2.position - newPoint4;
		varyingNormalG = varyingNormal[1];
		EmitVertex();

	gl_Position = proj_matrix * vec4(newPoint5, 1.0);
		varyingVertPosG = newPoint5;
		varyingLightDirG = light2.position - newPoint5;
		varyingNormalG = varyingNormal[0];
		EmitVertex();

	gl_Position = proj_matrix * vec4(newPoint6, 1.0);
		varyingVertPosG = newPoint6;
		varyingLightDirG = light2.position - newPoint6;
		varyingNormalG = varyingNormal[1];
		EmitVertex();

	gl_Position = proj_matrix * vec4(newPoint7, 1.0);
		varyingVertPosG = newPoint7;
		varyingLightDirG = light2.position - newPoint7;
		varyingNormalG = varyingNormal[0];
		EmitVertex();

	gl_Position = proj_matrix * vec4(newPoint8, 1.0);
		varyingVertPosG = newPoint8;
		varyingLightDirG = light2.position - newPoint8;
		varyingNormalG = varyingNormal[1];
		EmitVertex();


	EndPrimitive();
}

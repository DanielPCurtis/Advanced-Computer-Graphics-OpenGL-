#version 430

in vec3 varyingVertPosG;
in vec3 varyingLightDirG;
in vec3 varyingNormalG;

 
out vec4 fragColor;

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

uniform vec4 globalAmbient;
uniform vec4 globalSpot;
uniform PositionalLight light;
uniform PositionalLight light2;
uniform Material material;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform mat4 norm_matrix;
uniform mat4 shadowMVP;
layout (binding=0) uniform sampler2DShadow shadowTex;


void main(void)
{
	// normalized the light, normal, and eye direction vectors
	vec3 L = normalize(varyingLightDirG);
	vec3 N = normalize(varyingNormalG);
	vec3 V = normalize(-varyingVertPosG);
	
	// compute light reflection vector, with respect N:
	vec3 R = normalize(reflect(-L, N));
	
	// get the angle between the light and surface normal
	float cosTheta = dot(L,N);
	
	// angle between the view vector and reflected light:
	float cosPhi = dot(V,R);

	/*float swidth = 2.5;
			vec2 o = mod(floor(gl_FragCoord.xy), 2.0) * swidth;
			shadowFactor += lookup(-1.5*swidth + o.x,  1.5*swidth - o.y);
			shadowFactor += lookup(-1.5*swidth + o.x, -0.5*swidth - o.y);
			shadowFactor += lookup( 0.5*swidth + o.x,  1.5*swidth - o.y);
			shadowFactor += lookup( 0.5*swidth + o.x, -0.5*swidth - o.y);
			shadowFactor = shadowFactor / 4.0;

	vec4 shadowColor = (globalAmbient * material.ambient
							+ light.ambient * material.ambient) +
							(globalSpot * material.ambient + light2.ambient * material.ambient);

	vec4 lightedColor = (light.diffuse * material.diffuse * max(dot(L,N),0.0)
							+ light.specular * material.specular
							* pow(max(cosPhi,0.0),material.shininess*3.0)) +
							(light2.diffuse * material.diffuse * max(dot(L,N),0.0)
							+ light2.specular * material.specular
							* pow(max(cosPhi,0.0),material.shininess*3.0));

	fragColor = vec4((shadowColor.xyz + shadowFactor*(lightedColor.xyz)),1.0);


*/
	fragColor = (globalAmbient * material.ambient  +  light.ambient * material.ambient
				+ light.diffuse * material.diffuse * max(cosTheta,0.0)
				+ light.specular * material.specular * pow(max(cosPhi,0.0), material.shininess))
				+ (globalSpot * material.ambient  +  light2.ambient * material.ambient
						+ light2.diffuse * material.diffuse * max(cosTheta,0.0)
						+ light2.specular * material.specular * pow(max(cosPhi,0.0), material.shininess));

}

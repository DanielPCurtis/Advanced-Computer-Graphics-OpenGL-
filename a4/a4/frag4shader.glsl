#version 430

in vec3 varyingNormal, varyingLightDir, varyingVertPos, varyingHalfVec;
in vec4 shadow_coord;
in vec2 tc;
out vec4 fragColor;

struct GlobalLight
{	vec4 ambient, diffuse, specular;
	vec3 position;
};
 
struct PositionalLight
{	vec4 ambient, diffuse, specular;
	vec3 position;
};


struct Material
{	vec4 ambient, diffuse, specular;
	float shininess;
};

uniform vec4 globalAmbient;
uniform vec4 globalSpot;
uniform GlobalLight light2;
uniform PositionalLight light;
uniform Material material;
uniform mat4 mv_matrix; 
uniform mat4 proj_matrix;
uniform mat4 norm_matrix;
uniform mat4 shadowMVP;
layout (binding=0) uniform sampler2DShadow shadowTex;
layout (binding=1) uniform sampler2D s;


float lookup(float x, float y)
{  	float t = textureProj(shadowTex, shadow_coord + vec4(x * 0.001 * shadow_coord.w,
                                                         y * 0.001 * shadow_coord.w,
                                                         -0.01, 0.0));
	return t;
}

void main(void)
{	float shadowFactor=0.0;
	vec3 L = normalize(varyingLightDir);
	vec3 N = normalize(varyingNormal);
	vec3 V = normalize(-varyingVertPos);
	vec3 H = normalize(varyingHalfVec);

	float swidth = 2.5;
		vec2 o = mod(floor(gl_FragCoord.xy), 2.0) * swidth;
		shadowFactor += lookup(-1.5*swidth + o.x,  1.5*swidth - o.y);
		shadowFactor += lookup(-1.5*swidth + o.x, -0.5*swidth - o.y);
		shadowFactor += lookup( 0.5*swidth + o.x,  1.5*swidth - o.y);
		shadowFactor += lookup( 0.5*swidth + o.x, -0.5*swidth - o.y);
		shadowFactor = shadowFactor / 4.0;

		// hi res PCF
	/*	float width = 2.5;
		float endp = width * 3.0 + width/2.0;
		for (float m=-endp ; m<=endp ; m=m+width)
		{	for (float n=-endp ; n<=endp ; n=n+width)
			{	shadowFactor += lookup(m,n);
		}	}
		shadowFactor = shadowFactor / 64.0;
	*/
		// this would produce normal hard shadows
	//	shadowFactor = lookup(0.0, 0.0);


	vec4 texColor = texture(s,tc);

	vec4 shadowColor = texColor * ((globalAmbient + light.ambient )
						+ (globalSpot + light2.ambient));

	vec4 lightedColor = texColor * ((light.diffuse * max(dot(L,N),0.0)
						+ light.specular * pow(max(dot(H,N),0.0),material.shininess*3.0)) +
						 (light2.diffuse * max(dot(L,N),0.0)
						+ light2.specular * pow(max(dot(H,N),0.0),material.shininess*3.0)));

	fragColor = vec4((shadowColor.xyz + shadowFactor*(lightedColor.xyz)),1.0);

	
}

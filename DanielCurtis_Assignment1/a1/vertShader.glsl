#version 430

uniform float inc;
uniform float incx;
uniform float incy;
uniform float size;
uniform float color;
uniform float rotate;
uniform float upDown;
out vec4 colorIn;


void main(void)
{ 	if (gl_VertexID == 0)
	{
		if(color == 1)			{ colorIn = vec4(1.0, 1.0, 1.0, 0.0); }
		else					{ colorIn = vec4(1.0, 0.0, 0.0, 0.0); }

		if(rotate==1.0)			{ gl_Position = vec4( size * (0.25 + incx), size * (-0.25 + incy), 0.0, 1.0); }
		else if(upDown == 1.0)	{ gl_Position = vec4( size * 0.25, size * ( -0.25 + inc ), 0.0, 1.0 ); }
		else					{ gl_Position = vec4(size * 0.25, size * -0.25, 0.0, 1.0); }
	}

	else if (gl_VertexID == 1)
	{
		if(color == 1)			{ colorIn = vec4(1.0, 1.0, 1.0, 0.0); }
		else					{ colorIn = vec4(0.0, 1.0, 0.0, 0.0); }

		if(rotate == 1.0)		{ gl_Position = vec4(size * ( -0.25 + incx ), size * ( -0.25 + incy ), 0.0, 1.0 ); }
		else if(upDown == 1.0)	{ gl_Position = vec4( size * (-0.25), size * ( -0.25 + inc ), 0.0, 1.0 ); }
		else					{ gl_Position = vec4(size * -0.25, size * -0.25, 0.0, 1.0); }
	}

	else
	{
		if(color == 1)			{ colorIn = vec4(1.0, 1.0, 1.0, 0.0); }
		else					{ colorIn = vec4(0.0, 0.0, 1.0, 0.0); }

		if(rotate==1.0)			{ gl_Position = vec4(size*(incx), size*(0.25+incy), 0.0, 1.0); }
		else if(upDown == 1.0)	{ gl_Position = vec4(size*(0.0), size*(0.25+inc), 0.0, 1.0); }
		else					{ gl_Position = vec4(size * 0.0, size * 0.25, 0.0, 1.0); }
	}
}

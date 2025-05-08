#version 150

uniform sampler2D Sampler0; // logic texture
uniform sampler2D Sampler1; // scanline texture

in vec2 texCoord0;
in vec4 vertexColor;

uniform float GameTime;
uniform vec4 ColorModulator;
uniform vec2 LogicUV;

out vec4 fragColor;

vec2 transformScanlineUV(vec2 uv)
{
	vec2 transformedUV = uv;
	vec2 motion = vec2(0, 150);
	vec2 translation = vec2(GameTime) * motion;
	transformedUV += translation;
	transformedUV.x *= LogicUV.x / 16.0;
	transformedUV.y *= LogicUV.y / 16.0;
	transformedUV *= 8.0;
	return fract(transformedUV);
}

void main()
{
	vec4 color = texture(Sampler0, texCoord0) * vertexColor;
	vec4 scanline = texture(Sampler1, transformScanlineUV(texCoord0));
	color *= scanline;
	if (color.a == 0.0)
	{
		discard;
	}
	fragColor = color * ColorModulator;
}

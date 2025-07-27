#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0; // logic texture
uniform sampler2D Sampler3; // scanline texture

uniform float GameTime;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

vec2 transformScanlineUV(vec2 uv)
{
	vec2 transformedUV = uv;
	vec2 motion = vec2(0, 150.0 / 32.0);
	vec2 translation = vec2(GameTime) * motion;
	transformedUV += translation;
	transformedUV *= 256.0;
	return fract(transformedUV);
}

void main()
{
	vec4 color = texture(Sampler0, texCoord0);
	vec4 scanline = texture(Sampler3, transformScanlineUV(texCoord0));
	color *= scanline;
	if (color.a == 0.0)
	{
		discard;
	}
	color *= vertexColor * ColorModulator;
	color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
	color *= lightMapColor;
	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}

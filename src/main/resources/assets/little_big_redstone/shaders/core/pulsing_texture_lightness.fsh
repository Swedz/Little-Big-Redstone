#version 330

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;

uniform float GameTime;
uniform vec4 ColorModulator;

out vec4 fragColor;

float pulsingAlpha()
{
	float interval = 30.0;
	float t = fract(GameTime * (24000.0 / interval));
	float wave = (sin(t * 6.28318) + 1.0) / 2.0;
	return mix(0.25, 0.5, wave);
}

void main()
{
	vec4 color = texture(Sampler0, texCoord0) * vertexColor;
	vec4 overlay = vec4(1.0, 1.0, 1.0, pulsingAlpha());
	vec3 shiftedColor = mix(color.rgb, overlay.rgb, overlay.a);
	color = vec4(shiftedColor, color.a);
	if (color.a == 0.0)
	{
		discard;
	}
	fragColor = color * ColorModulator;
}

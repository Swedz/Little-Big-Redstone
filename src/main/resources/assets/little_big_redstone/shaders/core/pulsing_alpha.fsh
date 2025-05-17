#version 150

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
	vec4 color = vertexColor;
	color.a = pulsingAlpha();
	if (color.a == 0.0)
	{
		discard;
	}
	fragColor = color * ColorModulator;
}

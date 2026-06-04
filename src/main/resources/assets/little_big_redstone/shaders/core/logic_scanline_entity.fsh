#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <little_big_redstone:logic_scanline.glsl>

uniform sampler2D Sampler0;

in vec4 lightMapColor;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main()
{
	vec4 color = texture(Sampler0, texCoord0) * vertexColor;
	color.rgb *= logic_scanline(texCoord0, textureSize(Sampler0, 0));
	if (color.a == 0.0)
	{
		discard;
	}
	color *= ColorModulator;
    color *= lightMapColor;
    fragColor = color;
}

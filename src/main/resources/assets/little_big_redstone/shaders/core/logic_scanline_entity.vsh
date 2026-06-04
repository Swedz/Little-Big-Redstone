#version 330

#moj_import <minecraft:light.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:sample_lightmap.glsl>

uniform sampler2D Sampler2;

in vec3 Position;
in vec2 UV0;
in ivec2 UV2;
in vec4 Color;
in vec3 Normal;

out vec4 lightMapColor;
out vec2 texCoord0;
out vec4 vertexColor;

void main()
{
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
}
#version 330

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

#ifdef USE_LIGHTMAP
#moj_import <minecraft:light.glsl>
#moj_import <minecraft:sample_lightmap.glsl>

uniform sampler2D Sampler2;
#endif

in vec3 Position;
in vec2 UV0;
in vec4 Color;

#ifdef USE_LIGHTMAP
in ivec2 UV2;
in vec3 Normal;
#endif

#ifdef USE_LIGHTMAP
out vec4 lightMapColor;
#endif

out vec2 texCoord0;
out vec4 vertexColor;

void main()
{
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

#ifdef USE_LIGHTMAP
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = sample_lightmap(Sampler2, UV2);
#else
    vertexColor = Color;
#endif

    texCoord0 = UV0;
}
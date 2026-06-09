#version 330

#moj_import <minecraft:globals.glsl>

float logic_scanline(vec2 texCoord0, ivec2 textureSize)
{
    vec2 motion = vec2(0, 5);
    motion *= vec2(1024, 512) / textureSize;
    vec2 translation = vec2(GameTime) * motion;
    vec2 coord = (texCoord0 + translation) * textureSize;
    float lineScale = 3;
    float minAlpha = 0.9;
    float intensity = floor(sin(coord.y * lineScale) / lineScale) + 1 + minAlpha;
    return clamp(intensity, 0, 1);
}
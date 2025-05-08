package net.swedz.little_big_redstone.helper.guigraphics;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record TextureShaderConfiguration(
		Supplier<ShaderInstance> shader, VertexFormat.Mode mode, VertexFormat format,
		Consumer<ShaderInstance> extraSetup
)
{
	public static final TextureShaderConfiguration DEFAULT = new TextureShaderConfiguration(GameRenderer::getPositionTexColorShader, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, null);
}

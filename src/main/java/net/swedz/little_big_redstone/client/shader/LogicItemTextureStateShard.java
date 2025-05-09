package net.swedz.little_big_redstone.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;

public final class LogicItemTextureStateShard extends RenderStateShard.TextureStateShard
{
	private final ResourceLocation scanline;
	
	public LogicItemTextureStateShard(ResourceLocation texture, ResourceLocation scanline, boolean blur, boolean mipmap)
	{
		super(texture, blur, mipmap);
		this.scanline = scanline;
	}
	
	@Override
	public void setupRenderState()
	{
		super.setupRenderState();
		
		RenderSystem.setShaderTexture(3, scanline);
	}
}

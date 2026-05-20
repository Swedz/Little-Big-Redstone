package net.swedz.little_big_redstone.client.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.swedz.little_big_redstone.LBRClientRenderTypes;

public final class TintedItemBufferSource implements MultiBufferSource
{
	private final MultiBufferSource source;
	
	private final int color;
	
	public TintedItemBufferSource(MultiBufferSource source, int color)
	{
		this.source = source;
		this.color = color;
	}
	
	@Override
	public VertexConsumer getBuffer(RenderType type)
	{
		if(type.format() == DefaultVertexFormat.NEW_ENTITY)
		{
			return source.getBuffer(LBRClientRenderTypes.tintedItem(type, color));
		}
		return source.getBuffer(type);
	}
}

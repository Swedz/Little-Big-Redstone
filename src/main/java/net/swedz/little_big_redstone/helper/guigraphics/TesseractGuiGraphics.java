package net.swedz.little_big_redstone.helper.guigraphics;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.swedz.tesseract.neoforge.api.Assert;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;

public final class TesseractGuiGraphics implements BlitGuiGraphics, FillGuiGraphics, StringGuiGraphics
{
	private final GuiGraphics vanilla;
	
	private boolean                                 batching;
	private List<ResourceLocation>                  batchOrder = Lists.newArrayList();
	private Map<ResourceLocation, List<DrawAction>> batches    = Maps.newHashMap();
	
	private int[] color = new int[]{255, 255, 255, 255};
	
	private ResourceLocation texture = MissingTextureAtlasSprite.getLocation();
	
	private Font font = Minecraft.getInstance().font;
	
	public TesseractGuiGraphics(GuiGraphics vanilla)
	{
		this.vanilla = vanilla;
	}
	
	@Deprecated
	public GuiGraphics vanilla()
	{
		return vanilla;
	}
	
	public PoseStack pose()
	{
		return vanilla.pose();
	}
	
	public void enableBatching()
	{
		batching = true;
	}
	
	public void disableBatching()
	{
		batching = false;
		batchOrder = Lists.newArrayList();
		batches = Maps.newHashMap();
	}
	
	public void drawBatches()
	{
		Assert.that(batching, "Batching has not been enabled");
		
		for(var texture : batchOrder)
		{
			var batch = GuiGraphicsBatch.start(vanilla, texture);
			var draws = batches.get(texture);
			for(var draw : draws)
			{
				draw.addVertexes(batch);
			}
			batch.end();
		}
		
		this.disableBatching();
	}
	
	@Override
	public ResourceLocation getTexture()
	{
		return texture;
	}
	
	@Override
	public void setTexture(ResourceLocation texture)
	{
		if(texture == null)
		{
			texture = MissingTextureAtlasSprite.getLocation();
		}
		this.texture = texture;
	}
	
	@Override
	public int[] getColor()
	{
		return color;
	}
	
	@Override
	public void setColor(int red, int green, int blue, int alpha)
	{
		color = new int[]{red, green, blue, alpha};
	}
	
	@Override
	public Font getFont()
	{
		return font;
	}
	
	@Override
	public void setFont(Font font)
	{
		if(font == null)
		{
			font = Minecraft.getInstance().font;
		}
		this.font = font;
	}
	
	@ApiStatus.Internal
	@Override
	public void innerBlit(int x1, int x2, int y1, int y2, int blitOffset, float minU, float maxU, float minV, float maxV)
	{
		int red = color[0];
		int green = color[1];
		int blue = color[2];
		int alpha = color[3];
		
		final DrawAction draw = (pose, buffer) ->
		{
			buffer.addVertex(pose, (float) x1, (float) y1, (float) blitOffset).setUv(minU, minV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x1, (float) y2, (float) blitOffset).setUv(minU, maxV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x2, (float) y2, (float) blitOffset).setUv(maxU, maxV).setColor(red, green, blue, alpha);
			buffer.addVertex(pose, (float) x2, (float) y1, (float) blitOffset).setUv(maxU, minV).setColor(red, green, blue, alpha);
		};
		
		if(batching)
		{
			batches.compute(texture, (__, draws) ->
			{
				if(draws == null)
				{
					draws = Lists.newArrayList();
					batchOrder.add(texture);
				}
				draws.add(draw);
				return draws;
			});
		}
		else
		{
			var batch = GuiGraphicsBatch.start(vanilla, texture);
			draw.addVertexes(batch);
			batch.end();
		}
	}
	
	@Override
	public void fill(RenderType renderType, int minX, int minY, int maxX, int maxY, int z)
	{
		vanilla.fill(renderType, minX, minY, maxX, maxY, z, this.getColorARGB());
	}
	
	@Override
	public int drawString(String text, float x, float y, boolean dropShadow)
	{
		return vanilla.drawString(font, text, x, y, this.getColorARGB(), dropShadow);
	}
	
	@Override
	public int drawString(FormattedCharSequence text, float x, float y, boolean dropShadow)
	{
		return vanilla.drawString(font, text, x, y, this.getColorARGB(), dropShadow);
	}
}

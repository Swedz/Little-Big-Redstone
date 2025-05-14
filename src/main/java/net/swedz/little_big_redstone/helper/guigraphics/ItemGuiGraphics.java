package net.swedz.little_big_redstone.helper.guigraphics;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ItemGuiGraphics extends WrappedGuiGraphics, TextGuiGraphics, ColoredGuiGraphics
{
	default void renderItem(ItemStack stack, int x, int y)
	{
		this.renderItem(stack, ItemDisplayContext.GUI, x, y);
	}
	
	default void renderItem(ItemStack stack, int x, int y, int guiOffset)
	{
		this.renderItem(null, null, stack, x, y, guiOffset);
	}
	
	default void renderItem(Level level, LivingEntity entity, ItemStack stack, int x, int y, int guiOffset)
	{
		this.renderItem(level, entity, stack, ItemDisplayContext.GUI, x, y, guiOffset);
	}
	
	default void renderItem(ItemStack stack, ItemDisplayContext displayContext, int x, int y)
	{
		this.renderItem(stack, displayContext, x, y, 0);
	}
	
	default void renderItem(ItemStack stack, ItemDisplayContext displayContext, int x, int y, int guiOffset)
	{
		this.renderItem(null, null, stack, displayContext, x, y, guiOffset);
	}
	
	default void renderItem(Level level, LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, int x, int y)
	{
		this.renderItem(level, entity, stack, displayContext, x, y, 0);
	}
	
	default void renderItem(Level level, LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, int x, int y, int guiOffset)
	{
		if(stack.isEmpty())
		{
			return;
		}
		
		float[] oldColor = RenderSystem.getShaderColor();
		float oldRed = oldColor[0];
		float oldGreen = oldColor[1];
		float oldBlue = oldColor[2];
		float oldAlpha = oldColor[3];
		int[] color = this.getColor();
		this.internal().setColor(color[0] / 255f, color[1] / 255f, color[2] / 255f, color[3] / 255f);
		
		var model = Minecraft.getInstance().getItemRenderer().getModel(stack, level, entity, 0);
		
		this.internal().pose().pushPose();
		this.internal().pose().translate(x + 8, y + 8, 150 + (model.isGui3d() ? guiOffset : 0));
		this.internal().pose().scale(16, -16, 16);
		
		if(!model.usesBlockLight())
		{
			Lighting.setupForFlatItems();
		}
		
		Minecraft.getInstance().getItemRenderer().render(stack, displayContext, false, this.internal().pose(), this.internal().bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
		this.internal().flush();
		
		if(!model.usesBlockLight())
		{
			Lighting.setupFor3DItems();
		}
		
		this.internal().pose().popPose();
		
		this.internal().setColor(oldRed, oldGreen, oldBlue, oldAlpha);
	}
	
	default void renderItemDecorations(ItemStack stack, int x, int y)
	{
		this.renderItemDecorations(stack, x, y, null);
	}
	
	default void renderItemDecorations(ItemStack stack, int x, int y, String text)
	{
		float[] oldColor = RenderSystem.getShaderColor();
		float oldRed = oldColor[0];
		float oldGreen = oldColor[1];
		float oldBlue = oldColor[2];
		float oldAlpha = oldColor[3];
		int[] color = this.getColor();
		this.internal().setColor(color[0] / 255f, color[1] / 255f, color[2] / 255f, color[3] / 255f);
		
		this.internal().renderItemDecorations(this.getFont(), stack, x, y, text);
		
		this.internal().setColor(oldRed, oldGreen, oldBlue, oldAlpha);
	}
}

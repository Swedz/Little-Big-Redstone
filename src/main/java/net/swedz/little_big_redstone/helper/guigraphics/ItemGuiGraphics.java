package net.swedz.little_big_redstone.helper.guigraphics;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ItemGuiGraphics
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
	
	void renderItem(Level level, LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, int x, int y, int guiOffset);
}

package net.swedz.little_big_redstone.gui.logicarray;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.swedz.little_big_redstone.LBR;

public final class LogicArrayScreen extends AbstractContainerScreen<LogicArrayMenu> implements MenuAccess<LogicArrayMenu>
{
	private static final ResourceLocation BACKGROUND = LBR.id("textures/gui/container/logic_array/inventory_background.png");
	
	public LogicArrayScreen(LogicArrayMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageHeight = 186;
		inventoryLabelY = imageHeight - 94;
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(graphics, mouseX, mouseY, partialTick);
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}

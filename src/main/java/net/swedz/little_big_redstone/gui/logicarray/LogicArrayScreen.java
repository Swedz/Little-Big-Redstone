package net.swedz.little_big_redstone.gui.logicarray;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArrayPlayerSlot;

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
	protected void renderSlot(GuiGraphics graphics, Slot slot)
	{
		if(slot instanceof LogicArrayPlayerSlot && slot.getSlotIndex() == menu.getLogicArraySlot())
		{
			graphics.pose().pushPose();
			graphics.pose().translate(0, 0, 100);
			
			graphics.blit(LBR.id("textures/gui/slot_atlas.png"), slot.x - 1, slot.y - 1, 0, 18, 18, 18);
			
			this.renderSlotContents(graphics, slot.getItem(), slot, null);
			
			graphics.pose().popPose();
			
			return;
		}
		
		super.renderSlot(graphics, slot);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		graphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}

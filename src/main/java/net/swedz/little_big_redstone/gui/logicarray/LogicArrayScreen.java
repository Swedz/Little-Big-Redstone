package net.swedz.little_big_redstone.gui.logicarray;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.gui.slot.MaybeLockedPlayerSlot;

public final class LogicArrayScreen extends AbstractContainerScreen<LogicArrayMenu> implements MenuAccess<LogicArrayMenu>
{
	private static final Identifier BACKGROUND = LBR.id("textures/gui/container/logic_array/inventory_background.png");
	
	public LogicArrayScreen(LogicArrayMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, 176, 186);
		
		inventoryLabelY = imageHeight - 94;
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		super.extractRenderState(graphics, mouseX, mouseY, partialTick);
		this.extractTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY)
	{
		if(slot instanceof MaybeLockedPlayerSlot && slot.getSlotIndex() == menu.getLogicArraySlot())
		{
			graphics.pose().pushMatrix();
			
			graphics.blit(RenderPipelines.GUI_TEXTURED, LBR.id("textures/gui/slot_atlas.png"), slot.x - 1, slot.y - 1, 0, 18, 18, 18, 256, 256);
			
			this.renderSlotContents(graphics, slot.getItem(), slot, null);
			
			graphics.pose().popMatrix();
			
			return;
		}
		
		super.extractSlot(graphics, slot, mouseX, mouseY);
	}
	
	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick)
	{
		graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
	}
}

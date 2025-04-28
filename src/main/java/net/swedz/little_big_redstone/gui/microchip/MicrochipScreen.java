package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	private static final ResourceLocation INVENTORY_BACKGROUND = LBR.id("textures/gui/container/microchip/inventory_background.png");
	
	private MicrochipWidget microchipWidget;
	
	public MicrochipScreen(MicrochipMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 256;
		imageHeight = 232;
	}
	
	public void handleUpdate()
	{
		microchipWidget.handleUpdate();
	}
	
	private boolean isWithinBoard(int x, int y)
	{
		return menu.microchip().size().bounds().contains(x, y);
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		this.addRenderableWidget(microchipWidget = new MicrochipWidget(leftPos, topPos, this));
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
	{
		super.render(graphics, mouseX, mouseY, partialTick);
		
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	public void renderFloatingItem(GuiGraphics graphics, ItemStack stack, int x, int y, String text)
	{
		var size = menu.microchip().size();
		int mouseX = size.boardX(x + 8);
		int mouseY = size.boardY(y + 8);
		if(stack.has(LBRComponents.LOGIC) && this.isWithinBoard(mouseX, mouseY))
		{
			var component = stack.get(LBRComponents.LOGIC);
			int logicX = component.size().topLeftCornerX(mouseX);
			int logicY = component.size().topLeftCornerY(mouseY);
			var context = LogicRenderer.Context.create(menu.color(), component, true, microchipWidget.hasSelectedPort(), false);
			graphics.pose().pushPose();
			graphics.pose().scale(size.scale(), size.scale(), size.scale());
			LogicRenderers.render(context, graphics, component, logicX, logicY);
			graphics.pose().popPose();
		}
		else
		{
			super.renderFloatingItem(graphics, stack, x, y, text);
		}
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		// Dont render the "Microchip" and "Inventory" labels
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		graphics.blit(INVENTORY_BACKGROUND, leftPos, topPos, 0, 0, 256, 256);
	}
}

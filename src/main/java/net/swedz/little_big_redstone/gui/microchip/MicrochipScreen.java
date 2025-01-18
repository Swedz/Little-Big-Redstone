package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.helper.GuiGraphicsHelper;
import net.swedz.little_big_redstone.microchip.Microchip;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	private static final ResourceLocation SHADOW_HOVER_OVERLAY = LBR.id("textures/gui/container/microchip/shadow_hover_overlay.png");
	private static final ResourceLocation CIRCUIT_BACKGROUND   = LBR.id("textures/gui/container/microchip/circuit_background.png");
	private static final ResourceLocation INVENTORY_BACKGROUND = LBR.id("textures/gui/container/microchip/inventory_background.png");
	
	private final Microchip microchip;
	
	private int boardX, boardY, boardWidth, boardHeight;
	
	public MicrochipScreen(MicrochipMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 256;
		imageHeight = 256 - (12 * 2);
		
		boardX = 0;
		boardY = 0;
		boardWidth = imageWidth;
		boardHeight = imageHeight - 90 - 4;
		
		microchip = menu.microchip();
	}
	
	private int toLocalX(int x)
	{
		return x - leftPos;
	}
	
	private int toLocalY(int y)
	{
		return y - topPos;
	}
	
	private boolean isWithinBoard(int x, int y)
	{
		return x >= boardX && x < boardX + boardWidth &&
			   y >= boardY && y < boardY + boardHeight;
	}
	
	private boolean mouseClickedOnBoard(int mouseX, int mouseY, int button)
	{
		// TODO do stuff!
		LBR.LOGGER.info("clicked on board: {}, {}", mouseX, mouseY);
		return false;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button)
	{
		int localMouseX = this.toLocalX((int) mouseX);
		int localMouseY = this.toLocalY((int) mouseY);
		return this.isWithinBoard(localMouseX, localMouseY) ?
				this.mouseClickedOnBoard(localMouseX, localMouseY, button) :
				super.mouseClicked(mouseX, mouseY, button);
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
		if(stack.has(LBRComponents.LOGIC) && this.isWithinBoard(x + 8, y + 8))
		{
			var logic = stack.get(LBRComponents.LOGIC);
			LogicRenderers.render(graphics, logic, x, y);
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
	
	private void renderShadowBg(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, int padding)
	{
		for(int p = padding; p >= 1; p--)
		{
			graphics.fill(x - p, y - p, x + width + p, y + height + p, 0x40000000);
		}
		
		graphics.enableScissor(x - padding + leftPos, y - padding + topPos, x + width + padding + leftPos, y + height + padding + topPos);
		GuiGraphicsHelper.blit(graphics, SHADOW_HOVER_OVERLAY, this.toLocalX(mouseX) - 64, this.toLocalY(mouseY) - 64, 128, 128, 0, 0, 64, 64, 64, 64, 1, 1, 1, 0.11f);
		graphics.disableScissor();
	}
	
	private void renderCircuitBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		this.renderShadowBg(graphics, mouseX, mouseY, boardX, boardY, boardWidth, boardHeight, 3);
		
		graphics.setColor(1, 0.5f, 0.5f, 1);
		graphics.blit(CIRCUIT_BACKGROUND, boardX, boardY, 0, 0, boardWidth, boardHeight, 64, 64);
		graphics.setColor(1, 1, 1, 1);
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		
		this.renderCircuitBg(graphics, partialTick, mouseX, mouseY);
		
		graphics.blit(INVENTORY_BACKGROUND, 0, imageHeight - 90, 0, 0, 256, 90);
		
		graphics.pose().popPose();
	}
}

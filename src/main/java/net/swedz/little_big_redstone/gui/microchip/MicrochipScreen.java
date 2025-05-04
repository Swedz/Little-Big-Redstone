package net.swedz.little_big_redstone.gui.microchip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidget;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	private static final ResourceLocation INVENTORY_BACKGROUND = LBR.id("textures/gui/container/microchip/inventory_background.png");
	
	public static int getGridSnappedCoord(int coord)
	{
		return (coord / 16) * 16;
	}
	
	private MicrochipWidget microchipWidget;
	
	private float partialTick;
	
	public MicrochipScreen(MicrochipMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
		
		imageWidth = 256;
		imageHeight = 227;
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
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	public void renderFloatingItem(GuiGraphics vanilla, ItemStack stack, int x, int y, String text)
	{
		var size = menu.microchip().size();
		int mouseX = size.boardX(x + 8);
		int mouseY = size.boardY(y + 8);
		
		if(this.isWithinBoard(mouseX, mouseY))
		{
			if(stack.has(LBRComponents.LOGIC))
			{
				var component = stack.get(LBRComponents.LOGIC);
				int logicX = Screen.hasControlDown() ? getGridSnappedCoord(mouseX - size.bounds().minX() - component.size().centerX() + 8) + size.boardX(8) : component.size().topLeftCornerX(mouseX);
				int logicY = Screen.hasControlDown() ? getGridSnappedCoord(mouseY - size.bounds().minY() - component.size().centerY() + 8) + size.boardY(8) : component.size().topLeftCornerY(mouseY);
				var context = LogicRenderer.Context.create(menu.color(), component, true, microchipWidget.hasSelectedPort(), false);
				
				vanilla.pose().pushPose();
				vanilla.pose().scale(size.scale(), size.scale(), size.scale());
				var graphics = new TesseractGuiGraphics(vanilla);
				graphics.enableBatching();
				LogicRenderers.render(context, graphics, component, logicX, logicY);
				graphics.drawBatches();
				vanilla.pose().popPose();
				
				return;
			}
			else if(stack.is(LBRItems.REDSTONE_BIT.asItem()))
			{
				vanilla.pose().pushPose();
				vanilla.pose().scale(size.scale(), size.scale(), size.scale());
				if(!microchipWidget.context().hasPort())
				{
					// TODO convert this to a shader?
					float gameTime = ((Minecraft.getInstance().level.getGameTime() % 24000L) + partialTick) / 24000f;
					float interval = 30;
					float t = Mth.frac(gameTime * (24000f / interval));
					float wave = (float) ((Math.sin(t * 6.28318f) + 1) / 2f);
					float alpha = 0.25f * (1 - wave) + 0.5f * wave;
					vanilla.setColor(1, 1, 1, alpha);
				}
				super.renderFloatingItem(vanilla, stack, mouseX - 8, mouseY - 8, text);
				vanilla.setColor(1, 1, 1, 1);
				vanilla.pose().popPose();
				return;
			}
		}
		
		super.renderFloatingItem(vanilla, stack, x, y, text);
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		// Dont render the "Microchip" and "Inventory" labels
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY)
	{
		this.partialTick = partialTick;
		
		graphics.blit(INVENTORY_BACKGROUND, leftPos, topPos, 0, 0, 256, 256);
	}
}

package net.swedz.little_big_redstone.gui.microchip;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientShaders;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItemDisplayContext;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArrayPlayerSlot;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidget;
import net.swedz.little_big_redstone.gui.microchip.wire.WireEndpoints;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.object.logic.LogicComponent;
import net.swedz.tesseract.neoforge.api.Bounds;
import net.swedz.tesseract.neoforge.helper.guigraphics.TesseractGuiGraphics;

import java.util.List;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	private static final ResourceLocation MICROCHIP_BACKGROUND   = LBR.id("textures/gui/container/microchip/inventory_background.png");
	private static final ResourceLocation LOGIC_ARRAY_BACKGROUND = LBR.id("textures/gui/container/logic_array/microchip_inventory_background.png");
	
	public static int getGridSnappedCoord(int coord)
	{
		return (coord / 16) * 16;
	}
	
	private MicrochipWidget microchipWidget;
	
	private float partialTicks;
	
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
		
		this.addRenderableWidget(microchipWidget = new MicrochipWidget(leftPos + 8, topPos + 8, this, microchipWidget));
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
	{
		boolean drag = this.getFocused() != null && this.isDragging() && button == InputConstants.MOUSE_BUTTON_LEFT && this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY);
		return drag || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	protected void renderSlot(GuiGraphics vanilla, Slot slot)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		if(slot instanceof LogicArrayPlayerSlot && slot.index == menu.getLogicArrayItemHandler().getSelectedSlot())
		{
			graphics.pose().pushPose();
			graphics.pose().translate(0, 0, 100);
			
			graphics.setTexture(LBR.id("textures/gui/slot_atlas.png"));
			graphics.blit(slot.x - 1, slot.y - 1, 0, 18, 18, 18);
			
			this.renderSlotContents(vanilla, slot.getItem(), slot, null);
			
			graphics.pose().popPose();
			
			return;
		}
		
		super.renderSlot(vanilla, slot);
	}
	
	@Override
	public void renderFloatingItem(GuiGraphics vanilla, ItemStack stack, int localX, int localY, String text)
	{
		int mouseX = localX + leftPos + 8;
		int mouseY = localY + topPos + 8;
		
		if(microchipWidget.isMouseOver(mouseX, mouseY))
		{
			var microchip = menu.microchip();
			var size = microchip.size();
			int boardMouseX = size.boardCoord(localX, microchipWidget.zoom(), microchipWidget.offsetX());
			int boardMouseY = size.boardCoord(localY, microchipWidget.zoom(), microchipWidget.offsetY());
			
			var graphics = new TesseractGuiGraphics(vanilla);
			
			vanilla.enableScissor(microchipWidget.x(), microchipWidget.y(), microchipWidget.x() + MicrochipBlockEntity.CIRCUIT_BOUNDS.width(), microchipWidget.y() + MicrochipBlockEntity.CIRCUIT_BOUNDS.height());
			graphics.pose().pushPose();
			graphics.pose().translate(8, 8, 0);
			graphics.pose().scale(size.scale(), size.scale(), 0);
			graphics.pose().scale(microchipWidget.zoom(), microchipWidget.zoom(), 0);
			graphics.pose().translate(-microchipWidget.offsetX(), -microchipWidget.offsetY(), 0);
			
			if(stack.getItem() instanceof StickyNoteItem)
			{
				graphics.pose().translate(0, 0, 232);
				
				int itemX = Screen.hasControlDown() ? getGridSnappedCoord(boardMouseX) : (boardMouseX - 8);
				int itemY = Screen.hasControlDown() ? getGridSnappedCoord(boardMouseY) : (boardMouseY - 8);
				
				graphics.renderItem(stack, LBRItemDisplayContext.MICROCHIP_GUI, itemX, itemY);
				
				graphics.pose().popPose();
				vanilla.disableScissor();
				return;
			}
			else if(stack.has(LBRComponents.LOGIC))
			{
				var component = stack.get(LBRComponents.LOGIC);
				var context = LogicRenderer.Context.create(menu.color(), component, menu.getCarriedWires() != null, microchipWidget.hasSelectedPort(), false);
				
				int logicX = Screen.hasControlDown() ? getGridSnappedCoord(component.size().topLeftCornerX(boardMouseX) + 8) : component.size().topLeftCornerX(boardMouseX);
				int logicY = Screen.hasControlDown() ? getGridSnappedCoord(component.size().topLeftCornerY(boardMouseY) + 8) : component.size().topLeftCornerY(boardMouseY);
				
				this.renderCarriedWires(graphics, logicX, logicY, context, component);
				this.renderCarriedLogic(graphics, logicX, logicY, context, component);
				
				graphics.pose().popPose();
				vanilla.disableScissor();
				return;
			}
			else if(stack.is(LBRItems.REDSTONE_BIT.asItem()))
			{
				graphics.pose().translate(0, 0, 232);
				
				graphics.setTexture(LBR.id("textures/item/redstone_bit.png"));
				if(!microchipWidget.context().hasPort())
				{
					graphics.setTextureShader(LBRClientShaders::pulsingTextureAlpha);
				}
				graphics.blit(boardMouseX - 8, boardMouseY - 8, 0, 0, 16, 16, 16, 16);
				graphics.renderItemDecorations(stack, boardMouseX - 8, boardMouseY - 8);
				
				graphics.pose().popPose();
				vanilla.disableScissor();
				return;
			}
			
			graphics.pose().popPose();
			vanilla.disableScissor();
		}
		
		super.renderFloatingItem(vanilla, stack, localX, localY, text);
	}
	
	private void renderCarriedWires(TesseractGuiGraphics graphics, int logicX, int logicY, LogicRenderer.Context context, LogicComponent<?, ?> component)
	{
		if(menu.getCarriedWires() != null)
		{
			var microchip = menu.microchip();
			var size = microchip.size();
			
			graphics.pose().pushPose();
			graphics.enableBatching();
			
			for(var wire : menu.getCarriedWires())
			{
				var endpoints = WireEndpoints.carried(microchipWidget.context(), menu.getCarriedComponentSlot(), component, wire, logicX, logicY);
				List<Bounds> avoidBounds = List.of(microchipWidget.panel().wires().pathing().mutateComponentBounds(component.size().toBounds(logicX, logicY)));
				
				microchipWidget.panel().wires().renderWire(graphics, avoidBounds, endpoints, true);
			}
			
			graphics.drawBatches();
			graphics.pose().popPose();
		}
	}
	
	private void renderCarriedLogic(TesseractGuiGraphics graphics, int logicX, int logicY, LogicRenderer.Context context, LogicComponent<?, ?> component)
	{
		var microchip = menu.microchip();
		var size = microchip.size();
		
		graphics.pose().pushPose();
		graphics.enableBatching();
		
		LogicRenderers.render(context, graphics, component, logicX, logicY);
		
		graphics.drawBatches();
		graphics.pose().popPose();
	}
	
	@Override
	protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
	{
		// Dont render the "Microchip" and "Inventory" labels
	}
	
	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
	{
		this.partialTicks = partialTicks;
		
		graphics.pose().pushPose();
		graphics.pose().translate(leftPos, topPos, 0);
		
		graphics.blit(MICROCHIP_BACKGROUND, 0, 0, 0, 0, 256, 256);
		
		if(menu.getLogicArrayItemHandler().shouldDisplay())
		{
			graphics.blit(LOGIC_ARRAY_BACKGROUND, -83, 0, 0, 0, 256, 256);
		}
		
		graphics.pose().popPose();
	}
}

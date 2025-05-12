package net.swedz.little_big_redstone.gui.microchip;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRCreativeTabs;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.api.Bounds;
import net.swedz.little_big_redstone.client.model.logic.LogicBakingModelData;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArrayPlayerSlot;
import net.swedz.little_big_redstone.gui.logicarray.slot.LogicArraySlot;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidget;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidgetWires;
import net.swedz.little_big_redstone.helper.guigraphics.TesseractGuiGraphics;
import net.swedz.little_big_redstone.microchip.logic.LogicComponent;

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
		
		this.addRenderableWidget(microchipWidget = new MicrochipWidget(leftPos, topPos, this));
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.render(graphics, mouseX, mouseY, partialTicks);
		
		this.renderTooltip(graphics, mouseX, mouseY);
	}
	
	@Override
	protected void renderTooltip(GuiGraphics vanilla, int x, int y)
	{
		if(menu.getCarried().isEmpty() && hoveredSlot instanceof LogicArraySlot && menu.getLogicArrayItemHandler().isCreativeMode())
		{
			var items = LBRCreativeTabs.getLogicArrayItems();
			if(hoveredSlot.index < items.size())
			{
				vanilla.pose().pushPose();
				vanilla.renderTooltip(Minecraft.getInstance().font, items.get(hoveredSlot.index), x, y);
				vanilla.pose().popPose();
				return;
			}
		}
		
		super.renderTooltip(vanilla, x, y);
	}
	
	@Override
	protected void renderSlot(GuiGraphics vanilla, Slot slot)
	{
		var graphics = new TesseractGuiGraphics(vanilla);
		
		if(slot instanceof LogicArrayPlayerSlot && slot.index == menu.getLogicArrayItemHandler().getSelectedSlot())
		{
			graphics.pose().pushPose();
			graphics.pose().translate(0, 0, 100);
			
			graphics.setTexture(LBR.id("textures/gui/container/logic_array/slot_atlas.png"));
			graphics.blit(slot.x - 1, slot.y - 1, 18, 0, 18, 18);
			
			this.renderSlotContents(vanilla, slot.getItem(), slot, null);
			
			graphics.pose().popPose();
			
			return;
		}
		else if(slot instanceof LogicArraySlot && menu.getLogicArrayItemHandler().isCreativeMode())
		{
			var items = LBRCreativeTabs.getLogicArrayItems();
			if(slot.index < items.size())
			{
				graphics.pose().pushPose();
				graphics.pose().translate(0, 0, 100);
				
				vanilla.renderItem(items.get(slot.index), slot.x, slot.y);
				
				graphics.pose().popPose();
			}
			return;
		}
		
		super.renderSlot(vanilla, slot);
	}
	
	@Override
	public void renderFloatingItem(GuiGraphics vanilla, ItemStack stack, int x, int y, String text)
	{
		var microchip = menu.microchip();
		var size = microchip.size();
		int mouseX = size.boardX(x + 8);
		int mouseY = size.boardY(y + 8);
		
		if(this.isWithinBoard(mouseX, mouseY))
		{
			if(stack.has(LBRComponents.LOGIC))
			{
				var component = stack.get(LBRComponents.LOGIC);
				var context = LogicRenderer.Context.create(menu.color(), component, menu.getCarriedWires() != null, microchipWidget.hasSelectedPort(), false);
				
				var graphics = new TesseractGuiGraphics(vanilla);
				
				graphics.pose().pushPose();
				graphics.pose().scale(size.scale(), size.scale(), size.scale());
				
				this.renderCarriedWires(graphics, mouseX, mouseY, context, component);
				this.renderCarriedLogic(graphics, mouseX, mouseY, context, component);
				
				graphics.pose().popPose();
				
				return;
			}
			else if(stack.is(LBRItems.REDSTONE_BIT.asItem()))
			{
				vanilla.pose().pushPose();
				vanilla.pose().scale(size.scale(), size.scale(), size.scale());
				if(!microchipWidget.context().hasPort())
				{
					// TODO convert this to a shader?
					float gameTime = ((Minecraft.getInstance().level.getGameTime() % 24000L) + partialTicks) / 24000f;
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
	
	private void renderCarriedWires(TesseractGuiGraphics graphics, int mouseX, int mouseY, LogicRenderer.Context context, LogicComponent<?, ?> component)
	{
		if(menu.getCarriedWires() != null)
		{
			var microchip = menu.microchip();
			var size = microchip.size();
			
			int logicX = Screen.hasControlDown() ? getGridSnappedCoord(mouseX - size.bounds().minX() - component.size().centerX() + 8) : (component.size().topLeftCornerX(mouseX) - size.boardX(8));
			int logicY = Screen.hasControlDown() ? getGridSnappedCoord(mouseY - size.bounds().minY() - component.size().centerY() + 8) : (component.size().topLeftCornerY(mouseY) - size.boardX(8));
			
			graphics.pose().pushPose();
			graphics.pose().translate(size.boardX(8), size.boardY(8), 0);
			graphics.enableBatching();
			
			for(var wire : menu.getCarriedWires())
			{
				boolean isOutput = wire.output().slot() == menu.getCarriedComponentSlot();
				var outputLogic = isOutput ? null : microchip.components().get(wire.output().slot());
				var outputLogicComponent = isOutput ? component : outputLogic.component();
				int outputX = isOutput ? logicX : outputLogic.x();
				int outputY = isOutput ? logicY : outputLogic.y();
				
				boolean isInput = wire.input().slot() == menu.getCarriedComponentSlot();
				var inputLogic = isInput ? null : microchip.components().get(wire.input().slot());
				var inputLogicComponent = isInput ? component : inputLogic.component();
				int inputX = isInput ? logicX : inputLogic.x();
				int inputY = isInput ? logicY : inputLogic.y();
				
				int startX = MicrochipWidgetWires.getWireStartX(outputX, outputLogicComponent);
				int startY = MicrochipWidgetWires.getWireStartY(outputY, outputLogicComponent, wire.output().index());
				int endX = MicrochipWidgetWires.getWireEndX(inputX);
				int endY = MicrochipWidgetWires.getWireEndY(inputY, inputLogicComponent, wire.input().index());
				
				boolean powered = outputLogic != null && outputLogicComponent.output(wire.output().index());
				
				int argb = LogicBakingModelData.get(component).getColorSet(component.color().orElse(menu.color())).foreground();
				
				List<Bounds> avoidBounds = List.of(microchipWidget.wireRenderer().pathing().mutateComponentBounds(component.size().toBounds(logicX, logicY)));
				
				microchipWidget.wireRenderer().renderWire(graphics, Either.right(avoidBounds), true, startX, startY, endX, endY, true, powered, argb, partialTicks);
			}
			
			graphics.drawBatches();
			graphics.pose().popPose();
		}
	}
	
	private void renderCarriedLogic(TesseractGuiGraphics graphics, int mouseX, int mouseY, LogicRenderer.Context context, LogicComponent<?, ?> component)
	{
		var microchip = menu.microchip();
		var size = microchip.size();
		
		int logicX = Screen.hasControlDown() ? getGridSnappedCoord(mouseX - size.bounds().minX() - component.size().centerX() + 8) + size.boardX(8) : component.size().topLeftCornerX(mouseX);
		int logicY = Screen.hasControlDown() ? getGridSnappedCoord(mouseY - size.bounds().minY() - component.size().centerY() + 8) + size.boardY(8) : component.size().topLeftCornerY(mouseY);
		
		graphics.enableBatching();
		
		LogicRenderers.render(context, graphics, component, logicX, logicY);
		
		graphics.drawBatches();
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
		
		graphics.blit(MICROCHIP_BACKGROUND, leftPos, topPos, 0, 0, 256, 256);
		
		if(menu.getLogicArrayItemHandler().shouldDisplay())
		{
			graphics.blit(LOGIC_ARRAY_BACKGROUND, leftPos - 83, topPos, 0, 0, 256, 256);
		}
	}
}

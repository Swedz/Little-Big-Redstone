package net.swedz.little_big_redstone.gui.microchip;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.swedz.little_big_redstone.LBR;
import net.swedz.little_big_redstone.LBRClientRenderPipelines;
import net.swedz.little_big_redstone.LBRComponents;
import net.swedz.little_big_redstone.LBRItemDisplayContext;
import net.swedz.little_big_redstone.LBRItems;
import net.swedz.little_big_redstone.block.microchip.MicrochipBlockEntity;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderer;
import net.swedz.little_big_redstone.gui.microchip.logic.LogicRenderers;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipThermometerWidget;
import net.swedz.little_big_redstone.gui.microchip.widget.MicrochipWidget;
import net.swedz.little_big_redstone.gui.microchip.wire.WireMetadata;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePath;
import net.swedz.little_big_redstone.gui.microchip.wire.WirePathKey;
import net.swedz.little_big_redstone.gui.microchip.wire.render.WiresGuiElementRenderState;
import net.swedz.little_big_redstone.gui.microchip.wire.render.WiresRenderState;
import net.swedz.little_big_redstone.gui.slot.MaybeLockedPlayerSlot;
import net.swedz.little_big_redstone.item.LogicItem;
import net.swedz.little_big_redstone.item.stickynote.StickyNoteItem;
import net.swedz.little_big_redstone.microchip.object.logic.config.LogicConfig;
import net.swedz.little_big_redstone.microchip.wire.Wire;
import net.swedz.little_big_redstone.network.packet.StoreMicrochipViewPositionPacket;
import org.joml.Matrix3x2f;

import java.util.Map;
import java.util.Optional;

public final class MicrochipScreen extends AbstractContainerScreen<MicrochipMenu>
{
	private static final Identifier MICROCHIP_BACKGROUND   = LBR.id("textures/gui/container/microchip/inventory_background.png");
	private static final Identifier LOGIC_ARRAY_BACKGROUND = LBR.id("textures/gui/container/logic_array/microchip_inventory_background.png");
	
	public static int getGridSnappedCoord(int coord)
	{
		return (coord / 16) * 16;
	}
	
	private MicrochipWidget            microchipWidget;
	private MicrochipThermometerWidget thermostatWidget;
	
	private float partialTicks;
	
	public MicrochipScreen(
			MicrochipMenu menu,
			Inventory playerInventory,
			Component title
	)
	{
		super(menu, playerInventory, title, 256, 227);
	}
	
	public void handleUpdate(boolean rerouteWires)
	{
		microchipWidget.handleUpdate(rerouteWires);
	}
	
	private boolean isWithinBoard(int x, int y)
	{
		return menu.microchip().size().bounds().contains(x, y);
	}
	
	@Override
	protected void init()
	{
		super.init();
		
		this.addRenderableWidget(microchipWidget = new MicrochipWidget(leftPos + 8, topPos + 8, this, menu.viewPosition()));
		
		this.addRenderableWidget(thermostatWidget = new MicrochipThermometerWidget(leftPos, topPos, this));
	}
	
	public MicrochipWidget getMicrochipWidget()
	{
		return microchipWidget;
	}
	
	public MicrochipThermometerWidget getThermostatWidget()
	{
		return thermostatWidget;
	}
	
	@Override
	protected void containerTick()
	{
		thermostatWidget.tick();
	}
	
	@Override
	public void removed()
	{
		new StoreMicrochipViewPositionPacket(menu.blockPos(), menu.viewPosition()).sendToServer();
		
		super.removed();
	}
	
	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY)
	{
		boolean drag = this.getFocused() != null &&
					   this.isDragging() &&
					   (event.button() == InputConstants.MOUSE_BUTTON_LEFT || event.button() == InputConstants.MOUSE_BUTTON_MIDDLE) &&
					   this.getFocused().mouseDragged(event, dragX, dragY);
		return drag || super.mouseDragged(event, dragX, dragY);
	}
	
	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
	{
		super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
		
		this.extractTooltip(graphics, mouseX, mouseY);
		microchipWidget.renderTooltip(graphics);
	}
	
	@Override
	protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY)
	{
		if(slot instanceof MaybeLockedPlayerSlot && slot.index == menu.getLogicArrayItemHandler().getSelectedSlot())
		{
			graphics.blit(LBR.id("textures/gui/slot_atlas.png"), slot.x - 1, slot.y - 1, 0, 18, 18, 18);
			
			this.renderSlotContents(graphics, slot.getItem(), slot, null);
			return;
		}
		
		super.extractSlot(graphics, slot, mouseX, mouseY);
	}
	
	@Override
	public void extractFloatingItem(GuiGraphicsExtractor graphics, ItemStack carried, int localX, int localY, String itemCount)
	{
		int mouseX = localX + 8;
		int mouseY = localY + 8;
		
		if(microchipWidget.isMouseOver(mouseX, mouseY))
		{
			var microchip = menu.microchip();
			var size = microchip.size();
			int boardMouseX = size.boardCoord(localX - leftPos, microchipWidget.viewPosition().zoom(), microchipWidget.viewPosition().x());
			int boardMouseY = size.boardCoord(localY - topPos, microchipWidget.viewPosition().zoom(), microchipWidget.viewPosition().y());
			
			graphics.enableScissor(microchipWidget.x(), microchipWidget.y(), microchipWidget.x() + MicrochipBlockEntity.CIRCUIT_BOUNDS.width(), microchipWidget.y() + MicrochipBlockEntity.CIRCUIT_BOUNDS.height());
			graphics.pose().pushMatrix();
			graphics.pose().translate(leftPos + 8, topPos + 8);
			graphics.pose().scale(size.scale(), size.scale());
			graphics.pose().scale(microchipWidget.viewPosition().zoom(), microchipWidget.viewPosition().zoom());
			graphics.pose().translate((int) -microchipWidget.viewPosition().x(), (int) -microchipWidget.viewPosition().y());
			
			if(StickyNoteItem.hasRelevantComponents(carried))
			{
				int itemX = minecraft.hasControlDown() ? getGridSnappedCoord(boardMouseX) : (boardMouseX - 8);
				int itemY = minecraft.hasControlDown() ? getGridSnappedCoord(boardMouseY) : (boardMouseY - 8);
				
				graphics.item(carried, LBRItemDisplayContext.MICROCHIP_GUI, itemX, itemY);
				
				graphics.pose().popMatrix();
				graphics.disableScissor();
				return;
			}
			else if(carried.has(LBRComponents.LOGIC_CONFIG))
			{
				var logicColor = LogicItem.getColor(carried);
				var config = carried.get(LBRComponents.LOGIC_CONFIG);
				var context = LogicRenderer.Context.create(logicColor, menu.color(), config.type(), menu.getCarriedWires() != null, microchipWidget.hasSelectedPort(), false);
				
				int logicX = minecraft.hasControlDown() ? getGridSnappedCoord(config.size().topLeftCornerX(boardMouseX) + 8) : config.size().topLeftCornerX(boardMouseX);
				int logicY = minecraft.hasControlDown() ? getGridSnappedCoord(config.size().topLeftCornerY(boardMouseY) + 8) : config.size().topLeftCornerY(boardMouseY);
				
				this.renderCarriedWires(graphics, logicX, logicY, context, config, logicColor);
				this.renderCarriedLogic(graphics, logicX, logicY, context, config, logicColor);
				
				graphics.pose().popMatrix();
				graphics.disableScissor();
				return;
			}
			else if(carried.is(LBRItems.REDSTONE_BIT.asItem()))
			{
				var pipeline = RenderPipelines.GUI_TEXTURED;
				if(!microchipWidget.context().hasPort())
				{
					pipeline = LBRClientRenderPipelines.PULSING_TEXTURE_ALPHA;
				}
				graphics.blit(pipeline, LBR.id("textures/item/redstone_bit.png"), boardMouseX - 8, boardMouseY - 8, 0, 0, 16, 16, 16, 16);
				graphics.itemDecorations(font, carried, boardMouseX - 8, boardMouseY - 8);
				
				graphics.pose().popMatrix();
				graphics.disableScissor();
				return;
			}
			
			graphics.pose().popMatrix();
			graphics.disableScissor();
		}
		
		super.extractFloatingItem(graphics, carried, localX, localY, itemCount);
	}
	
	private record CarriedWiresData(WirePathKey key, WirePath path)
	{
	}
	
	private void renderCarriedWires(GuiGraphicsExtractor graphics, int logicX, int logicY, LogicRenderer.Context context, LogicConfig<?> config, Optional<DyeColor> color)
	{
		if(menu.getCarriedWires() != null)
		{
			var wirePanel = microchipWidget.panel().wires();
			
			Map<Wire, CarriedWiresData> paths = Maps.newConcurrentMap();
			for(var wire : menu.getCarriedWires())
			{
				var key = WirePathKey.carried(microchipWidget.context(), menu.getCarriedComponentSlot(), config, wire, logicX, logicY);
				var path = wirePanel.pathing().get(key, false);
				paths.put(wire, new CarriedWiresData(key, path));
			}
			WirePath.blockAllOf(paths.values().stream().map(CarriedWiresData::path).toList());
			
			var renderState = new WiresRenderState();
			for(var entry : paths.entrySet())
			{
				var wire = entry.getKey();
				var data = entry.getValue();
				var metadata = WireMetadata.carried(microchipWidget.microchip(), microchipWidget.color(), menu.getCarriedComponentSlot(), config.type(), color, wire);
				renderState.add(wirePanel.renderWire(data.key(), data.path(), metadata));
			}
			var pose = new Matrix3x2f(graphics.pose());
			var boardBounds = microchipWidget.microchip().size().bounds();
			graphics.submitGuiElementRenderState(new WiresGuiElementRenderState(pose, boardBounds, renderState, false, true));
			graphics.submitGuiElementRenderState(new WiresGuiElementRenderState(pose, boardBounds, renderState, true, true));
		}
	}
	
	private <C extends LogicConfig<C>> void renderCarriedLogic(GuiGraphicsExtractor graphics, int logicX, int logicY, LogicRenderer.Context context, C config, Optional<DyeColor> logicColor)
	{
		var microchip = menu.microchip();
		var size = microchip.size();
		
		graphics.pose().pushMatrix();
		
		LogicRenderers.render(context, graphics, config.type().create(config, logicColor), logicX, logicY);
		
		graphics.pose().popMatrix();
	}
	
	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY)
	{
		// Dont render the "Microchip" and "Inventory" labels
	}
	
	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks)
	{
		this.partialTicks = partialTicks;
		
		graphics.pose().pushMatrix();
		graphics.pose().translate(leftPos, topPos);
		
		graphics.blit(MICROCHIP_BACKGROUND, 0, 0, 0, 0, 256, 256);
		
		if(menu.getLogicArrayItemHandler().shouldDisplay())
		{
			graphics.blit(LOGIC_ARRAY_BACKGROUND, -83, 0, 0, 0, 256, 256);
		}
		
		graphics.pose().popMatrix();
	}
}
